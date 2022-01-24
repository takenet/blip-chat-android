package net.take.blip_sdk_webview;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

public class BlipSDKApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        if(BuildConfig.DEBUG){
            // to detect memory leaks (if any whatsoever)
            if (LeakCanary.isInAnalyzerProcess(this)) {
                return;
            }
            LeakCanary.install(this);
        }
    }
}
