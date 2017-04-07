
Blip SDK for Android
======

SDK to easily add BLiP conversations in your Android app. For more information see [BLiP portal][1] and [BLiP documentation][2].

Installation
--------

Grab jar via Gradle:
```groovy
compile 'net.take:blip-sdk:0.0.17'
```

or Maven:
```xml
<dependency>
  <groupId>net.take</groupId>
  <artifactId>blip-sdk</artifactId>
  <version>0.0.17</version>
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

After including sdk reference on your project you need to get your api key on [BLiP portal][3]. Go to the left menu and access `Publications > Blip Chat`.

### Opening a new BLiP conversation

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

BLiP Android SDK supports three different user authentication types. It is possible to define which authentication method BLiP SDK will use to identify your client.

* Guest - Users will receive a guest account to interact with the chatbot. In this mode the users have not message history.
* Login - Users will receive an account with his 'Name' and 'Email' (provided by the user) to interact with the chatbot. In this mode the users have not message history.
* Dev - Users will receive an account identified by developer to interact with the chatbot. User data must be provided passing a BlipOptions instance as parameter on *BlipClient.openThread* method. You can set 4 properties: `userIdentifier`, `userPassword`, `userName` and `userEmail`. `UserIdentifier` and `userPassword` are **required**. In this mode the users have message history.

To define what user authetication type use the AuthType enum on authType propertie of BlipOptions. Possible values for authType are: `AuthType.GUEST`, `AuthType.LOGIN` and `AuthType.DEV`.

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

For instance,

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

        BlipClient.openBlipThread(context, "YOUR_API_KEY", blipOptions);
    }
}
```
To see more details about authentication types [click here][4].

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
 [4]: 
 [snap]: https://oss.sonatype.org/content/repositories/snapshots/
 
