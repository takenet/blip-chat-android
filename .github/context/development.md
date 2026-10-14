# Development

## Sem CI/CD neste repositório

Diferente de `blip-chat` e `blip-chat-widget` (que têm `azure-pipeline(s).yml`), **este repositório não tem pipeline de CI/CD configurado** (nenhum arquivo `.yml`/`.yaml` de pipeline encontrado). Build, teste e publicação são feitos manualmente/local, via Gradle wrapper. A publicação para Maven Central é feita rodando o Gradle task diretamente (ver commit `de684d7 feat: add Maven Central Repository publish pipeline via ./gradlew :blip-chat:uploadToCentral`), não por um pipeline hospedado.

## Comandos essenciais (executar a partir de `src/`)

```bash
cd src
./gradlew :blip-chat:assembleRelease
./gradlew :blip-chat:publishReleasePublicationToMavenLocal
./gradlew generateChecksums          # depende das duas tasks acima
```

Usar sempre o wrapper de `src/` (raiz do projeto Gradle), não o wrapper vestigial dentro de `src/blip-chat/` (ver [repository.md](./repository.md)).

## Assinatura e publicação (módulo `blip-chat`)

`build.gradle` do módulo `blip-chat` aplica `maven-publish` e `signing`, e publica no Maven Central via endpoint `https://central.sonatype.com/api/v1/publisher/upload`. Credenciais/segredos (`signing.keyId`, `signing.password`, `signing.secretKeyRingFile`, `ossrhUsername`, `ossrhPassword`, `ossrhBearerToken`) são carregados de `local.properties` (se existir, não versionado — está no `.gitignore`) ou, como fallback, de variáveis de ambiente (`SIGNING_KEY_ID`, `SIGNING_PASSWORD`, `SIGNING_SECRET_KEY_RING_FILE`, `OSSRH_USERNAME`, `OSSRH_PASSWORD`, `OSSH_BEARER_TOKEN` — reparar que essa última variável tem grafia "OSSH" e não "OSSRH", provável typo histórico que deve ser preservado por compatibilidade a menos que se decida corrigir deliberadamente em todos os lugares que a referenciam).

Nunca commitar segredos reais em `local.properties` nem em `build.gradle`.

## Versionamento

- `version` do módulo Gradle: `"3.0." + BUILD_NUMBER` (variável de ambiente `BUILD_NUMBER`, ausente ⇒ vira o literal `"-SNAPSHOT"` por causa de precedência de operador — ver [decisions.md](./decisions.md)).
- `PUBLISH_VERSION` (usado na publicação Maven) é uma constante fixa (`'3.0.01.2'` no momento desta análise) definida manualmente no `build.gradle` — **não é derivada automaticamente de `version`/`BUILD_NUMBER`**. Ao lançar uma nova versão, atualizar `PUBLISH_VERSION` manualmente e o README (`implementation 'io.github.takenet:blipchat:X.Y.Z'`).

## App de exemplo (`app` module)

Usa `signingConfigs` com keystore local `blip-chat-key.jks` (não versionado) e `BuildConfig.APPKEY` placeholder `"YOUR_APP_KEY_HERE"` — precisa ser preenchido localmente para rodar o sandbox contra um bot real.

## AGP / Gradle

- AGP declarado: `4.1.2` (root `src/build.gradle`).
- Gradle Wrapper raiz: `6.5`.
- `compileSdkVersion`/`targetSdkVersion`: `26` no módulo `app`, `33` no módulo `blip-chat`. `minSdkVersion 15` em ambos.

Ver [decisions.md](./decisions.md) para o histórico de uma tentativa de upgrade do AGP que foi revertida e deixou um resíduo de texto corrompido no `build.gradle` raiz.
