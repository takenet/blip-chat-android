package net.take.blipchat.webview;

import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.webkit.DownloadListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import net.take.blipchat.utils.Helpers;

import static android.content.Context.DOWNLOAD_SERVICE;

public class BlipWebViewBuilder {

    private final Context context;
    private final WebView webView;

    public BlipWebViewBuilder(Context context, WebView webView) {

        if (context == null) throw new UnsupportedOperationException("Context can't be null");
        if (webView == null) throw new UnsupportedOperationException("WebView can't be null");

        this.context = context;
        this.webView = webView;
    }

    public BlipWebViewBuilder withCustomWebViewClient(CustomWebViewClient customWebViewClient) {
        webView.setWebViewClient(customWebViewClient);
        return this;
    }

    public BlipWebViewBuilder withCustomWebChromeClient(CustomWebChromeClient customWebChromeClient) {
        webView.setWebChromeClient(customWebChromeClient);
        return this;
    }

    public WebView build() {
        return webView;
    }

    public BlipWebViewBuilder withJavaScriptSupport() {

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setDomStorageEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (0 != (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE)) {
                WebView.setWebContentsDebuggingEnabled(true);
            }
        }

        return this;
    }

    public BlipWebViewBuilder withCustomDownloader() {
        webView.setDownloadListener(new DownloadListener() {
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {
                DownloadManager.Request request = new DownloadManager.Request(
                        Uri.parse(url));
                String fileName;
                try {
                    fileName = url.substring(url.lastIndexOf("/")+1);
                } catch (Exception e) {
                    e.printStackTrace();
                    fileName = "file";
                }
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
                DownloadManager dm = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
                dm.enqueue(request);
                Toast.makeText(context, "Baixando Arquivo...",
                        Toast.LENGTH_LONG).show();
            }
        });
        return this;
    }


    public BlipWebViewBuilder withGeolocation() {
        webView.getSettings().setGeolocationEnabled(true);
        webView.getSettings().setDatabaseEnabled(true);
        return this;
    }

    public BlipWebViewBuilder withZoomSupport() {

        webView.getSettings().setSupportZoom(false);
        webView.getSettings().setBuiltInZoomControls(false);
        webView.getSettings().setDisplayZoomControls(false);

        return this;
    }

    public BlipWebViewBuilder withCacheEnabled(boolean enable) {

        if (enable) {
            webView.getSettings().setAppCacheEnabled(true);
            webView.getSettings().setAppCacheMaxSize(1024 * 1024 * 8);
            webView.getSettings().setAppCachePath(Helpers.getCachePath(this.context));
            webView.getSettings().setAllowFileAccess(true);

            webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        }

        return this;
    }

    public BlipWebViewBuilder withJavascriptInterface(WebAppInterface webAppInterface) {

        webView.addJavascriptInterface(webAppInterface, "Android");

        return  this;
    }

}
