package net.take.blipchat;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import net.take.blipchat.listeners.ThreadActivityListener;
import net.take.blipchat.models.BlipOptions;
import net.take.blipchat.services.ThreadService;

import com.google.gson.Gson;

public class BlipChatFragment extends Fragment implements ThreadActivityListener {
    
    private static final String ARG_APP_KEY = "app_key";
    private static final String ARG_OPTIONS = "blip_options";
    
    private String appKey;
    private BlipOptions blipOptions;
    private WebView webView;
    private FrameLayout progressContainer;
    private ProgressBar progressBar;
    private static final Gson jsonConverter = new Gson();
    
    public static BlipChatFragment newInstance(String appKey, BlipOptions options) {
        BlipChatFragment fragment = new BlipChatFragment();
        Bundle args = new Bundle();
        args.putString(ARG_APP_KEY, appKey);
        args.putString(ARG_OPTIONS, jsonConverter.toJson(options));
        fragment.setArguments(args);
        return fragment;
    }
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            appKey = getArguments().getString(ARG_APP_KEY);
            String optionsJson = getArguments().getString(ARG_OPTIONS);
            blipOptions = jsonConverter.fromJson(optionsJson, BlipOptions.class);
        }
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_thread, container, false);
        
        ThreadService.setupChatView(view, appKey, blipOptions, this);
        
        progressContainer = view.findViewById(R.id.progressContainer);
        progressBar = view.findViewById(R.id.progressBar);
        webView = view.findViewById(R.id.webview);
        
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (webView != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                webView.evaluateJavascript("setPresence(true)", null);
            } else {
                webView.loadUrl("javascript:setPresence(true)");
            }
        }
    }
    
    @Override
    public void onPause() {
        super.onPause();
        if (webView != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                webView.evaluateJavascript("setPresence(false)", null);
            } else {
                webView.loadUrl("javascript:setPresence(false)");
            }
        }
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (webView != null) {
            webView.destroy();
        }
    }

    @Override
    public void setProgressBarValue(int progress) {
        if (progressBar != null) {
            progressBar.setProgress(progress);
        }
    }

    @Override
    public void enableProgressView(boolean mustEnable) {
        if (progressContainer != null) {
            if (mustEnable) {
                progressContainer.setVisibility(View.VISIBLE);
            } else {
                progressContainer.setVisibility(View.GONE);
            }
        }
    }

    public void setPresence(boolean available) {
        if (webView != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                webView.evaluateJavascript("setPresence(" + available + ")", null);
            } else {
                webView.loadUrl("javascript:setPresence(" + available + ")");
            }
        }
    }
}