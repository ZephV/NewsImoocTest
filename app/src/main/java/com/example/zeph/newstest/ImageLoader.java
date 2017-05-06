package com.example.zeph.newstest;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.LruCache;
import android.widget.ImageView;
import android.widget.ListView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

public class ImageLoader {

//    private ImageView mImageView;
//    private String mURL;
    private LruCache<String, Bitmap> mCache;// 创建Cache
    private ListView mListView;
    private Set<NewsAsyncTask> mTask;


    public ImageLoader(ListView listView) {
        mListView = listView;
        mTask = new HashSet<>();
        // 获取最大缓存
        int maxCache = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxCache / 4;
        mCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                // 在每次缓存得时候调用
                return value.getByteCount();
            }
        };
    }

    /**
     * 将Bitmap加入缓存
     *
     * @param url    存入缓存的Key
     * @param bitmap 存入缓存的格式
     */
    public void addBitmapToCache(String url, Bitmap bitmap) {
        if (getBitmapFromCache(url) == null) {
            mCache.put(url, bitmap);
        }
    }


    /**
     * 在缓存中取出Bitmap
     *
     * @param url 是取出缓存得Key
     */
    public Bitmap getBitmapFromCache(String url) {
        return mCache.get(url);
    }

//
//    private Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            if (mImageView.getTag().equals(mURL)) {
//                mImageView.setImageBitmap((Bitmap) msg.obj);
//            }
//        }
//    };

//    /**
//     * 多线程方法加载图片
//     * @param imageView
//     * @param url
//     */
//    public void showImageByThread(ImageView imageView, final String url) {
//        mImageView = imageView;
//        mUrl = url;
//
//        new Thread() {
//            @Override
//            public void run() {
//                super.run();
//                Bitmap bitmap = getBitmapFromURL(url);
//                Message message = Message.obtain();
//                message.obj = bitmap;
//                handler.sendMessage(message);
//            }
//        }.start();
//    }


    /**
     * 通过URL从网络中获取Bitmap
     *
     * @param urlString
     * @return
     */
    public Bitmap getBitmapFromURL(String urlString) {
        Bitmap bitmap;
        InputStream is = null;
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            is = new BufferedInputStream(connection.getInputStream());
            bitmap = BitmapFactory.decodeStream(is);
            connection.disconnect();
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    public void showImageByAsyncTask(ImageView imageView, String url) {

        Bitmap bitmap = getBitmapFromCache(url);
        if (bitmap == null) {
            imageView.setImageResource(R.mipmap.ic_launcher);
        } else {
            imageView.setImageBitmap(bitmap);
        }

    }

    public void loadImages(int start, int end) {
        for (int i = start; i < end; i++) {
            String url = NewsAdapter.URLS[i];
            Bitmap bitmap = getBitmapFromCache(url);
            if (bitmap == null) {
                NewsAsyncTask task = new NewsAsyncTask(url);
                task.execute(url);
                mTask.add(task);
            } else {
                ImageView imageView = (ImageView) mListView.findViewWithTag(url);
                imageView.setImageBitmap(bitmap);
            }
        }
    }

    public void cancelAllTasks() {
        if (mTask != null) {
            for (NewsAsyncTask task : mTask) {
                task.cancel(false);
            }
        }
    }


    private class NewsAsyncTask extends AsyncTask<String, Void, Bitmap> {
        private String mUrl;

        public NewsAsyncTask(String url) {
            mUrl = url;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            String url = params[0];
            Bitmap bitmap = getBitmapFromURL(params[0]); // 从网络获取图片
            if (bitmap != null) {
                addBitmapToCache(url, bitmap); // 将不在缓存的图片加入缓存
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            ImageView imageView = (ImageView) mListView.findViewWithTag(mUrl);
            if (imageView != null && bitmap != null) {
                imageView.setImageBitmap(bitmap);
            }
            mTask.remove(this);
        }
    }

}
