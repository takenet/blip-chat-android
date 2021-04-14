package net.take.blipchat.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class Helpers {

    public static String getCachePath(Context context) {
        return context.getFilesDir().getParent() + "/cache";
    }

    public static String serializeExtras(Map<String, String> extras) {

        if (extras == null || extras.entrySet().size() == 0) return null;

        JSONObject pnObj = new JSONObject();

        try {
            for (Map.Entry<String, String> eS : extras.entrySet()) {
                pnObj.put(eS.getKey(), eS.getValue());
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return pnObj.toString();
    }

    public static float dpToPx(Context context, float valueInDp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
    }

}
