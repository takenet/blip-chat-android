# Roadmap de produto vs. estado da arquitetura

## Proveniência

Origem: discovery do cliente Energisa (mesmo discovery documentado em
`blip-chat-widget/.github/context/roadmap.md`). Este arquivo registra
**apenas** os itens desse discovery que têm implementação real neste SDK
Android (`net.take.blipchat`) — a maioria dos itens do discovery vive na UI
web do iframe (`blip-chat-widget`) ou no app React (`blip-chat`), fora deste
repositório, e por isso não está listada aqui.

## Itens relevantes a este SDK

| Item | Descrição                                 | Estado                                                                                                                                                                                                                                                                                                                                                                                                         |
| ---- | ----------------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| F7   | Teclado virtual cobrindo o campo de input | **Corrigido** — commits `109a9b1` e `257a82a`. `ThreadService` agora emite `postMessage({code:'KeyboardState', isOpen})` proativamente ao JS, deduplicado, com guarda de API level. Ver [decisions.md](./decisions.md#emissão-proativa-de-estado-de-teclado-via-postmessage-energisa-f7).                                                                                                                      |
| E1   | Captura de foto nativa                    | `NEEDS_PRODUCT_DECISION` — funciona no modo Full Screen (`ThreadActivity`), quebra com `NullPointerException` no modo Embedded (`BlipChatFragment`). Causa raiz e arquivos exatos documentados em [decisions.md](./decisions.md#débito-técnico-conhecido-captura-de-foto-nativa-e1-quebra-em-modo-embedded-com-npe). Depende de confirmação se a Energisa usará o modo Embedded antes de investir na correção. |

## Demais itens do discovery (F1–F6, E2–E5, N1–N8)

Não se aplicam a este SDK Android — pertencem à UI do iframe/widget web ou
ao app React `blip-chat`. Consultar
`blip-chat-widget/.github/context/roadmap.md` para o mapeamento completo.
