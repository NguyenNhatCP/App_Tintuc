package com.example.appnews;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.example.appnews.adapter.MyAdapter;
import com.example.appnews.model.Channel;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class ReadRss extends AsyncTask<Void, Void, Void> {
    Context context;
    String address;
    ProgressDialog progressDialog;
    ArrayList<Channel> channels;
    RecyclerView recyclerView;
    MyAdapter adapter;
    URL url;
    public static HashMap<String, Bitmap> map = new HashMap<>();

    public ReadRss(Context context, RecyclerView recyclerView, String address) {
        this.context = context;
        this.recyclerView = recyclerView;
        this.address = address;
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Đang tải...");
    }

    //before fetching of rss statrs show progress to user
    @Override
    protected void onPreExecute() {
        progressDialog.show();
        super.onPreExecute();
    }

    //This method will execute in background so in this method download rss feeds
    @Override
    protected Void doInBackground(Void... params) {
        //call process xml method to process document we downloaded from getData() method
        ProcessXml(Getdata());

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        progressDialog.dismiss();
        if (recyclerView.getAdapter() == null || recyclerView.getAdapter().getItemCount() < 1) {
            adapter = new MyAdapter(channels, context);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            //recyclerView.addItemDecoration(new VerticalSpace(20));
            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.setAdapter(adapter);
        } else {
            adapter = (MyAdapter) recyclerView.getAdapter();
            adapter.setChannels(channels);
            adapter.notifyDataSetChanged();
        }
    }

    private static String IsMatch(String s, String pattern) {
        try {
            Pattern patt = Pattern.compile(pattern);
            Matcher matcher = patt.matcher(s);
            String tmp = matcher.replaceAll("");
            return tmp;
        } catch (RuntimeException e) {
            return e.toString();
        }
    }

    // In this method we will process Rss feed  document we downloaded to parse useful information from it
    private void ProcessXml(Document data) {
        if (data != null) {
            String title = "";
            String link = "";
            String content = "";

            channels = new ArrayList<>();
            Element root = data.getDocumentElement();
            NodeList elementsByTagName = root.getElementsByTagName("item");
            for (int i = 0; i < elementsByTagName.getLength(); i++) {
                Element item = (Element) elementsByTagName.item(i);
                Channel feedItem = new Channel();
                title = checkExistsElement(item.getElementsByTagName("title"));
                feedItem.setTitle(title);
                content = checkExistsElement(item.getElementsByTagName("description"));

                String pubDate = checkExistsElement(item.getElementsByTagName("pubDate"));
                //Format Date
                try {
                    feedItem.setDate(pubDate);

                } catch (Exception e) {
                    e.printStackTrace();

                }
                link = item.getElementsByTagName("link").item(0).getTextContent();
                feedItem.setLink(link);
                Pattern p = Pattern.compile("(http(s?):)([/|.|\\w|\\s|-])*\\.(?:jpg|gif|png)");
                Matcher matcher = p.matcher(content);
                if (matcher.find()) {
                    String img = matcher.group();
                    Log.d("hinhanh", img + "......." + i);
                    feedItem.setImage(img);
                }
                String regexDes = "<([^\\s]+)(\\s[^>]*?)?(?<!/)>";
                content = IsMatch(content, regexDes);
                Log.d("des", content + "......." + i);
                feedItem.setDescription(content);
                channels.add(feedItem);
            }
        }
    }

    private String checkExistsElement(NodeList nodelist) {
        String result;
        if (nodelist.getLength() != 0) {
            return result = nodelist.item(0).getTextContent();
        } else {
            return "";
        }
    }

    //This method will download rss feed document from specified url
    public Document Getdata() {
        try {
            url = new URL(address);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            InputStream inputStream = connection.getInputStream();
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            Document xmlDoc = builder.parse(inputStream);
            return xmlDoc;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}
