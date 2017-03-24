package com.example.gadfly.projectgadfly;

import android.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;
import android.widget.TextView;

public class WebPage extends AppCompatActivity {

    public static final String ADDRESS = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_page);
        String name = getIntent().getStringExtra(ADDRESS);
        android.webkit.WebView myWebView = (android.webkit.WebView) findViewById(R.id.webView);
        myWebView.setWebViewClient(new WebViewClient());
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        myWebView.loadUrl("http://www.govtrack.us");
    }

}

