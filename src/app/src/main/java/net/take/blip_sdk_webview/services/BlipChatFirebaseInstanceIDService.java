package net.take.blip_sdk_webview.services;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class BlipChatFirebaseInstanceIDService extends FirebaseMessagingService {

    @Override
    public void onNewToken(String token) {
        // Called when a new token is generated
        Log.d(BlipChatFirebaseInstanceIDService.class.getCanonicalName(), "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        //sendRegistrationToServer(token);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        // Handle FCM messages here
        Log.d(BlipChatFirebaseInstanceIDService.class.getCanonicalName(), "From: " + remoteMessage.getFrom());
    }
}
