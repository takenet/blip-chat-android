package net.take.blip_sdk_webview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.iid.FirebaseInstanceId;

import net.take.blipchat.AuthType;
import net.take.blipchat.BlipClient;
import net.take.blipchat.activities.ThreadActivity;
import net.take.blipchat.models.Account;
import net.take.blipchat.models.AuthConfig;
import net.take.blipchat.models.BlipOptions;
import net.take.blipchat.models.ConnectionDataConfig;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SandboxAppActivity extends AppCompatActivity {


    private static final int TAKE_PICTURE_REQUEST = 101;
    private String filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonGuest = (Button) findViewById(R.id.buttonGuest);
        Button buttonDev = (Button) findViewById(R.id.buttonDev);
        Button buttonTakePicture = (Button) findViewById(R.id.buttonTakePicture);

        buttonGuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    BlipOptions blipOptions = new BlipOptions();
                    blipOptions.setCustomCommonUrl("https://chat.blip.ai/");
                    blipOptions.setAuthConfig(new AuthConfig(AuthType.Guest));
                    //BlipClient.openBlipThread(SandboxAppActivity.this, BuildConfig.APPKEY , blipOptions);
                    BlipClient.openBlipThread(SandboxAppActivity.this, "YmxpcGNoYXRwb3J0YWw6ODZjNDJlODYtMTg0OC00ZjQ1LTgxZDctNTc4ZjNjYjhjMmYy" , blipOptions);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }
        });

        buttonDev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    AuthConfig authConfig = new AuthConfig(AuthType.Dev, "testereconnect1@test.com","123456");

                    Map<String, String> extras = new HashMap<>();
                    String fcmUserToken = FirebaseInstanceId.getInstance().getToken();
                    extras.put("#inbox.forwardTo", String.format("%s@firebase.gw.msging.net", fcmUserToken));

                    Account account = new Account();
                    account.setFullName("Teste reconnect 1");
                    account.setEmail("testereconnect1@test.com");
                    account.setEncryptMessageContent(true);
                    account.setExtras(extras);

                    BlipOptions blipOptions = new BlipOptions();
                    blipOptions.setAuthConfig(authConfig);
                    blipOptions.setAccount(account);
                    blipOptions.setCustomWidgetUrl("https://pagseguro-cdn.blip.ai/web/blip-chat.js");
                    blipOptions.setCustomCommonUrl("https://pagseguro.chat.blip.ai");
                    blipOptions.setConnectionDataConfig(new ConnectionDataConfig("0mn.io","pagseguro-ws.0mn.io","443"));

                    BlipClient.openBlipThread(SandboxAppActivity.this, BuildConfig.APPKEY, blipOptions);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }
        });

        buttonTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if(takePictureIntent.resolveActivity(getPackageManager()) != null){
                    File photoFile = null;
                    try{
                        photoFile = createImageFile();
                    }catch(IOException ex){
                        Log.e("BlipChat", "Image file creation failed", ex);
                    }
                    if(photoFile != null){
                        Uri photoURI = FileProvider.getUriForFile(SandboxAppActivity.this,
                                "net.take.blip_sdk_webview.fileprovider",
                                photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    }else{
                        takePictureIntent = null;
                    }
                }

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
                chooserIntent.putExtra(Intent.EXTRA_TITLE, "File chooser");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
                startActivityForResult(chooserIntent, TAKE_PICTURE_REQUEST);
            }
        });

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
        super.onActivityResult(requestCode, resultCode, intent);

            Uri[] results = null;
            //Check if response is positive
            if(requestCode == TAKE_PICTURE_REQUEST){
                if(resultCode== Activity.RESULT_OK){

                    //If no image available is captured photo
                    if(intent == null || intent.getData() == null){
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

                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(results[0]);
                    this.startActivity(i);
                } else {
                    Log.d(ThreadActivity.class.getCanonicalName(), "OnActivityResult : result canceled for File Chooser Request");
                }
            }

    }
}
