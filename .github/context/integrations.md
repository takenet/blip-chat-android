# Integrations

## BLiP Chat Web Widget (`blip-chat-widget`) — integração principal

O SDK Android não implementa UI de chat nativa. Ele carrega, dentro de uma `WebView`, o script hospedado publicamente em `https://unpkg.com/blip-chat-widget` (`BuildConfig.SCRIPT_SDK_URL`, sobrescrevível via `blipOptions.customWidgetUrl`) dentro do template `BlipSdkTemplate.html`. A comunicação nativo↔web se dá por:

- Placeholders substituídos em runtime no HTML (`%appKey%`, `%authConfig%`, `%account%`, `%connectionData%`, `%commonUrl%`, `%scriptSdkUrl%`, `%blipChatUrl%`).
- `WebAppInterface` (`@JavascriptInterface`) exposta ao JS da página (estado de teclado).
- Chamadas JS diretas via `webView.evaluateJavascript("setPresence(true/false)", null)` para sinalizar presença do usuário ao entrar/sair de foco (Activity `onResume`/`onPause`, Fragment idem, ou manualmente via `BlipChatFragment.setPresence(...)`).

Qualquer mudança de contrato no `blip-chat-widget` (ex.: nome da função `setPresence`, nomes dos placeholders esperados no HTML/JS) impacta diretamente este SDK — os dois repositórios evoluem acoplados ainda que sejam publicados/versionados de forma independente.

## Firebase Cloud Messaging — apenas no app de exemplo

`app` module depende de `com.google.firebase:firebase-messaging:15.0.2` e do plugin `com.google.gms.google-services`, configurado com `google-services.json` do projeto Firebase `blip-chat-test` (sandbox, não produção). Usado somente para demonstrar, no README, como encaminhar push token para o BLiP via `Account.extras["#inbox.forwardTo"]`. **Não é dependência do SDK publicado** (`blip-chat` module não referencia Firebase).

## Maven Central (Sonatype / Central Portal)

O módulo `blip-chat` publica artefatos (`aar` + sources jar) assinados no Maven Central sob o groupId `io.github.takenet`. Ver [development.md](./development.md) para o fluxo e variáveis de credencial.

## Lime Protocol / BLiP backend

`Account.java` espelha o schema `application/vnd.lime.account+json` do Lime Protocol (mesmo protocolo usado pelo backend BLiP/chatbot). Trocas de schema no lado do protocolo devem ser refletidas manualmente nesse modelo — não há geração automática de schema.
