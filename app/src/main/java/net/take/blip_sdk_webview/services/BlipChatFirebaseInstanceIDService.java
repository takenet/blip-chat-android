package net.take.blip_sdk_webview.services;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;


public class BlipChatFirebaseInstanceIDService extends  FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String newToken) {
        super.onNewToken(newToken);
        Log.d("NEW_TOKEN",newToken);
        Log.d(BlipChatFirebaseInstanceIDService.class.getCanonicalName(), "Refreshed token: " + newToken);
    }

}
