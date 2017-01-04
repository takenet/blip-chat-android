
Blip SDK for Android
======

SDK to easly add BLiP conversations in your Android app. For more information see [BLiP portal][1] and [BLiP documentation][2].

Installation
--------

Grab jar via Gradle:
```groovy
compile 'net.take:blip-sdk:0.0.4'
```

or Maven:
```xml
<dependency>
  <groupId>net.take</groupId>
  <artifactId>blip-sdk</artifactId>
  <version>0.0.4</version>
  <type>pom</type>
</dependency>
```

or download [the latest JAR][3] and import in your app.

Snapshots of the development version are available in [Sonatype's `snapshots` repository][snap].

How to use `@blip-sdk-android`
-------------------------

## Quick start

### Setting your SDK

After to add sdk reference you must provide a valid BLiP account. *To get this account enter in contact with BLiP team.*
If you already have your account set your credentials on **local.properties** file like bellow

```groovy
blipsdk.ownerIdentity = <your-valid-identity>
blipsdk.ownerPassword = <your-valid-password>
```

### Open a new BLiP conversation

To open a new thread is very simple. Use **BlipClient** helper class and call *openBlipThread* method

```java
BlipClient.openBlipThread(context, "<your-chatbot-identifier>@msging.net");
```

For instance, imagine that you want establish a new conversation between your customer and your chatbot, when your MainActivity is loaded. 

```java
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BlipClient.openBlipThread(this, "chatbotsample@msging.net");
    }
}
```

## Advanced features

### Setting informations about your client

Sometimes, is very important that your chatbot knows informations about your customers, as name or some external identifier for example. 
To do this use *setUserAccount* method on **BlipClient** helper class.

```java
BlipClient.setUserAccount(context, new BlipAccount("Name", "PhotoUri", "ExternalId"));
```

For instance,

```java

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String customerName = "Blip SDK Test User";
        String customerPhotoUri = "http://i.imgur.com/8oL7Ol8.png";
        String customerIdentifier = UUID.randomUUID().toString();

        BlipAccount blipAccount = new BlipAccount(customerName, customerPhotoUri, customerIdentifier);

        BlipClient.setUserAccount(this, blipAccount);

        //Now, if you start some thread your chatbot will know some informations about your customers
        BlipClient.openBlipThread(this, "chatbotsample@msging.net");
    }
}
```

> TODO: Change complete node for only chatbot identifier
  TODO: Chaneg BlipAccount to add generic fields and add setters


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
