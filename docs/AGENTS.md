# AGENTS.md — Pierre (backend)

Instruções para agentes de IA que trabalham neste repositório. Leia este arquivo inteiro antes de começar e consulte `requisitos.md` para o que deve ser construído.

## Projeto

Pierre é uma plataforma de estudo gamificado voltado para área de STEM. Este repositório reune backend, frontend e documentação. 

Frontend e Backend estão organizados em `src/frontend` e `src/backend` respectivamente.

A documentação está em `docs/`.

## Cronograma

- Pequeno incremento em 19/11 englobando funcionalidades de login e cadastro.
- Demais funcionalidades serão adicionadas futuramente.

## Comandos

### Backend

- Rodar a aplicação: `./mvnw spring-boot:run` (`mvnw.cmd` no Windows)
- Rodar os testes: `./mvnw test`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- Java 21 é obrigatório. Se o JDK ativo for outro, avise em vez de contornar.

## Stack e decisões já tomadas

Não reabra estas decisões; se algo parecer errado, registre em "Pontos em aberto" de `requisitos.md` e siga.

- Monolito modular em Spring Boot, um pacote por módulo (`br.pierre.pierre.<modulo>`), mais `br.pierre.pierre.compartilhado`.
- Subpacotes de cada módulo: `controller`, `service`, `repository`, `model`, `dto`.
- Spring Data JPA com **H2** neste incremento; o Hibernate gera o esquema (`ddl-auto=create-drop`). O MariaDB entra depois.
- API REST em JSON, prefixo `/api/v1`, documentada com springdoc.
- Autenticação por **sessão no servidor** (cookie), Spring Security e BCrypt.
- Maven com Maven Wrapper.

## O que não fazer no incremento de 19/11

- Não adicionar Flyway, Docker, docker-compose, CI, ArchUnit, JWT, refresh token nem login com Google.
- Não criar módulos além de `identidade` e `compartilhado`.
- Não adicionar dependências sem necessidade clara; diga qual problema a dependência resolve.
- Não implementar nada que não esteja em `requisitos.md`. Se faltar uma regra, adote a hipótese mais simples, registre-a em "Pontos em aberto" e avise no resumo final.

## Convenções de código

- Controllers recebem e devolvem DTOs (records). Entidades JPA nunca saem pela API.
- Regras de negócio ficam no `service` ou na entidade, nunca no controller.
- O usuário logado vem sempre do `Authentication`, nunca do caminho nem do corpo da requisição.
- Datas e horas com `java.time` e um `Clock` injetado nos serviços.
- Classes em português sem acento (`Usuario`, `UsuarioService`); colunas em `snake_case`; chave primária `cod_<entidade>`.
- Enums Java gravados como VARCHAR.
- Erros de negócio viram exceções próprias, traduzidas em um único `@RestControllerAdvice` para `ProblemDetail` (RFC 7807), com mensagens em português.
- Cada tabela pertence a um módulo, e só o módulo dono grava nela.
- Comente o porquê de decisões não óbvias, em português. Não comente o óbvio.

## Segurança

- Nunca registrar senha nem hash em log; nunca devolver `senha_hash` em resposta.
- Nenhum segredo, chave ou credencial no repositório. Segredos reais ficam em variáveis de ambiente, e só um `.env.example` com valores fictícios pode ir para o Git.
- CSRF está desativado neste incremento por causa do Swagger UI. Mantenha o `TODO` visível no código para reativar quando existir front-end.

## Testes

- Todo comportamento novo vem com teste. Os testes exigidos do incremento estão na seção 8 de `requisitos-incremento.md`.
- Testes unitários não sobem o Spring; testes de integração usam MockMvc e o H2.
- Rode `./mvnw test` antes de dizer que terminou, e informe o resultado real, inclusive falhas.

## Git (proposta, ainda não validada pelo grupo)

- A branch `main` deve sempre compilar e passar nos testes. Não faça push direto na `main`.
- Branches curtas, nomeadas pelo requisito: `feature/RF01-cadastro`, `fix/RF02-sessao`.
- Commits no formato `tipo(modulo): descrição (RFxx)`, por exemplo `feat(identidade): registra cadastro de usuário (RF01)`.
- Mudanças pequenas e focadas: um assunto por pull request.

## Como trabalhar

1. Leia `requisitos.md` e o código existente antes de editar.
2. Para tarefas grandes, proponha um plano curto e espere confirmação antes de escrever muito código.
3. Ao terminar, resuma o que mudou, o que foi testado e as hipóteses que adotou. Não recapitule cada passo.
4. Se um pedido contradisser este arquivo ou os requisitos, aponte a contradição em vez de escolher um lado em silêncio.

## Referências no repositório

- `requisitos.md`: o que construir
- `README.md`: como rodar o projeto (a ser mantido atualizado)
- `docs/`: ERS, DER e decisões de arquitetura