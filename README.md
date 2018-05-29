
BLiP Chat for Android
======

SDK to easily add BLiP Chat's widget to your Android app. For more information, see [BLiP portal][1] and [BLiP documentation][2]. See supported versions [here](#support).

Installation
--------

Add the jcenter maven repository reference to build.gradle file of your project

```groovy
allprojects {
    repositories {
        //others repository dependencies
        jcenter()
    }
}
```

Grab jar via Gradle:
```groovy
compile 'net.take:blip-chat:2.1.14'
```

or Maven:
```xml
<dependency>
  <groupId>net.take</groupId>
  <artifactId>blip-chat</artifactId>
  <version>2.1.14</version>
  <type>pom</type>
</dependency>
```

or download [the latest JAR][3] and import into your app.

Snapshots of the development version are available at [Sonatype's `snapshots` repository][snap].

How to use
-------------------------

## Quick start

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

#Auth Config

| Property          | Description                          |
| ----------------- | ------------------------------------ |
| authType          | Defines the auth type to be used     |
| userIdentity      | The user identifier                  |
| userPassword      | The user password                    |

*Required if on integrated auth type.

To define the user authetication type, use the AuthType enum on authType property of BlipOptions. Possible values for authType are: `AuthType.GUEST` (Not integrated) and `AuthType.DEV` (Integrated).

Note: `AuthType.GUEST` will be used if 'authType' is not defined.

#Account

Check this [link](http://limeprotocol.org/resources.html#account) to see possible properties.

#Example

```java

import net.take.blipchat.AuthType;
import net.take.blipchat.BlipClient;
import net.take.blipchat.models.AuthConfig;
import net.take.blipchat.models.BlipOptions;

import org.limeprotocol.messaging.resources.Account;

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

