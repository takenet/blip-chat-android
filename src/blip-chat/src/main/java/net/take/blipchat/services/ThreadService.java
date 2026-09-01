package net.take.blipchat.services;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.google.gson.Gson;

import android.app.Activity;
import android.view.View;
import android.view.ViewTreeObserver;

import net.take.blipchat.BuildConfig;
import net.take.blipchat.R;
import net.take.blipchat.activities.ThreadActivity;
import net.take.blipchat.listeners.ThreadActivityListener;
import net.take.blipchat.models.BlipOptions;
import net.take.blipchat.utils.Constants;
import net.take.blipchat.utils.Helpers;
import net.take.blipchat.webview.BlipWebViewBuilder;
import net.take.blipchat.webview.CustomWebChromeClient;
import net.take.blipchat.webview.CustomWebViewClient;
import net.take.blipchat.webview.WebAppInterface;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class ThreadService {
    private static final Gson jsonConverter = new Gson();
    
    private static CustomWebChromeClient lastCustomWebChromeClient;
    private static CustomWebViewClient lastCustomWebViewClient;
    private static String lastPageContent;
    private static String lastBaseUrl;
    

    
    public static CustomWebChromeClient getLastCustomWebChromeClient() {
        return lastCustomWebChromeClient;
    }
    
    public static CustomWebViewClient getLastCustomWebViewClient() {
        return lastCustomWebViewClient;
    }
    
    public static String getLastPageContent() {
        return lastPageContent;
    }
    
    public static String getLastBaseUrl() {
        return lastBaseUrl;
    }
    
    public static void setupChatView(View rootView, String appKey, BlipOptions blipOptions, ThreadActivityListener listener) {
        ProgressBar progressBar = rootView.findViewById(R.id.progressBar);
        WebView webView = rootView.findViewById(R.id.webview);
        
        progressBar.setMax(100);
        progressBar.setProgress(0);
        
        configureWebView(webView, rootView.getContext(), appKey, blipOptions, listener);
    }
    
    private static void configureWebView(WebView webView, Context context, String appKey, BlipOptions blipOptions, ThreadActivityListener listener) {
        String baseUrl = "https://" + context.getPackageName();
        
        ThreadActivityListener actualListener = listener;
        if (actualListener == null) {
            actualListener = new ThreadActivityListener() {
                @Override
                public void setProgressBarValue(int progress) {
                    // No-op for Fragment usage
                }
                
                @Override
                public void enableProgressView(boolean mustEnable) {
                    // No-op for Fragment usage
                }
            };
        }
        
        ThreadActivity threadActivity = (context instanceof ThreadActivity) ? (ThreadActivity) context : null;
        WebAppInterface webAppInterface = new WebAppInterface(threadActivity);
        
        BlipWebViewBuilder webViewBuilder = new BlipWebViewBuilder(context, webView);
        CustomWebChromeClient customWebChromeClient = new CustomWebChromeClient(threadActivity, actualListener);
        CustomWebViewClient customWebViewClient = new CustomWebViewClient(actualListener, baseUrl, context);
        
        lastCustomWebChromeClient = customWebChromeClient;
        lastCustomWebViewClient = customWebViewClient;
        lastBaseUrl = baseUrl;
        
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
            Log.e(ThreadService.class.getCanonicalName(), e.getMessage());
            return;
        }
        
        webView = webViewBuilder.build();
        
        if (context instanceof Activity) {
            setKeyboardVisibilityListener((Activity) context, webAppInterface, webView);
        }
        
        String pageContent = generateHtmlPageContent(context, appKey, blipOptions);
        lastPageContent = pageContent;
        webView.loadDataWithBaseURL(baseUrl, pageContent, "text/html; charset=UTF-8", null, null);
    }
    
    private static String generateHtmlPageContent(Context context, String appKey, BlipOptions blipOptions) {
        StringBuilder content = new StringBuilder();
        BufferedReader reader = null;

        try {
            InputStreamReader inputStreamReader = new InputStreamReader(context.getAssets().open("BlipSdkTemplate.html"));
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
            Log.e(ThreadService.class.getCanonicalName(), e.getMessage());
        } catch (IOException e) {
            Log.e(ThreadService.class.getCanonicalName(), e.getMessage());
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                Log.e(ThreadService.class.getCanonicalName(), e.getMessage());
            }
        }

        return content.toString();
    }
    
    private static void setKeyboardVisibilityListener(Activity activity, WebAppInterface webAppInterface, final WebView webView) {
        final View activityRootView = activity.findViewById(android.R.id.content);
        if (activityRootView != null) {
            final boolean[] lastEmittedKeyboardState = {false};
            activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();
                    boolean isOpen = heightDiff > Helpers.dpToPx(activity, 200);

                    if (webAppInterface != null) {
                        webAppInterface.setIsKeyboardOpen(isOpen);
                    }

                    // Only notify JS when the keyboard state actually changes, to avoid redundant calls on every layout pass
                    if (isOpen != lastEmittedKeyboardState[0]) {
                        lastEmittedKeyboardState[0] = isOpen;
                        if (webView != null) {
                            String script = "setKeyboardOpen(" + isOpen + ")";
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                webView.evaluateJavascript(script, null);
                            } else {
                                webView.loadUrl("javascript:" + script);
                            }
                        }
                    }
                }
            });
        }
    }
}