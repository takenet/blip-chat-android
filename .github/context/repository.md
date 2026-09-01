# Repository layout

## Particularidade importante: raiz Gradle fica em `src/`, não na raiz do repo

```
blip-chat-android/            <- raiz do repo Git
├── LICENSE, README.md, images/
└── src/                      <- raiz do projeto Gradle (settings.gradle aqui)
    ├── build.gradle           (root, buildscript: AGP + google-services classpath)
    ├── settings.gradle         include ':blip-chat', ':app'
    ├── gradlew / gradlew.bat / gradle/wrapper/   (wrapper raiz: Gradle 6.5)
    ├── app/                    módulo de exemplo/sandbox (com.android.application)
    │   ├── build.gradle
    │   ├── google-services.json   (Firebase do app de exemplo, projeto "blip-chat-test")
    │   └── src/{main,test,androidTest}/java/net/take/blip_sdk_webview/...
    └── blip-chat/              módulo do SDK publicado (com.android.library)
        ├── build.gradle        (versão, assinatura, publish ao Maven Central)
        ├── gradlew / gradlew.bat / gradle/wrapper/   (wrapper próprio: Gradle 3.5 — vestigial, não usado no build real)
        └── src/{main,test,androidTest}/java/net/take/blipchat/...
```

## Achado: dois Gradle Wrappers coexistindo

- `src/gradle/wrapper/gradle-wrapper.properties` → **Gradle 6.5** (usado de fato; combina com AGP declarado em `src/build.gradle`).
- `src/blip-chat/gradle/wrapper/gradle-wrapper.properties` → **Gradle 3.5** (datado de 2018, herança de quando `blip-chat` era projeto standalone antes de virar submódulo). Não é referenciado pelo build multi-módulo atual (o build usa apenas o wrapper raiz). Tratar como resíduo histórico — não removê-lo sem confirmar que nenhum processo de CI/publicação externo o invoca isoladamente (ver [decisions.md](./decisions.md)).

## Pacotes Java (por módulo)

- `blip-chat` → pacote `net.take.blipchat` (namespace do SDK publicado, estável — é a API pública).
- `app` → pacote `net.take.blip_sdk_webview` (aplicationId do app de exemplo, não confundir com o pacote do SDK).

## Diretórios ignorados neste contexto

`build/`, `.gradle/`, `.idea/`, `generated/`, `intermediates/`, `outputs/`, `tmp/` — gerados por build/IDE, sem valor de conhecimento permanente.

## Assets relevantes fora do código Java

- `src/blip-chat/src/main/assets/BlipSdkTemplate.html` — template HTML carregado na WebView; tem placeholders (`%appKey%`, `%authConfig%`, `%account%`, `%connectionData%`, `%commonUrl%`, `%scriptSdkUrl%`, `%blipChatUrl%`) substituídos em runtime por `ThreadService.generateHtmlPageContent`. É o ponto de integração com o widget JS (`blip-chat-widget`).
