package com.example.zeph.newstest;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class NewsAdapter extends BaseAdapter implements AbsListView.OnScrollListener {

    private List<NewsBean> mList;
    private LayoutInflater mLayoutInflater;
    private ImageLoader mImageLoader;
    private int mStart, mEnd;
    public static String[] URLS;
    private boolean mFirstIn;

    public NewsAdapter(Context context, List<NewsBean> data, ListView listview) {
        mLayoutInflater = LayoutInflater.from(context);
        mList = data;
        mImageLoader = new ImageLoader(listview);
        URLS = new String[data.size()];
        mFirstIn = true;
        for (int i = 0; i < data.size(); i++) {
            URLS[i] = data.get(i).newsImgUrl;
        }
        listview.setOnScrollListener(this);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mLayoutInflater.inflate(R.layout.item_layout, null);
            viewHolder.ivIcon = (ImageView) convertView.findViewById(R.id.iv_icon);
            viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
            viewHolder.tvContent = (TextView) convertView.findViewById(R.id.tv_content);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.ivIcon.setImageResource(R.mipmap.ic_launcher);
        String url = mList.get(position).newsImgUrl;
        viewHolder.ivIcon.setTag(url);
//            new ImageLoader().showImageByThread(viewHolder.ivIcon,url);
        mImageLoader.showImageByAsyncTask(viewHolder.ivIcon, url);
        viewHolder.tvTitle.setText(mList.get(position).newsTitle);
        viewHolder.tvContent.setText(mList.get(position).newsContent);
        return convertView;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

        if (scrollState == SCROLL_STATE_IDLE) {
            mImageLoader.loadImages(mStart, mEnd);
        } else {
            mImageLoader.cancelAllTasks();
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        mStart = firstVisibleItem;
        mEnd = firstVisibleItem + visibleItemCount;
        /**
         * 第一次显示时候调用
         */
        if (mFirstIn && visibleItemCount > 0) {
            mImageLoader.loadImages(mStart, mEnd);
            mFirstIn = false;
        }
    }

    class ViewHolder {
        public TextView tvTitle, tvContent;
        public ImageView ivIcon;
    }
}
