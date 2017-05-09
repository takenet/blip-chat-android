
BLiP Chat for Android
======

SDK to easily add BLiP Chat widget in your Android app. For more information see [BLiP portal][1] and [BLiP documentation][2]. See supported versions [here](#support).

Installation
--------

Grab jar via Gradle:
```groovy
compile 'net.take:blip-chat:0.0.22'
```

or Maven:
```xml
<dependency>
  <groupId>net.take</groupId>
  <artifactId>blip-chat</artifactId>
  <version>0.0.22</version>
  <type>pom</type>
</dependency>
```

or download [the latest JAR][3] and import in your app.

Snapshots of the development version are available in [Sonatype's `snapshots` repository][snap].

How to use
-------------------------

## Quick start

### Prerequisites

* Add the internet and location permissions on AndroidManifest.xml

```xml
<manifest xlmns:android...>
 ...
 <uses-permission android:name="android.permission.INTERNET" />
 <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
 <application ...
</manifest>
```

### Setting your SDK

After including sdk reference on your project you need to get your api key on [BLiP portal][3]. Go to the left menu and access `Publications > Blip Chat`. You will also need to add your Android Application Id on the `Domains` section, in order to enable your chatbot in your app.

### Opening the BLiP conversation widget

To open a new thread is very simple. Use **BlipClient** helper class and call *openBlipThread* method

```java
BlipClient.openBlipThread(context, "YOUR_API_KEY");
```

For instance, imagine that you want to establish a new conversation between your customer and your chatbot, when your MainActivity is loaded.

```java
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BlipClient.openBlipThread(context, "YOUR_API_KEY");
    }
}
```

## Advanced features

### Defining authentication type

BLiP Chat android sdk supports three different user authentication types. It is possible to define which authentication method will be used to identify your client.

* Guest - Users will receive a guest account to interact with the chatbot. In this mode the users have not message history.
* Login - Users will receive an account with his 'Name' and 'Email' (provided by the user) to interact with the chatbot. In this mode the users have not message history.
* Dev - Users will receive an account identified by developer to interact with the chatbot. User data must be provided passing a BlipOptions instance as parameter on *BlipClient.openThread* method. You can set 4 properties: `userIdentifier`, `userPassword`, `userName` and `userEmail`. `UserIdentifier` and `userPassword` are **required**. In this mode the users have message history.

To define what user authetication type use the AuthType enum on authType property of BlipOptions. Possible values for authType are: `AuthType.GUEST`, `AuthType.LOGIN` and `AuthType.DEV`.

Note: Guest type will be used as default If you do not define 'authType'.

```java
BlipOptions blipOptions = new BlipOptions();
blipOptions.setAuthType(AuthType.DEV);
blipOptions.setUserIdentifier("USER-IDENTIFIER");
blipOptions.setUserPassword("USER-PASSWORD");
blipOptions.setUserName("USER-NAME");
blipOptions.setUserEmail("USER-EMAIL");

BlipClient.openBlipThread(context, "YOUR_API_KEY", blipOptions);
```

### Hiding Menu

BLiP Chat android sdk has a menu that can be hidden. To do that you only need to set hideMenu property of BlipOptions. *This menu is visible by default.*

```java
BlipOptions blipOptions = new BlipOptions();
blipOptions.setHideMenu(true);

BlipClient.openBlipThread(context, "YOUR_API_KEY", blipOptions);
```

### Example 

Defining auth type DEV and hiding menu:

```java

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BlipOptions blipOptions = new BlipOptions();
        blipOptions.setAuthType(AuthType.DEV);
        blipOptions.setUserIdentifier("USER-IDENTIFIER");
        blipOptions.setUserPassword("USER-PASSWORD");
        blipOptions.setUserName("USER-NAME");
        blipOptions.setUserEmail("USER-EMAIL");
        blipOptions.setHideMenu(true);

        BlipClient.openBlipThread(context, "YOUR_API_KEY", blipOptions);
    }
}
```

### Support
-------

  Android 4.4 (Kitkat) or later

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


 [1]: https://blip.ai
 [2]: https://portal.blip.ai/#/docs/home
 [3]: http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22net.take%22
 [snap]: https://oss.sonatype.org/content/repositories/snapshots/
 
