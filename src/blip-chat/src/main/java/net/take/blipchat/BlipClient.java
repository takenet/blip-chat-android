package net.take.blipchat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.google.gson.Gson;

import net.take.blipchat.activities.ThreadActivity;
import net.take.blipchat.models.AuthConfig;
import net.take.blipchat.models.BlipOptions;
import net.take.blipchat.models.ConnectionDataConfig;
import net.take.blipchat.utils.Constants;

public final class BlipClient {

    public static void openFullScreenThread(Context context, String appKey, BlipOptions blipOptions) throws IllegalArgumentException {
        blipOptions = getDefaultBlipOptions(appKey, blipOptions);
        
        String json = new Gson().toJson(blipOptions);
        Intent intent = new Intent(context, ThreadActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.API_KEY_EXTRAS, appKey);
        bundle.putString(Constants.OPTIONS_EXTRAS, json);
        intent.putExtras(bundle);

        context.startActivity(intent);
    }

    public static void openFullScreenThread(Context context, String appKey) throws IllegalArgumentException {
        openFullScreenThread(context, appKey, null);
    }

    public static BlipChatFragment openEmbeddedThread(String appKey, BlipOptions blipOptions) throws IllegalArgumentException {
        blipOptions = getDefaultBlipOptions(appKey, blipOptions);
        return BlipChatFragment.newInstance(appKey, blipOptions);
    }

    public static BlipChatFragment openEmbeddedThread(String appKey) throws IllegalArgumentException {
        return openEmbeddedThread(appKey, null);
    }

    private static BlipOptions getDefaultBlipOptions(String appKey, BlipOptions blipOptions) throws IllegalArgumentException {
        if (blipOptions == null || blipOptions.getAuthConfig() == null || blipOptions.getAuthConfig().getAuthType() == null) {
            blipOptions = new BlipOptions();
            blipOptions.setAuthConfig(new AuthConfig(AuthType.Guest));
        }

        if (blipOptions.getConnectionDataConfig() == null){
            blipOptions.setConnectionDataConfig(new ConnectionDataConfig());
        }

        validateSdkConfiguration(appKey, blipOptions);
        return blipOptions;
    }

    private static void validateSdkConfiguration(String appKey, BlipOptions blipOptions) {
        if (TextUtils.isEmpty(appKey)) {
            throw new IllegalArgumentException("Argument appKey can't be null or empty.");
        }

        if (AuthType.Dev.equals(blipOptions.getAuthConfig().getAuthType())) {
            if(TextUtils.isEmpty(blipOptions.getAuthConfig().getUserIdentity())){
                throw new IllegalArgumentException("Argument userIdentifier of blipOptions' authConfig is required");
            }
            if(TextUtils.isEmpty(blipOptions.getAuthConfig().getUserPassword())){
                throw new IllegalArgumentException("Argument userPassword of blipOptions' authConfig is required");
            }
        }
    }

}
