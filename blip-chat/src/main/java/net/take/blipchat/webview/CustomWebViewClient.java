package net.take.blipchat.webview;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import androidx.annotation.RequiresApi;
import android.util.Log;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import net.take.blipchat.listeners.ThreadActivityListener;

//WebView Client
public class CustomWebViewClient extends WebViewClient {

    private final ThreadActivityListener listener;

    private int historyStackPointer = 0;
    private String baseurl;
    private boolean clearHistory = false;
    private Context context;

    public CustomWebViewClient(ThreadActivityListener listener, String baseUrl, Context context) {
        this.listener = listener;
        this.baseurl = baseUrl;
        this.context = context;
    }

    @TargetApi(23)
    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        super.onReceivedError(view, request, error);
        Log.e(CustomWebChromeClient.class.getCanonicalName(), "onReceivedError: " + String.format("Url: %s; Error Code: %d; Error Description %s", request.getUrl().toString(), error.getErrorCode(), error.getDescription().toString()));
    }

    public int getHistoryStackPointer() {
        return this.historyStackPointer;
    }

    public void setHistoryStackPointer(int count) {
        this.historyStackPointer = count;
    }

    public void setClearHistory(boolean clearHistory) {
        this.clearHistory = clearHistory;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        String url = request.getUrl().toString();
        if (!url.contains(this.baseurl)) {
            openWithActionView(url);
            return true;
        }

        return false;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (!url.contains(this.baseurl)) {
            openWithActionView(url);
            return true;
        }

        return false;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        this.listener.enableProgressView(true);
        this.listener.setProgressBarValue(0);
        historyStackPointer++;

        super.onPageStarted(view, url, favicon);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        this.listener.enableProgressView(false);

        if (clearHistory) {
            view.clearHistory();
            clearHistory = false;
        }
    }

    private void openWithActionView(String url) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        this.context.startActivity(i);
    }

}