package net.take.blipchat.activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.google.gson.Gson;

import net.take.blipchat.BuildConfig;
import net.take.blipchat.R;
import net.take.blipchat.listeners.ThreadActivityListener;
import net.take.blipchat.models.BlipOptions;
import net.take.blipchat.utils.Constants;
import net.take.blipchat.utils.Helpers;
import net.take.blipchat.webview.BlipWebViewBuilder;
import net.take.blipchat.webview.CustomWebChromeClient;
import net.take.blipchat.webview.CustomWebViewClient;
import net.take.blipchat.webview.WebAppInterface;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class ThreadActivity extends AppCompatActivity implements ThreadActivityListener, ActivityCompat.OnRequestPermissionsResultCallback {

    public static final int PERMISSIONS_REQUEST_FINE_LOCATION = 100;
    public static final int WRITE_EXTERNAL_STORAGE = 101;

    private final Gson jsonConverter = new Gson();

    private CustomWebChromeClient customWebChromeClient;
    FrameLayout progressContainer;
    ProgressBar progressBar;
    WebView webview;
    String appKey;
    BlipOptions blipOptions;
    private ValueCallback<Uri> mUM;
    private final static int FCR=1;
    private WebAppInterface webAppInterface;
    private CustomWebViewClient customWebViewClient;
    private String pageContent = "";
    private String baseUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        progressContainer = (FrameLayout) findViewById(R.id.progressContainer);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        webview = (WebView) findViewById(R.id.webview);

        progressBar.setMax(100);
        progressBar.setProgress(0);

        appKey = getIntent().getExtras().getString(Constants.API_KEY_EXTRAS);
        blipOptions = jsonConverter.fromJson(getIntent().getExtras().getString(Constants.OPTIONS_EXTRAS), BlipOptions.class) ;

        webAppInterface = new WebAppInterface(this);
        baseUrl = "https://" + getApplicationContext().getPackageName();

        setKeyboardVisibilityListener();

        BlipWebViewBuilder webViewBuilder = new BlipWebViewBuilder(this, webview);

        customWebChromeClient = new CustomWebChromeClient(this, this);

        customWebViewClient = new CustomWebViewClient(this, baseUrl, this);

        try {
            webViewBuilder
                    .withCustomWebViewClient(customWebViewClient)
                    .withCustomWebChromeClient(customWebChromeClient)
                    .withJavaScriptSupport()
                    .withJavascriptInterface(webAppInterface)
                    .withCustomDownloader()
                    .withGeolocation()
                    .withZoomSupport()
                    .withCacheEnabled(true);

        } catch (Exception e) {
            Log.e(ThreadActivity.class.getCanonicalName(), e.getMessage());
            finish();
        }

        webview = webViewBuilder.build();

        pageContent = getHtmlPageContent();

        webview.loadDataWithBaseURL(baseUrl, pageContent, "text/html; charset=UTF-8", null, null);

        // Load the user-specified URL directly in the WebView
        // String customUrl = "https://compliance-take.chat.blip.ai/?appKey=c2FsbGVzaHR0cDo0NzhhMWU2NC1lMjM4LTRhMGEtYTdkNi02MWFkZDZhNGQyMTE=";
        // webview.loadUrl(customUrl);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onResume() {
        super.onResume();
        this.webview.evaluateJavascript("setPresence(true)", null);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onPause() {
        super.onPause();
        this.webview.evaluateJavascript("setPresence(false)", null);
    }

    private String getHtmlPageContent() {
        StringBuilder content = new StringBuilder();
        BufferedReader reader = null;

        try {
            InputStreamReader inputStreamReader = new InputStreamReader(getAssets().open("BlipSdkTemplate.html"));
            reader = new BufferedReader(inputStreamReader);
            String text = null;

            String authConfigJson = jsonConverter.toJson(blipOptions.getAuthConfig());
            String accountJson = jsonConverter.toJson(blipOptions.getAccount());
            String connectionDataJson = jsonConverter.toJson(blipOptions.getConnectionDataConfig());
            String commonUrl = blipOptions.getCustomCommonUrl() != null ? blipOptions.getCustomCommonUrl() : "";
            String blipChatUrl = blipOptions.getCustomCommonUrl() != null ? blipOptions.getCustomCommonUrl() : BuildConfig.BLIP_CHAT_URL;
            String blipChatWidgetUrl = blipOptions.getCustomWidgetUrl() != null ? blipOptions.getCustomWidgetUrl() : BuildConfig.SCRIPT_SDK_URL;

            while ((text = reader.readLine()) != null) {

                text = text.replace(Constants.APPKEY_VAR_KEY, appKey);
                text = text.replace(Constants.AUTHCONFIG_VAR_KEY, authConfigJson);
                text = text.replace(Constants.ACCOUNT_VAR_KEY, accountJson);
                text = text.replace(Constants.CONNECTION_DATA_VAR_KEY, connectionDataJson);
                text = text.replace(Constants.COMMON_URL_VAR_KEY, commonUrl);
                text = text.replace(Constants.SCRIPT_SDK_URL_KEY, blipChatWidgetUrl);
                text = text.replace(Constants.BLIP_CHAT_URL_KEY, blipChatUrl);
                content.append(text);

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
            }
        }
        return content.toString();
    }

    private void setKeyboardVisibilityListener() {
        final View activityRootView = findViewById(R.id.activity_main);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();
                if (heightDiff > Helpers.dpToPx(ThreadActivity.this, 200)) { // keyboard opened
                    webAppInterface.setIsKeyboardOpen(true);
                } else { // keyboard closed
                    webAppInterface.setIsKeyboardOpen(false);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
        super.onActivityResult(requestCode, resultCode, intent);
        ValueCallback<Uri[]> pathCallback = customWebChromeClient.getPathCallback();

        if(Build.VERSION.SDK_INT >= 21){
            Uri[] results = null;
            //Check if response is positive
            if(requestCode == FCR){
                if(resultCode== Activity.RESULT_OK){
                    if(null == pathCallback){
                        return;
                    }
                    //If no image available is captured photo
                    if(intent == null || intent.getData() == null){
                        String filePath = customWebChromeClient.getFilePath();
                        File f = new File(filePath);
                        Uri contentUri = Uri.fromFile(f);
                        if(filePath != null){
                            results = new Uri[]{contentUri};
                        }
                    }else{
                        String dataString = intent.getDataString();
                        if(dataString != null){
                            results = new Uri[]{Uri.parse(dataString)};
                        }
                    }
                } else {
                    Log.d(ThreadActivity.class.getCanonicalName(), "OnActivityResult : result canceled for File Chooser Request");
                }
            }
            pathCallback.onReceiveValue(results);
            pathCallback = null;
        }else{
            if(requestCode == FCR){
                if(null == mUM) return;
                Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();
                mUM.onReceiveValue(result);
                mUM = null;
            }
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        int historyStackPointer = customWebViewClient.getHistoryStackPointer();

        if((keyCode == KeyEvent.KEYCODE_BACK)) {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                if (historyStackPointer > 1 ||  webview.canGoBack()) {
                    customWebViewClient.setHistoryStackPointer(0);
                    webview.loadDataWithBaseURL(baseUrl, pageContent, "text/html; charset=UTF-8", null, baseUrl);
                    customWebViewClient.setClearHistory(true);
                    progressContainer.setVisibility(View.VISIBLE);
                    return true;
                }

            } else if (webview.canGoBack()) {

                if(historyStackPointer > 1) {
                    customWebViewClient.setHistoryStackPointer(0);
                    webview.loadDataWithBaseURL(baseUrl, pageContent, "text/html; charset=UTF-8", null, null);
                    progressContainer.setVisibility(View.VISIBLE);
                    return true;
                }
            }
        }

        return super.onKeyUp(keyCode, event);
    }

    public void setProgressBarValue(int progress) {
        this.progressBar.setProgress(progress);
    }

    public void enableProgressView(boolean mustEnable) {
        if (mustEnable) {
            progressContainer.setVisibility(View.VISIBLE);
        } else {
            progressContainer.setVisibility(View.GONE);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_FINE_LOCATION:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    customWebChromeClient.grantGeoLocationPermission();
                } else {
                    customWebChromeClient.rejectGeoLocationPermission();
                }
                break;
            case WRITE_EXTERNAL_STORAGE:
                // If request is cancelled, the result arrays are empty.
                for (int i = 0, len = permissions.length; i < len; i++) {
                    if(grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        boolean showRationale = shouldShowRequestPermissionRationale( permissions[i] );
                        if (!showRationale) { // User checked 'Never ask again'
                            showDialogPermissionDeniedPermanently();
                        }
                        customWebChromeClient.rejectExternalFilePermission();
                        return;
                    }
                }
                customWebChromeClient.grantExternalFilePermission();
                break;
        }
    }

    private void showDialogPermissionDeniedPermanently() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ThreadActivity.this);
        builder.setMessage(R.string.blipchat_permission_denied_permanently_dialog_message);

        // Add the buttons
        builder.setPositiveButton(R.string.blipchat_settings, (dialog, id) -> {
            // User clicked OK button

            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            startActivity(intent);
        });
        builder.setNegativeButton(R.string.blipchat_not_now, (dialog, id) -> {
            // User cancelled the dialog
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}