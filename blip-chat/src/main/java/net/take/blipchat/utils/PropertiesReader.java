package net.take.blipchat.utils;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesReader {
    private Context context;
    private Properties properties;
    public PropertiesReader(Context context) {
        this.context = context;
        //creates a new object ‘Properties’
        properties = new Properties();
    }

    public Properties getProperties(String FileName) throws IOException {
        try {
            //access to the folder ‘assets’
            AssetManager am = context.getAssets();
            //opening the file
            InputStream inputStream = am.open(FileName);
            //loading of the properties
            properties.load(inputStream);
        }
        catch (Exception e) {
            throw new IOException("Could not open 'blip.properties' file inside Assets folder.");
        }
        return properties;
    }
}
