
BLiP Chat for Android
======

SDK to easily add BLiP Chat's widget to your Android app. For more information, see [BLiP portal][1] and [BLiP documentation][2]. 

## 🆕 New in Latest Version

This SDK now supports **two integration modes**:

1. **Full Screen Mode** - Opens chat in a dedicated full-screen Activity (traditional approach)
2. **Embedded Mode** - Returns a Fragment that can be embedded anywhere in your app (flexible approach)

The embedded mode gives you **full control** over how and where the chat appears in your application.

Installation
--------

Add the Maven Central repository to your project's build.gradle:

```groovy
allprojects {
    repositories {
        mavenCentral()
    }
}
```

Add the dependency:
```groovy
implementation 'io.github.takenet:blipchat:3.0.01.2'
```

How to use
-------------------------

## 🖥️ Full Screen Mode (Activity)

Opens the chat in a dedicated full-screen Activity:

```java
// Basic usage
BlipClient.openFullScreenThread(context, "your-app-key");

// With custom options
BlipOptions blipOptions = new BlipOptions();
blipOptions.setAuthConfig(new AuthConfig(AuthType.Guest));
BlipClient.openFullScreenThread(context, "your-app-key", blipOptions);
```

## 📱 Embedded Mode (Fragment) - **NEW!**

Returns a Fragment that you can embed anywhere in your app:

```java
// Create the chat fragment
BlipChatFragment chatFragment = BlipClient.openEmbeddedThread("your-app-key");

// Embed it in your layout
getSupportFragmentManager().beginTransaction()
    .replace(R.id.chat_container, chatFragment)
    .commit();

// Control presence manually (optional)
chatFragment.setPresence(true);  // Set user as online
chatFragment.setPresence(false); // Set user as offline
```

## 📋 Complete Example

```java
public class MainActivity extends AppCompatActivity {
    
    private BlipChatFragment chatFragment;
    
    private void openFullScreenChat() {
        BlipOptions options = new BlipOptions();
        options.setAuthConfig(new AuthConfig(AuthType.Guest));
        
        // Opens in full screen
        BlipClient.openFullScreenThread(this, "your-app-key", options);
    }
    
    private void showEmbeddedChat() {
        if (chatFragment == null) {
            // Create embedded chat
            chatFragment = BlipClient.openEmbeddedThread("your-app-key");
            
            // Add to your layout
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.chat_container, chatFragment)
                .commit();
        }
        
        // Show the container
        findViewById(R.id.chat_container).setVisibility(View.VISIBLE);
        chatFragment.setPresence(true);
    }
    
    private void hideEmbeddedChat() {
        if (chatFragment != null) {
            chatFragment.setPresence(false);
        }
        findViewById(R.id.chat_container).setVisibility(View.GONE);
    }
}
```

## 🎯 Key Benefits

### Full Screen Mode
- ✅ Simple integration
- ✅ Complete chat experience
- ✅ Handles all lifecycle automatically

### Embedded Mode (NEW!)
- ✅ **Full control** over chat placement
- ✅ **Flexible** integration experience  
- ✅ Embed in any layout or view hierarchy
- ✅ Manual presence control
- ✅ Perfect for chat-centric apps
- ✅ Show/hide chat without navigation

## 🔧 Advanced Configuration

### Prerequisites

* Add the INTERNET permissions to AndroidManifest.xml in order to use this library. If there is a request location on your chatbot flow, you should also add the ACCESS_FINE_LOCATION permission. To send or download files on BLiP Chat, add WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE and CAMERA permissions.

```xml
<manifest xlmns:android...>
 ...
 <uses-permission android:name="android.permission.INTERNET" />
 <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
 <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
 <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
 <uses-permission android:name="android.permission.CAMERA" />

 <application ...
</manifest>
```

It is also necessary to add gson dependency to your app once BLiP Chat uses this library.

```groovy
implementation 'com.google.code.gson:gson:2.8.9'
```

### Setting your SDK

After including the SDK reference in your project, you need to get your app key on [BLiP portal][1]. Choose the desired bot, go to the upper menu and access `Channels > Blip Chat`. On the `Setup` tab you will be able to get the required app key. You will also need to sign up your Android Application Id on the `Domains` section in order to enable your chatbot in your app.

### Opening the BLiP conversation widget

Opening a new thread is a very simple process. Use **BlipClient** helper class and call *openBlipThread* method

```java
BlipClient.openBlipThread(context, "YOUR_APP_KEY");
```

For instance, imagine that when your MainActivity is loaded you want to establish a new conversation between customer and chatbot.

```java
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BlipClient.openBlipThread(context, "YOUR_APP_KEY");
    }
}
```

## Advanced features

### Defining authentication type

BLiP Chat's Android SDK supports two user authentication types. It is possible to define which authentication method will be used to identify your client.

* Not integrated - Users will receive a guest account to interact with the chatbot.
* Integrated - Users will receive an account identified by developer to interact with the chatbot. User data must be provided passing a BlipOptions instance as parameter on *BlipClient.openThread* method. You can set 2 properties: `authConfig` and `account`.

*Auth Config

| Property          | Description                          |
| ----------------- | ------------------------------------ |
| authType          | Defines the auth type to be used     |
| userIdentity      | The user identifier                  |
| userPassword      | The user password                    |

*Required if on integrated auth type.

To define the user authetication type, use the AuthType enum on authType property of BlipOptions. Possible values for authType are: `AuthType.GUEST` (Not integrated) and `AuthType.DEV` (Integrated).

Note: `AuthType.GUEST` will be used if 'authType' is not defined.

*Account

Check this [link](http://limeprotocol.org/resources.html#account) to see possible properties.

## Example

```java

import net.take.blipchat.AuthType;
import net.take.blipchat.BlipClient;
import net.take.blipchat.models.AuthConfig;
import net.take.blipchat.models.BlipOptions;

...

AuthConfig authConfig = new AuthConfig(AuthType.Dev, "userId123PS","pass123PS");

Account account = new Account();
account.setFullName("User Name Android123");
account.setEmail("test@android.com");

BlipOptions blipOptions = new BlipOptions();
blipOptions.setAuthConfig(authConfig);
blipOptions.setAccount(account);

BlipClient.openBlipThread(context, APP_KEY, blipOptions);

```

### Using Organization 

To use organization in BLiP Chat's Android SDK, you must assign options to your organization's BLiP Chat URL.

## Example

```java

import net.take.blipchat.AuthType;
import net.take.blipchat.BlipClient;
import net.take.blipchat.models.AuthConfig;
import net.take.blipchat.models.BlipOptions;

...

AuthConfig authConfig = new AuthConfig(AuthType.Dev, "userId123PS","pass123PS");

Account account = new Account();
account.setFullName("User Name Android123");
account.setEmail("test@android.com");

BlipOptions blipOptions = new BlipOptions();
blipOptions.setAuthConfig(authConfig);
blipOptions.setAccount(account);

blipOptions.setCustomCommonUrl("https://take.chat.blip.ai/"); // Use the organization chat url

BlipClient.openBlipThread(context, APP_KEY, blipOptions);

```


## ProGuard

Android apps that use [ProGuard](https://developer.android.com/studio/build/shrink-code) to obfuscate code need to add an exception in file `proguard-rules.pro` to not obfuscate the BLiP Chat code, otherwise, some BLiP Chat classes may not work properly which makes it impossible to open the chat.


## Example

```java
-keepattributes JavascriptInterface
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}
-keepclassmembers class net.take.blipchat.webview.WebAppInterface {
   public *;
}

-dontwarn org.w3c.dom.bootstrap.DOMImplementationRegistry

-keepclassmembers class net.take.blipchat.models.** {
 static final long serialVersionUID;
 private static final java.io.ObjectStreamField[] serialPersistentFields;
 !static !transient <fields>;
 private void writeObject(java.io.ObjectOutputStream);
 private void readObject(java.io.ObjectInputStream);
 java.lang.Object writeReplace();
 java.lang.Object readResolve();
}

-keep class com.firebase.** { *; }
-keep class org.apache.** { *; }
-keepnames class com.fasterxml.jackson.** { *; }
-keepnames class javax.servlet.** { *; }
-keepnames class org.ietf.jgss.** { *; }
-dontwarn org.apache.**
-dontwarn org.w3c.dom.**
-dontwarn retrofit2.**
-keep class com.google.android.gms.internal.** { *; }
```

# Optional properties

| Property          | Description                                             |
| ----------------- | ------------------------------------------------------- |
| Account           | Set the bot's user information                          |
| HideMenu          | Show/hide chat menu                                     |

## Example

```java
AuthConfig authConfig = new AuthConfig(AuthType.Dev, "luizpush@test.com","123456");

Map<String, String> extras = new HashMap<>();
String fcmUserToken = FirebaseInstanceId.getInstance().getToken();
extras.put("#inbox.forwardTo", String.format("%s@firebase.gw.msging.net", fcmUserToken));

Account account = new Account();
account.setFullName("luizpush");
account.setEmail("luizpush@gmail.com");
account.setEncryptMessageContent(true);
account.setExtras(extras);

BlipOptions blipOptions = new BlipOptions();
blipOptions.setAuthConfig(authConfig);
blipOptions.setAccount(account);

blipOptions.setHideMenu(true);

BlipClient.openBlipThread(SandboxAppActivity.this, BuildConfig.APPKEY, blipOptions);
```

### Support
-------

  Android 4.4 (Kitkat)* or later

 *BLiP Chat for Android does not support file input on Android 4.4.

License
-------

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.


 [1]: https://preview.blip.ai
 [2]: https://docs.blip.ai/
 [3]: http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22net.take%22
 [snap]: https://oss.sonatype.org/content/repositories/snapshots/

