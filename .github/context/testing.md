# Testing

## Estado atual: cobertura mínima, majoritariamente placeholders

- `blip-chat/src/test/.../HelpersUnitTests.java` — **único teste com valor real**: usa Robolectric (`RobolectricTestRunner`) para validar `Helpers.serializeExtras(Map)` (serialização de `extras` para JSON, removendo entradas nulas).
- `blip-chat/src/androidTest/.../ExampleInstrumentedTest.java` — teste instrumentado gerado por template do Android Studio, não customizado (verifica apenas `getContext().getPackageName()`).
- `app/src/test/.../ExampleUnitTest.java` — teste unitário gerado por template (`assertEquals(4, 2+2)`), sem valor funcional.

## Frameworks disponíveis (declarados em `blip-chat/build.gradle`)

- JUnit 4 (`testImplementation 'junit:junit:4.12'`)
- Mockito 1.10.19 (`org.mockito:mockito-core`)
- Robolectric 3.1 (`org.robolectric:robolectric`)
- Espresso 2.2.2 (`androidTestImplementation`, para testes instrumentados)
- `testOptions { unitTests.returnDefaultValues = true }` — permite chamar métodos do framework Android sem mockar tudo (retornam valores default em vez de lançar exceção).

## Lacuna conhecida

Não há testes cobrindo `BlipClient`, `ThreadService`, `ThreadActivity`, `BlipChatFragment`, os `webview/*Client`s, nem os modelos de serialização Gson (`BlipOptions`, `AuthConfig`, `Account`). Qualquer mudança nesses componentes hoje depende de teste manual (rodando o app `app`/sandbox). Não há suíte de regressão automatizada a rodar antes de publicar uma nova versão.
