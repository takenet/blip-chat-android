package net.take.blipchat.webview;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.GeolocationPermissions;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import net.take.blipchat.R;
import net.take.blipchat.activities.ThreadActivity;
import net.take.blipchat.listeners.ExternalFilePermissionListener;
import net.take.blipchat.listeners.GeolocationPermissionListener;
import net.take.blipchat.listeners.ThreadActivityListener;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static net.take.blipchat.activities.ThreadActivity.PERMISSIONS_REQUEST_FINE_LOCATION;
import static net.take.blipchat.activities.ThreadActivity.WRITE_EXTERNAL_STORAGE;

//WebViewChrome let you know when progress change to set the value for the progress bar
public class CustomWebChromeClient extends WebChromeClient implements GeolocationPermissionListener, ExternalFilePermissionListener {

    private ThreadActivity activity;
    private final ThreadActivityListener listener;
    private GeolocationPermissions.Callback permissionCallback;
    private String permissionOrigin;
    private ValueCallback<Uri[]> pathCallback;
    private String filePath;
    private final static int FCR=1;


    public ValueCallback<Uri[]> getPathCallback() {
        return pathCallback;
    }

    public String getFilePath() {
        return filePath;
    }

    public CustomWebChromeClient(ThreadActivity activity, ThreadActivityListener listener) {
        this.activity = activity;
        this.listener = listener;
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        this.listener.setProgressBarValue(newProgress);
        if(newProgress == 100) {
            this.listener.enableProgressView(false);
        }
        super.onProgressChanged(view, newProgress);
    }

    @Override
    public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
        Log.d("BLiP Chat", consoleMessage.message() + " -- From line "
                + consoleMessage.lineNumber() + " of "
                + consoleMessage.sourceId());
        return super.onConsoleMessage(consoleMessage);
    }

    @Override
    public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionCallback = callback;
            permissionOrigin = origin;
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_FINE_LOCATION);
        } else {
            callback.invoke(origin, true, false);
        }
    }

    @Override
    public void grantExternalFilePermission() {
        Intent takePictureIntent = getCameraIntent();
        startChooserIntent(takePictureIntent);
    }

    @Override
    public void rejectExternalFilePermission() {
        pathCallback.onReceiveValue(null);
        pathCallback = null;
    }

    @Override
    public void grantGeoLocationPermission() {
        permissionCallback.invoke(permissionOrigin, true, false);
    }

    @Override
    public void rejectGeoLocationPermission() {
        permissionCallback.invoke(permissionOrigin, false, false);
        permissionCallback = null;
        permissionOrigin = null;
    }

    private boolean checkAllInputFilePermissions() {
        return (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

    private File createImageFile() throws IOException{
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "img_"+timeStamp+"_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName,".jpg",storageDir);
        //Save file path to get image on activity result
        filePath = image.getAbsolutePath();
        return image;
    }

    public boolean onShowFileChooser(
            WebView webView, ValueCallback<Uri[]> filePathCallback,
            WebChromeClient.FileChooserParams fileChooserParams){

        if(pathCallback != null){
            pathCallback = null;
        }
        pathCallback = filePathCallback;

        if(Build.VERSION.SDK_INT >=23 && !checkAllInputFilePermissions()) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, WRITE_EXTERNAL_STORAGE);
            Log.d(CustomWebChromeClient.class.getCanonicalName(), "onShowFileChooser: Don't have all permissions needed, asking for it");
            return true;
        }

        Intent takePictureIntent = getCameraIntent();
        startChooserIntent(takePictureIntent);
        return true;
    }

    private void startChooserIntent(Intent takePictureIntent) {
        Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
        contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
        contentSelectionIntent.setType("*/*");
        Intent[] intentArray;

        if(takePictureIntent != null){
            intentArray = new Intent[]{takePictureIntent};
        }else{
            intentArray = new Intent[0];
        }

        Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
        chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
        chooserIntent.putExtra(Intent.EXTRA_TITLE, R.string.blipchat_file_chooser);
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
        activity.startActivityForResult(chooserIntent, FCR);
    }

    @Nullable
    private Intent getCameraIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePictureIntent.resolveActivity(activity.getPackageManager()) != null){
            File photoFile = null;
            try{
                photoFile = createImageFile();
            }catch(IOException ex){
                Log.e("BlipChat", "Image file creation failed", ex);
            }
            if(photoFile != null){
                Uri photoURI = FileProvider.getUriForFile(activity,
                        activity.getApplication().getPackageName()+ ".blipchat.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            }else{
                takePictureIntent = null;
            }
        }
        return takePictureIntent;
    }
}