package com.example.appnews.activity;

import android.app.DownloadManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.appnews.R;

public class WebViewActivity extends AppCompatActivity {
    WebView webView;
    ImageView imageView;
    ProgressBar progressBar;
    Toolbar toobar;
    LinearLayout linearLayout;
    SwipeRefreshLayout swipeRefreshLayout;
    String myCurrentUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);
        Intent intent = getIntent();
        final String link = intent.getStringExtra("link");
        AddEvent();
        progressBar.setMax(100);
        webView.clearCache(true);
        webView.clearHistory();
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.loadUrl(link);
        //new GetHTML(webView).execute(link);
        webView.setWebViewClient(new WebViewClient()
        {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                linearLayout.setVisibility(View.VISIBLE);
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                linearLayout.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                super.onPageFinished(view, url);
                myCurrentUrl = url;
            }
        });
        webView.setWebChromeClient(new WebChromeClient(){
                                       @Override
                                       public void onProgressChanged(WebView view, int newProgress) {
                                           super.onProgressChanged(view, newProgress);
                                           progressBar.setProgress(newProgress);
                                       }
                                       @Override
                                       public void onReceivedTitle(WebView view, String title) {
                                           super.onReceivedTitle(view, title);
                                           setSupportActionBar(toobar);
                                           toobar.setTitle(title);
                                           toobar.setNavigationIcon(R.drawable.ic_back);
                                           toobar.setNavigationOnClickListener(new View.OnClickListener() {
                                               @Override
                                               public void onClick(View v) {
                                                    onBackPressed();
                                               }
                                           });
                                       }
                                       @Override
                                       public void onReceivedIcon(WebView view, Bitmap icon) {
                                           super.onReceivedIcon(view, icon);
                                           imageView.setImageBitmap(icon);
                                       }
        });
        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                DownloadManager.Request myRequest = new DownloadManager.Request(Uri.parse(url));
                myRequest.allowScanningByMediaScanner();
                myRequest.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                DownloadManager myManager = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
                myManager.enqueue(myRequest);

                Toast.makeText(WebViewActivity.this, "Đang tải tệp tin...", Toast.LENGTH_SHORT).show();
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                webView.reload();
            }
        });
    }

    private void AddEvent() {
        webView = (WebView) findViewById(R.id.webview);
        progressBar= (ProgressBar)findViewById(R.id.progress_horizontal_webview);
        imageView = (ImageView) findViewById(R.id.imgWebView);
        toobar = (Toolbar) findViewById(R.id.toolbar);
        linearLayout = (LinearLayout) findViewById(R.id.linear_webview);
        swipeRefreshLayout =(SwipeRefreshLayout)findViewById(R.id.swipe_refresh_webview);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.activity_webview,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.memu_back:
                onBackPressed();
                break;
            case R.id.menu_forward:
                onForwardPressed();
                break;
            case R.id.menu_refresh:
                webView.reload();
                break;
            case R.id.menu_share:
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                share.putExtra(Intent.EXTRA_TEXT,"Nội dung: "+myCurrentUrl);
                share.putExtra(Intent.EXTRA_SUBJECT,"Tựa đề");
                startActivity(Intent.createChooser(share,"Chia sẻ với bạn bè..."));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    private void onForwardPressed()
    {
        if(webView.canGoForward())
        {
            webView.goForward();
        }
        else
        {
            Toast.makeText(this, "Không thể tiến đến trang tiếp theo", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        if(webView.canGoBack())
        {
            webView.goBack();
        }
        else
        {
            finish();
        }
    }
}
