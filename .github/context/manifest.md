# Context Manifest — blip-chat-android

Índice do conhecimento permanente deste repositório. Leia primeiro este arquivo para saber onde encontrar cada tipo de informação.

| Arquivo                              | Conteúdo                                                                                      |
| ------------------------------------ | --------------------------------------------------------------------------------------------- |
| [project.md](./project.md)           | O que é o SDK, para quem serve, formas de distribuição, versão publicada.                     |
| [architecture.md](./architecture.md) | Módulos Gradle, camadas internas do SDK, fluxo Activity vs Fragment, ponte WebView↔JS.        |
| [repository.md](./repository.md)     | Layout de pastas, particularidades da estrutura (ex.: duplicidade de Gradle Wrapper).         |
| [conventions.md](./conventions.md)   | Convenções de pacote/nome, padrões de código usados no SDK.                                   |
| [development.md](./development.md)   | Como buildar, assinar e publicar o artefato no Maven Central.                                 |
| [testing.md](./testing.md)           | O que existe de testes (unitário/instrumentado) e o que não existe.                           |
| [integrations.md](./integrations.md) | Firebase (app de exemplo), Maven Central, BLiP Chat Web Widget (via WebView).                 |
| [decisions.md](./decisions.md)       | Decisões e reversões técnicas relevantes (versão do AGP, modos de integração, versionamento). |
| [roadmap.md](./roadmap.md)           | Itens do discovery Energisa relevantes a este SDK (F7 corrigido após correção de blocker de relay para o iframe, E1 pendente de decisão). |

## Escopo

Este contexto cobre **apenas** `blip-chat-android` (pasta `src/` deste repo, que contém o projeto Gradle raiz com os módulos `app` e `blip-chat`). Diretórios de build/output (`build/`, `.gradle/`, `generated/`, `intermediates/`, `outputs/`, `tmp/`) foram ignorados por serem gerados e não representarem conhecimento estável.

## Como manter

Ao alterar `src/blip-chat` (o SDK publicado) ou `src/app` (app de exemplo/sandbox), revalide se `architecture.md`, `development.md` ou `decisions.md` continuam corretos. Rode o modo SYNC do Context Engineer após qualquer mudança aprovada nesses módulos.
