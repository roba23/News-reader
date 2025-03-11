package com.example.newsreader;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ReadStory extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_read_story);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        SharedPreferences sharedPreferences = this.getSharedPreferences("com.example.newsreader", Context.MODE_PRIVATE);
        String html = sharedPreferences.getString("html", "<html lang=\"en\"><body><h1>Failed To Fetch</h1></body></html>");
        Intent intent = getIntent();
        String url = "";

        url = intent.getStringExtra("url");

        if(url == null && html == null){
            Log.i("ResultDB", "Both are null");
        }


        Log.i("ResultDB", "caught:" + url);
        Log.i("ResultDB", "caught:" + html);
       // TextView myTextview = findViewById(R.id.scrollableTextView);
      //  myTextview.setText(html);
       WebView webView = (WebView) findViewById(R.id.webView);
        if(webView == null){
           Log.i("ResultDB", "webview is null");
        }
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
       // webView.loadUrl(url);
        if(!InternetCheck.isInternetAvailable(this)){
            Log.i("ResultDB", "No Internet");
            url = null;
        }

        webView.loadDataWithBaseURL(url, html, "text/html", "UTF-8", null);


    }
}