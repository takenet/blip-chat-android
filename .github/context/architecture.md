# Architecture (SDK module: `blip-chat` / package `net.take.blipchat`)

## Camadas

```
BlipClient (fachada estática, API pública)
   ├── openFullScreenThread(...)  → inicia ThreadActivity (extras: apiKey, options em JSON via Gson)
   └── openEmbeddedThread(...)    → cria BlipChatFragment (newInstance, args em JSON via Gson)
             │
             ▼
   ThreadService.setupChatView(rootView, appKey, blipOptions, listener)
             │  (código único e compartilhado entre Activity e Fragment)
             ▼
   BlipWebViewBuilder  → configura WebView (JS, download, geolocation, zoom, cache)
   CustomWebViewClient / CustomWebChromeClient → callbacks de navegação/permissão/upload
   WebAppInterface     → @JavascriptInterface exposta ao JS da página (ex.: isKeyboardOpen)
             │
             ▼
   BlipSdkTemplate.html (assets) com placeholders preenchidos (appKey, authConfig, account,
   connectionData, commonUrl, scriptSdkUrl, blipChatUrl) → carrega o widget web (blip-chat-widget)
   via webView.loadDataWithBaseURL(...)
```

## Fato-chave: Activity e Fragment reusam a mesma lógica

`ThreadActivity` (tela cheia) e `BlipChatFragment` (embutido) **não duplicam** a lógica de montagem da WebView — ambos chamam `ThreadService.setupChatView(...)`. `ThreadService` mantém estado estático (`lastCustomWebChromeClient`, `lastCustomWebViewClient`, `lastPageContent`, `lastBaseUrl`) usado por `ThreadActivity` para lidar com `onActivityResult` (upload de arquivo) e navegação (back stack do WebView).

Risco estrutural conhecido: esse estado estático é compartilhado entre instâncias — se Activity e Fragment forem abertos simultaneamente (ou múltiplos Fragments), o último `setupChatView` sobrescreve o estado usado pelos outros. Não há isolamento por instância.

Débito técnico relacionado (não corrigido): o upload de arquivo via `onShowFileChooser`/`CustomWebChromeClient` funciona apenas quando `context instanceof ThreadActivity` — em `BlipChatFragment` (modo Embedded) a referência de Activity passada a `CustomWebChromeClient` fica `null` e o fluxo lança `NullPointerException`. Detalhe completo, causa raiz e arquivos exatos em [decisions.md](./decisions.md#débito-técnico-conhecido-captura-de-foto-nativa-e1-quebra-em-modo-embedded-com-npe).

## Modelos (`models/`, todos `Serializable`, seriados via Gson e passados por `Bundle`/`Intent` como JSON string)

- `BlipOptions` — agrega `AuthConfig`, `Account`, `ConnectionDataConfig`, `customCommonUrl`, `customWidgetUrl`. `hideMenu` está `@Deprecated` e o setter é no-op (mantido só por compatibilidade binária).
- `AuthConfig` — `authType` (`AuthType.Guest` | `AuthType.Dev`), `userIdentity`, `userPassword` (obrigatórios apenas no modo `Dev`).
- `Account` — espelha o schema Lime Protocol `application/vnd.lime.account+json` (nome, email, telefone, extras, etc.), usado para autenticação integrada.
- `ConnectionDataConfig` — domain/hostName/port para apontar a um gateway BLiP customizado.

## Ativação/URLs

- `BuildConfig.BLIP_CHAT_URL` (default `https://chat.blip.ai/`) e `BuildConfig.SCRIPT_SDK_URL` (default `https://unpkg.com/blip-chat-widget`) são definidos como `buildConfigField` no `build.gradle` do módulo `blip-chat`, sobrescritos por `blipOptions.customCommonUrl`/`customWidgetUrl` quando presentes (usado para organizações com URL própria — ver README seção "Using Organization").

## Permissões e integrações nativas

Exigidas no app hospedeiro (não perguntadas automaticamente pelo SDK): `INTERNET`, `ACCESS_FINE_LOCATION` (geolocalização no fluxo do chatbot), `WRITE_EXTERNAL_STORAGE`/`READ_EXTERNAL_STORAGE`/`CAMERA` (upload/download de arquivo). `BlipChatProvider` (`FileProvider`) expõe arquivos temporários com autoridade `${applicationId}.blipchat.fileprovider`.

## App de exemplo (`app` module, pacote `net.take.blip_sdk_webview`)

`SandboxAppActivity` é a Activity de demonstração/manual QA do SDK; usa Firebase Cloud Messaging (`firebase-messaging`) apenas para exemplificar o encaminhamento de push (`#inbox.forwardTo` em `Account.extras`), não é parte da API pública do SDK.
