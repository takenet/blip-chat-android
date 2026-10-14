# Conventions

## Nomes de pacote não seguem o nome da pasta do módulo

- Pasta `src/blip-chat` (Gradle module `:blip-chat`) → pacote Java `net.take.blipchat`.
- Pasta `src/app` (Gradle module `:app`) → pacote/applicationId `net.take.blip_sdk_webview`.

Não assumir que renomear a pasta do módulo muda o pacote, nem o contrário.

## Estilo de código do SDK

- Java puro (sem Kotlin), compatibilidade `sourceCompatibility/targetCompatibility = 1.8`.
- API pública exposta via classe final com métodos estáticos (`BlipClient`) — padrão fachada, não há injeção de dependência nem singleton instanciável.
- Modelos de dados (`models/`) são POJOs `Serializable`, sem validação própria; toda validação de obrigatoriedade fica centralizada em `BlipClient.validateSdkConfiguration`.
- Serialização para passar dados entre componentes Android (`Intent`/`Fragment` `Bundle`) é sempre JSON via Gson, nunca `Parcelable`.
- `Builder` pattern usado só em `BlipWebViewBuilder` (configuração incremental da `WebView`).
- Compatibilidade de API antiga é preservada com `@Deprecated` + método no-op (ex.: `BlipOptions.setHideMenu`), em vez de remoção — manter esse padrão ao evoluir a API pública.

## Versionamento do artefato publicado

Ver [decisions.md](./decisions.md) para o esquema de versão (`3.0.<BUILD_NUMBER>`) e a divergência entre `version` (Gradle) e `PUBLISH_VERSION` (Maven).
