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
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.google.gson.Gson;

import net.take.blipchat.R;
import net.take.blipchat.listeners.ThreadActivityListener;
import net.take.blipchat.services.ThreadService;
import net.take.blipchat.models.BlipOptions;
import net.take.blipchat.utils.Constants;
import net.take.blipchat.webview.CustomWebChromeClient;
import net.take.blipchat.webview.CustomWebViewClient;

import java.io.File;

public class ThreadActivity extends AppCompatActivity implements ThreadActivityListener, ActivityCompat.OnRequestPermissionsResultCallback {

    public static final int PERMISSIONS_REQUEST_FINE_LOCATION = 100;
    public static final int WRITE_EXTERNAL_STORAGE = 101;

    private final Gson jsonConverter = new Gson();

    private CustomWebChromeClient customWebChromeClient;
    private CustomWebViewClient customWebViewClient;
    FrameLayout progressContainer;
    ProgressBar progressBar;
    WebView webview;
    String appKey;
    BlipOptions blipOptions;
    private ValueCallback<Uri> mUM;
    private final static int FCR=1;
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

        appKey = getIntent().getExtras().getString(Constants.API_KEY_EXTRAS);
        blipOptions = jsonConverter.fromJson(getIntent().getExtras().getString(Constants.OPTIONS_EXTRAS), BlipOptions.class);

        View rootView = findViewById(android.R.id.content);
        ThreadService.setupChatView(rootView, appKey, blipOptions, this);
        

        progressContainer = findViewById(R.id.progressContainer);
        progressBar = findViewById(R.id.progressBar);
        webview = findViewById(R.id.webview);
        
        customWebChromeClient = ThreadService.getLastCustomWebChromeClient();
        customWebViewClient = ThreadService.getLastCustomWebViewClient();
        pageContent = ThreadService.getLastPageContent();
        baseUrl = ThreadService.getLastBaseUrl();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (webview != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                webview.evaluateJavascript("setPresence(true)", null);
            } else {
                webview.loadUrl("javascript:setPresence(true)");
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (webview != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                webview.evaluateJavascript("setPresence(false)", null);
            } else {
                webview.loadUrl("javascript:setPresence(false)");
            }
        }
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