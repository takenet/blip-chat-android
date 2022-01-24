package net.take.blipchat.webview;

import android.content.Context;
import android.webkit.JavascriptInterface;

public class WebAppInterface {
    Context mContext;
    boolean isKeyboardOpen;

    public WebAppInterface(Context c) {
        mContext = c;
        isKeyboardOpen = false;
    }

    @JavascriptInterface
    public boolean isKeyboardOpen() {
        return this.isKeyboardOpen;
    }

    public void setIsKeyboardOpen(boolean isOpen) {
        this.isKeyboardOpen = isOpen;
    }
}
