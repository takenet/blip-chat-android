# Project — BLiP Chat for Android

## O que é

SDK Android que embute o widget web do BLiP Chat (`blip-chat-widget`) dentro de um app Android nativo, via `WebView`. Não é um cliente de chat nativo: a UI de conversa é HTML/JS renderizado dentro do app, e o SDK cuida da ponte entre o app nativo e essa página (autenticação, presença, upload/download de arquivo, geolocalização, teclado).

Publicado como biblioteca open source no Maven Central:

```groovy
implementation 'io.github.takenet:blipchat:3.0.01.2'
```

Grupo/artefato: `io.github.takenet:blipchat` (histórico antigo usava `net.take`, migrado para `io.github.takenet` para publicação no Maven Central via Sonatype/Central Portal).

## Para quem serve

Times de apps Android (Java, `minSdkVersion 15+`) que já usam o BLiP para atendimento/chatbot e querem embutir a conversa no próprio app, sem abrir navegador externo.

## Duas formas de uso (fato central da API pública)

1. **Full Screen Mode** — `BlipClient.openFullScreenThread(context, appKey[, blipOptions])` abre uma `Activity` (`ThreadActivity`) dedicada em tela cheia.
2. **Embedded Mode** — `BlipClient.openEmbeddedThread(appKey[, blipOptions])` retorna um `Fragment` (`BlipChatFragment`) que o app hospedeiro anexa em qualquer container de layout, com controle manual de presença (`setPresence(boolean)`).

Ambos os modos compartilham a mesma lógica de montagem da WebView através de `ThreadService.setupChatView(...)` (ver [architecture.md](./architecture.md)).

## Autenticação

Dois `AuthType`: `Guest` (conta anônima) e `Dev` (conta integrada, exige `userIdentity`/`userPassword` em `AuthConfig`). Validação de obrigatoriedade ocorre em `BlipClient.validateSdkConfiguration`.

## Estrutura de distribuição

- Módulo publicável: `src/blip-chat` (Android library, `com.android.library`).
- Módulo de exemplo/sandbox: `src/app` (Android application, consome `src/blip-chat` via `implementation project(':blip-chat')`; a linha do Maven Central fica comentada no `build.gradle` do app como referência).

## Repositórios irmãos (mesmo produto, outras plataformas)

No workspace multi-root existem os equivalentes para outras plataformas: `blip-chat` (widget web em Vue), `blip-chat-widget` (o script/widget JS embutido via WebView por este SDK), `blip-chat-ios` (SDK iOS). Este SDK Android carrega o mesmo widget JS (`blip-chat-widget`) dentro da WebView — não reimplementa a UI de chat nativamente.
