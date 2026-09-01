# Decisions & known technical history

Fontes: `git log` (histórico de commits do módulo `src/`) e leitura direta do código/config atual.

## AGP 4.1.2 mantido; tentativa de upgrade para 8.1.4 revertida

Commit `c1459e6 bugfix: revert gradle version` reverte uma tentativa anterior de subir o Android Gradle Plugin. Vestígio dessa reversão ainda visível hoje em `src/build.gradle`: o comentário da primeira linha ficou corrompido, misturando texto do comentário original com uma linha de `classpath` referenciando AGP `8.1.4` que **não está ativa** (está dentro do comentário, comment-only). A linha `classpath` realmente ativa (dentro de `dependencies {}`) declara `4.1.2`. Ou seja: o build atual usa AGP `4.1.2`; a referência a `8.1.4` é resíduo textual morto do processo de revert, não uma configuração real. Não "corrigir" isso assumindo que 8.1.4 deveria estar ativo sem antes confirmar com o time — foi revertido de propósito.

## Dois modos de integração (Full Screen vs Embedded) — decisão de produto recente

Commit `ad730ef feat: add new way to open chat by embedding it in a portion of the screen like a side panel` introduziu `BlipChatFragment`/`openEmbeddedThread`, mantendo `openFullScreenThread`/`ThreadActivity` como modo original. A decisão de reuso foi centralizar a lógica de WebView em `ThreadService` (ver [architecture.md](./architecture.md)) em vez de duplicar entre Activity e Fragment.

## `hideMenu` depreciado sem remoção

`BlipOptions.setHideMenu(...)` é `@Deprecated` e virou no-op — mantido por compatibilidade binária com apps que já chamam esse setter, mas não tem mais efeito funcional. Não reativar sem entender por que foi desativado (não documentado no histórico investigado; tratar como "unknown" — ver seção abaixo).

## Versionamento do artefato publicado é semi-manual

`version` do Gradle (`"3.0." + BUILD_NUMBER`) e `PUBLISH_VERSION` (constante fixa no mesmo arquivo) **não estão amarrados um ao outro** — são duas fontes de versão independentes no mesmo `build.gradle`. Ao publicar uma nova versão, é preciso atualizar `PUBLISH_VERSION` manualmente (e o número no README). Histórico de commits mostra isso já ter causado incremento manual (`bbd4840 chore: upgrade sdk version from 3.0.00.7 to 3.0.00.8`).

## Migração de groupId `net.take` → `io.github.takenet`

O pacote Java permanece `net.take.blipchat` (não migrado — mudar o pacote quebraria compatibilidade binária de quem já integrou), mas o **artefato publicado** trocou de groupId para `io.github.takenet`, exigido pelo processo de verificação de propriedade de domínio do Maven Central/Sonatype para publicação sob namespaces de GitHub. Não confundir pacote Java com groupId Maven ao dar suporte a integrações.

## Emissão proativa de estado de teclado via `postMessage` (Energisa F7)

Commit `109a9b1 feat(webview): emit keyboard visibility state proactively to JS via postMessage`, seguido de `257a82a fix(webview): guard evaluateJavascript call with API level check for minSdkVersion 15 compatibility` (mesma branch, `feat/blip-chat-evolution-energisa`). Corrige item F7 do discovery Energisa (teclado virtual cobrindo o campo de input).

Mecanismo, em `ThreadService.setKeyboardVisibilityListener` (agora recebe também a referência de `WebView`, além de `Activity`/`WebAppInterface`):

- O `OnGlobalLayoutListener` já existente (compara `activityRootView.getRootView().getHeight()` com `activityRootView.getHeight()`, threshold de `200dp` via `Helpers.dpToPx`) continua atualizando `webAppInterface.setIsKeyboardOpen(isOpen)` a cada layout pass, como antes — isso é o polling que o JS já podia consultar sob demanda (`window.Android.isKeyboardOpen()`).
- **Novidade:** o listener agora também emite proativamente `window.postMessage({code:'KeyboardState', isOpen}, '*')` para o JS via `webView.evaluateJavascript(...)`, para que o JS não precise mais fazer polling.
- **Deduplicação:** um array de 1 posição (`final boolean[] lastEmittedKeyboardState = {false}`, truque padrão para capturar variável mutável em classe anônima) guarda o último estado emitido; o `postMessage` só dispara quando `isOpen` difere do último valor emitido — evita chamadas redundantes a cada layout pass (o `onGlobalLayout` dispara com muito mais frequência do que a real mudança de estado do teclado).
- **Guarda de compatibilidade (`257a82a`):** `WebView.evaluateJavascript(...)` só existe a partir de API 19 (`KITKAT`). Como o `minSdkVersion` do módulo é 15, a chamada é guardada por `Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT`; abaixo disso, cai para `webView.loadUrl("javascript:" + script)` (mecanismo mais antigo e sem retorno de valor, mas funcional para disparar o `postMessage`).

Local exato: `ThreadService.java`, método `setKeyboardVisibilityListener` (chamado a partir de `configureWebView`, apenas quando `context instanceof Activity` — ou seja, hoje só é ativado no fluxo que passa uma `Activity`; ver gap relacionado de E1 abaixo sobre `context instanceof ThreadActivity` vs `Activity` genérica).

## Débito técnico conhecido: captura de foto nativa (E1) quebra em modo Embedded com NPE

Investigado nesta sessão (branch `feat/blip-chat-evolution-energisa`), **não corrigido** — classificado como `NEEDS_PRODUCT_DECISION` (depende de confirmar se a Energisa usará o modo Embedded; ver também [architecture.md](./architecture.md) para os dois modos de integração).

Causa raiz confirmada lendo o código (não é hipótese):

- Em `ThreadService.configureWebView` (`ThreadService.java`), a linha `ThreadActivity threadActivity = (context instanceof ThreadActivity) ? (ThreadActivity) context : null;` só atribui uma referência de Activity quando o `context` é especificamente `ThreadActivity` (modo Full Screen) — **não** quando é uma `Activity` genérica (que é o que `BlipChatFragment` (modo Embedded) tipicamente fornece via `getActivity()`/`requireContext()`). Resultado: `threadActivity` é `null` no modo Embedded.
- Esse `threadActivity` (possivelmente `null`) é passado para `new CustomWebChromeClient(threadActivity, actualListener)` (`CustomWebChromeClient.java`), que guarda a referência no campo `activity` sem null-check.
- `CustomWebChromeClient.onShowFileChooser(...)` usa esse campo `activity` tanto para `ActivityCompat.requestPermissions(activity, ...)` (caminho de permissão não concedida, API ≥ 23) quanto, no fluxo normal, via `startChooserIntent(...)` → `activity.startActivityForResult(chooserIntent, FCR)`. Com `activity == null`, isso lança `NullPointerException` assim que o usuário toca em "tirar foto"/escolher arquivo dentro de um `BlipChatFragment`.
- Mesmo que o NPE fosse evitado, o resultado do chooser (`onActivityResult`) não teria onde ser entregue: `BlipChatFragment.java` (`net.take.blipchat.activities.BlipChatFragment`) **não sobrescreve `onActivityResult`** — só `ThreadActivity.java` o faz (usa `ThreadService.getLastCustomWebChromeClient()` para recuperar o `pathCallback`/`filePath`). Ou seja, o gap tem duas partes: (1) NPE imediato por falta da referência de Activity, e (2) mesmo com a referência, falta um receptor de `onActivityResult` no lado do Fragment.

Arquivos exatos envolvidos, para uma sessão futura não precisar reinvestigar:

- `src/blip-chat/src/main/java/net/take/blipchat/services/ThreadService.java` (método `configureWebView`, linha do `instanceof ThreadActivity`)
- `src/blip-chat/src/main/java/net/take/blipchat/webview/CustomWebChromeClient.java` (construtor, campo `activity`, método `onShowFileChooser`, `startChooserIntent`)
- `src/blip-chat/src/main/java/net/take/blipchat/activities/BlipChatFragment.java` (ausência de `onActivityResult`)

Caminho de correção não implementado ainda (a explorar quando houver decisão de produto): passar a `Activity` hospedeira genérica (via `getActivity()` do Fragment, propagada até `configureWebView`) em vez de depender de `instanceof ThreadActivity`, e adicionar `onActivityResult` em `BlipChatFragment` delegando para `ThreadService.getLastCustomWebChromeClient()`, com atenção ao risco de estado estático compartilhado já documentado em [architecture.md](./architecture.md#fato-chave-activity-e-fragment-reusam-a-mesma-lógica).

## Unknowns (não confirmados nesta investigação)

- Por que `hideMenu` foi desativado (não há commit explicativo encontrado).
- Se o wrapper Gradle 3.5 dentro de `src/blip-chat/gradle/` é referenciado por algum processo externo (ex.: script de publicação antigo fora deste repo) antes de ser considerado seguro para remoção.
- Se existe pipeline de CI/CD externo (fora deste repositório, ex. Azure DevOps configurado via UI sem arquivo YAML versionado) — não foi encontrado nenhum arquivo de pipeline no repositório.
