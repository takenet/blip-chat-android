
BLiP Chat for Android
======

SDK to easily add BLiP Chat widget in your Android app. For more information see [BLiP portal][1] and [BLiP documentation][2]. See supported versions [here](#support).

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
compile 'net.take:blip-chat:2.1.7'
```

or Maven:
```xml
<dependency>
  <groupId>net.take</groupId>
  <artifactId>blip-chat</artifactId>
  <version>2.1.7</version>
  <type>pom</type>
</dependency>
```

or download [the latest JAR][3] and import in your app.

Snapshots of the development version are available in [Sonatype's `snapshots` repository][snap].

How to use
-------------------------

## Quick start

### Prerequisites

* Add the INTERNET permissions on AndroidManifest.xml in order to use this library. If there is a request location on your chatbot flow you should also add the ACCESS_FINE_LOCATION permission. To send or download files on BLiP Chat add WRITE_EXTERNAL_STORAGE and READ_EXTERNAL_STORAGE permissions.

```xml
<manifest xlmns:android...>
 ...
 <uses-permission android:name="android.permission.INTERNET" />
 <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
 <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
 <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />

 <application ...
</manifest>
```

### Setting your SDK

After including sdk reference to your project you need to get your app key on [BLiP portal][1]. Choose the desired bot, go to the upper menu and access `Channels > Blip Chat`. On the `Setup` tab you will be able to get the required app key. You will also need to add your Android Application Id on the `Domains` section, in order to enable your chatbot in your app.

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

BLiP Chat android sdk supports three different user authentication types. It is possible to define which authentication method will be used to identify your client.

* Not integrated - Users will receive a guest account to interact with the chatbot.
<!-- * Login - Users will receive an account with his 'Name' and 'Email' (provided by the user) to interact with the chatbot. In this mode the users have not message history. -->
* Integrated - Users will receive an account identified by developer to interact with the chatbot. User data must be provided passing a BlipOptions instance as parameter on *BlipClient.openThread* method. You can set 2 properties: `authConfig` and `account`.

#Auth Config

| Property          | Description                          |
| ----------------- | ------------------------------------ |
| authType          | Defines the auth type to be used     |
| userIdentity      | The user identifier                  |
| userPassword      | The user password                    |

*Required if on integrated auth type.

To define what user authetication type use the AuthType enum on authType property of BlipOptions. Possible values for authType are: `AuthType.GUEST` (Not integrated) and `AuthType.DEV` (Integrated).

Note: Guest type will be used as default If you do not define 'authType'.

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

