package com.example.zeph.newstest;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView mListView;
    private static String URL = "http://www.imooc.com/api/teacher?type=4&num=30";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mListView = (ListView) findViewById(R.id.lv_main);
        new NewsAsyncTask().execute(URL);
    }


    /**
     * 实现了网络的异步访问
     */
    class NewsAsyncTask extends AsyncTask<String, Void, List<NewsBean>> {

        @Override
        protected List<NewsBean> doInBackground(String... params) {
            return getJsonData(params[0]);
        }

        @Override
        protected void onPostExecute(List<NewsBean> newsBeen) {
            super.onPostExecute(newsBeen);
            NewsAdapter adapter = new NewsAdapter(MainActivity.this, newsBeen, mListView);
            mListView.setAdapter(adapter);
        }
    }

    /**
     * 将url转化为我们所封装的newsBean对象
     */
    private List<NewsBean> getJsonData(String url) {
        List<NewsBean> newBeanList = new ArrayList<>();
        try {
            String josnString = readStream(new URL(url).openStream());
            JSONObject jsonObject;
            NewsBean newsBean;
            try {
                jsonObject = new JSONObject(josnString);
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                for (int i = 0; i < jsonArray.length(); i++) {
                    jsonObject = jsonArray.getJSONObject(i);
                    newsBean = new NewsBean();
                    newsBean.newsImgUrl = jsonObject.getString("picSmall");
                    newsBean.newsTitle = jsonObject.getString("name");
                    newsBean.newsContent = jsonObject.getString("description");
                    newBeanList.add(newsBean);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return newBeanList;
    }

    /**
     * 通过InputStream解析网络返回的数组
     */
    private String readStream(InputStream is) {
        InputStreamReader isr;
        String result = "";
        try {
            String line;
            isr = new InputStreamReader(is, "utf-8");//将字节流转化为字符流
            BufferedReader br = new BufferedReader(isr);//用BufferedReader读取字符流
            while ((line = br.readLine()) != null) {
                result += line;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
