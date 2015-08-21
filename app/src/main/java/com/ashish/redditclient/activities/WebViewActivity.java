package com.ashish.redditclient.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.ashish.redditclient.R;

public class WebViewActivity extends AppCompatActivity {

    //set up required variables
    private static final String urlTag = "url";

    //Launch webview as intent
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.web_view);

        WebView mWebview = (WebView) findViewById(R.id.webView);

        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar_web_view);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Web View");

        // getting intent data
        Intent intent = getIntent();

        // Get JSON values from previous intent
        String postUrl = intent.getStringExtra(urlTag);

        mWebview.getSettings().setJavaScriptEnabled(true); // enable javascript
        mWebview.getSettings().setBuiltInZoomControls(true);

        //load selected url
        mWebview.loadUrl(postUrl);

        final Activity activity = this;

        //function keeps the browser inside the app so the user doesn't
        //have to switch between applications
        mWebview.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(activity, description, Toast.LENGTH_SHORT).show();
            }
        });

    }
}