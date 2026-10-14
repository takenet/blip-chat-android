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

Commit `109a9b1 feat(webview): emit keyboard visibility state proactively to JS via postMessage`, seguido de `257a82a fix(webview): guard evaluateJavascript call with API level check for minSdkVersion 15 compatibility` (mesma branch, `feat/blip-chat-evolution-energisa`). Tentativa de corrigir o item F7 do discovery Energisa (teclado virtual cobrindo o campo de input).

**Blocker real encontrado em auditoria final e corrigido por `1744011 fix(webview): relay keyboard state postMessage into the chat iframe instead of the main frame`** — ver subseção "Correção do blocker" abaixo. Não tratar `109a9b1`/`257a82a` isoladamente como "F7 corrigido"; a versão funcional é a pós-`1744011`.

Mecanismo, em `ThreadService.setKeyboardVisibilityListener` (agora recebe também a referência de `WebView`, além de `Activity`/`WebAppInterface`):

- O `OnGlobalLayoutListener` já existente (compara `activityRootView.getRootView().getHeight()` com `activityRootView.getHeight()`, threshold de `200dp` via `Helpers.dpToPx`) continua atualizando `webAppInterface.setIsKeyboardOpen(isOpen)` a cada layout pass, como antes — isso é o polling que o JS já podia consultar sob demanda (`window.Android.isKeyboardOpen()`).
- **Novidade:** o listener agora também emite proativamente um `postMessage` com `code: 'KeyboardState'`/`isOpen` para o JS via `webView.evaluateJavascript(...)`, para que o JS não precise mais fazer polling.
- **Deduplicação:** um array de 1 posição (`final boolean[] lastEmittedKeyboardState = {false}`, truque padrão para capturar variável mutável em classe anônima) guarda o último estado emitido; o `postMessage` só dispara quando `isOpen` difere do último valor emitido — evita chamadas redundantes a cada layout pass (o `onGlobalLayout` dispara com muito mais frequência do que a real mudança de estado do teclado).
- **Guarda de compatibilidade (`257a82a`):** `WebView.evaluateJavascript(...)` só existe a partir de API 19 (`KITKAT`). Como o `minSdkVersion` do módulo é 15, a chamada é guardada por `Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT`; abaixo disso, cai para `webView.loadUrl("javascript:" + script)` (mecanismo mais antigo e sem retorno de valor, mas funcional para disparar o `postMessage`).

Local exato: `ThreadService.java`, método `setKeyboardVisibilityListener` (chamado a partir de `configureWebView`, apenas quando `context instanceof Activity` — ou seja, hoje só é ativado no fluxo que passa uma `Activity`; ver gap relacionado de E1 abaixo sobre `context instanceof ThreadActivity` vs `Activity` genérica).

### Correção do blocker: `postMessage` no frame principal nunca alcançava o iframe do widget (`1744011`)

**BLOCKER real, encontrado em auditoria final de completude e corrigido/revisado nesta sessão.** A versão `109a9b1`/`257a82a` emitia `window.postMessage({code:'KeyboardState', isOpen}, '*')` via `webView.evaluateJavascript(...)` — isso executa no **frame principal** carregado pelo `WebView` (o próprio `BlipSdkTemplate.html`), não dentro do `<iframe id="blip-chat-iframe">` que esse template embute e onde o widget web real (`blip-chat-widget` → `App.vue` do `blip-chat`) roda e escuta `message` events. Resultado: a mensagem nunca chegava ao destino — o `postMessage` "no vazio", F7 continuava quebrado apesar do código parecer correto.

Correção (`1744011 fix(webview): relay keyboard state postMessage into the chat iframe instead of the main frame`):

- Nova função `setKeyboardOpen(isOpen)` em `BlipSdkTemplate.html`, no mesmo padrão já usado por `setPresence` (código pré-existente que já fazia esse relay corretamente para outro propósito): busca `document.getElementById('blip-chat-iframe')` e chama `blipChatIframe.contentWindow.postMessage({code: 'KeyboardState', isOpen: isOpen}, "%blipChatUrl%")` — com `targetOrigin` real (placeholder `%blipChatUrl%` preenchido no build do template), não `'*'`.
- `ThreadService.java` deixou de montar o `postMessage` diretamente; agora o script injetado via `evaluateJavascript`/`loadUrl` é só `"setKeyboardOpen(" + isOpen + ")"`, delegando o relay para dentro do iframe à função do template.
- Revisado por um Reviewer independente nesta sessão: `BLOCKER RESOLVIDO`/`APPROVED`, incluindo confirmação de que o contrato (`code: 'KeyboardState'`, campo `isOpen`) é exatamente o que o listener em `blip-chat/App.vue` espera do lado do widget web.

**Conhecimento permanente (aplica-se a qualquer comunicação nativo→JS futura neste SDK):** `WebView.evaluateJavascript(...)` (e `loadUrl("javascript:...")`) executa **apenas no frame principal** carregado pelo `WebView` — nunca propaga automaticamente para dentro de um `<iframe>` filho. `BlipSdkTemplate.html` carrega o widget real (`blip-chat-widget`) dentro de um iframe (`blip-chat-iframe`), então qualquer mensagem que precise chegar ao **conteúdo do iframe** (e não ao template SDK em si) exige uma função de relay explícita no template — buscar o iframe por `getElementById` e chamar `contentWindow.postMessage(...)` com `targetOrigin` real — como `setPresence`/`setKeyboardOpen` já fazem. Nunca assumir que `window.postMessage` disparado no frame principal alcança o iframe.

## E1 (captura de foto nativa): corrigido em ambos os modos (Full Screen e Embedded)

**Decisão de produto:** modo Embedded (E1) DEVE funcionar corretamente — não será usado pela Energisa, mas serve outros clientes do SDK. Esta decisão substitui e revoga qualquer classificação anterior como `NEEDS_PRODUCT_DECISION` ou "fora de escopo"/"não será corrigido" para o modo Embedded.

Causa raiz original (confirmada lendo o código, branch `feat/blip-chat-evolution-energisa`):

- Em `ThreadService.configureWebView` (`ThreadService.java`), a linha `ThreadActivity threadActivity = (context instanceof ThreadActivity) ? (ThreadActivity) context : null;` só atribuía uma referência de Activity quando o `context` era especificamente `ThreadActivity` (modo Full Screen) — não quando era uma `Activity` genérica (o que `BlipChatFragment`, modo Embedded, tipicamente fornece via `getActivity()`/`requireContext()`). Resultado: `threadActivity` era `null` no modo Embedded.
- Esse `threadActivity` (possivelmente `null`) era passado para `new CustomWebChromeClient(threadActivity, actualListener)` (`CustomWebChromeClient.java`), que guardava a referência no campo `activity` sem null-check.
- `CustomWebChromeClient.onShowFileChooser(...)` usava esse campo `activity` tanto para `ActivityCompat.requestPermissions(activity, ...)` (permissão não concedida, API ≥ 23) quanto, no fluxo normal, via `startChooserIntent(...)` → `activity.startActivityForResult(chooserIntent, FCR)`. Com `activity == null`, isso lançava `NullPointerException` assim que o usuário tocava em "tirar foto"/escolher arquivo dentro de um `BlipChatFragment`.
- Mesmo evitando o NPE, o resultado do chooser (`onActivityResult`) não teria onde ser entregue: `BlipChatFragment.java` não sobrescrevia `onActivityResult` — só `ThreadActivity.java` o fazia.

### Rodada 1 — `a152165 fix(webview): support file chooser and camera capture in Embedded mode`

Eliminou o NPE generalizando `CustomWebChromeClient`/`ThreadService` de `ThreadActivity` para `Activity` genérica, e adicionou `BlipChatFragment.onActivityResult`.

**Blockers residuais encontrados por um Reviewer nesta rodada** (confirmados via decompilação de bytecode de `support-fragment:26.1.0`, não hipótese): `startActivityForResult`/`requestPermissions` chamados na `Activity` bruta **nunca roteavam de volta ao Fragment** — em modo Embedded, o chooser/permissão precisa ser disparado a partir do `Fragment` (que intercepta o resultado via seu próprio `onActivityResult`/`onRequestPermissionsResult` antes de delegar à Activity), não da `Activity` diretamente.

### Rodada 2 — `b8eafce fix(webview): route file chooser and permission requests through the Fragment in Embedded mode`

Corrigiu os 2 blockers propagando um `Fragment` opcional por toda a cadeia (`CustomWebChromeClient` → `ThreadService.setupChatView`/`configureWebView` → `BlipChatFragment`), usando `fragment.startActivityForResult`/`fragment.requestPermissions` quando em modo Embedded (em vez de `activity.startActivityForResult`/`ActivityCompat.requestPermissions`). Adicionado `BlipChatFragment.onRequestPermissionsResult` (novo), delegando a `ThreadService.handlePermissionsResult`.

**Confirmado `APPROVED_WITH_NOTES` por um Reviewer independente**, que validou por bytecode que o roteamento funciona e que o `requestCode` original chega correto ao Fragment.

### Rodada 3 — `38393c8 docs(readme): document FragmentActivity host requirement for Embedded mode`

README atualizado documentando o requisito de integração: a Activity hospedeira do modo Embedded deve ser `FragmentActivity`/`AppCompatActivity` e propagar `super.onActivityResult`/`onRequestPermissionsResult` (caso a Activity hospedeira sobrescreva esses métodos).

**`ThreadActivity.java` (modo Full Screen) não foi tocado em nenhuma das 3 mudanças** — continua funcionando exatamente como antes.

### Riscos residuais não bloqueantes (aceitos como pendência conhecida, não introduzidos por esta correção)

- **Estado estático compartilhado:** `ThreadService.lastCustomWebChromeClient` continua sendo um singleton `static` compartilhado (ver [architecture.md](./architecture.md#fato-chave-activity-e-fragment-reusam-a-mesma-lógica)) — se múltiplas instâncias coexistirem (Fragment + Activity simultâneas, ou múltiplos Fragments), o resultado pode ser roteado para o `CustomWebChromeClient` errado. Débito arquitetural pré-existente, não introduzido por esta correção.
- **Diálogo de permissão negada permanentemente ausente em Embedded:** o modo Embedded não replica o diálogo `showDialogPermissionDeniedPermanently` que existe em modo Full Screen — simplificação deliberada, UX secundária, não bloqueante.

Arquivos exatos envolvidos, para referência futura:

- `src/blip-chat/src/main/java/net/take/blipchat/services/ThreadService.java` (`configureWebView`, `setupChatView`, `handlePermissionsResult`)
- `src/blip-chat/src/main/java/net/take/blipchat/webview/CustomWebChromeClient.java` (construtor, campo `activity`/`fragment`, `onShowFileChooser`, `startChooserIntent`)
- `src/blip-chat/src/main/java/net/take/blipchat/activities/BlipChatFragment.java` (`onActivityResult`, `onRequestPermissionsResult`)

## Unknowns (não confirmados nesta investigação)

- Por que `hideMenu` foi desativado (não há commit explicativo encontrado).
- Se o wrapper Gradle 3.5 dentro de `src/blip-chat/gradle/` é referenciado por algum processo externo (ex.: script de publicação antigo fora deste repo) antes de ser considerado seguro para remoção.
- Se existe pipeline de CI/CD externo (fora deste repositório, ex. Azure DevOps configurado via UI sem arquivo YAML versionado) — não foi encontrado nenhum arquivo de pipeline no repositório.
