package com.sakec.chembur.sakecvotes;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;

public class WebActivity extends AppCompatActivity {

    WebView myWebView;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myWebView = new WebView(getApplicationContext());
        setContentView(myWebView);

        progressBar = findViewById(R.id.progressBar);

        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        myWebView.setWebChromeClient(new WebChromeClient() {
            private ProgressDialog mProgress;

            @Override
            public void onProgressChanged(WebView view, int progress) {
                try {

                    if (mProgress == null) {
                        mProgress = new ProgressDialog(WebActivity.this);
                        mProgress.show();
                    }
                    mProgress.setMessage("Loading.... " + String.valueOf(progress) + "%");
                    if (progress == 100) {
                        mProgress.dismiss();
                        mProgress = null;
                    }
                }catch (Exception e){}
            }
        });

        myWebView.loadUrl("https://www.somaiya.edu/");

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Check if the key event was the Back button and if there's history
        if ((keyCode == KeyEvent.KEYCODE_BACK) && myWebView.canGoBack()) {
            myWebView.goBack();
            return true;
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event);
    }
}

