# Pierre - Arquitetura do Backend

> TODO: Detalhar arquitetura e substituir este arquivo após o incremento de 19/11

## Decisões de arquitetura

| Tema | Escolha | Por quê | Status |
| --- | --- | --- | --- |
| Estilo | Monolito modular, um único artefato implantável | Restrição do projeto; o grupo não tem experiência com microsserviços | Decidido |
| Modularização | Um pacote por módulo, sem Spring Modulith; JOIN permitido entre tabelas de módulos diferentes | Menos conceitos novos; RF08 e RF09 cruzam dados de vários módulos | Decidido |
| BFF | Fora por enquanto | A mesma API REST serve web e app nativo | Decidido |
| Banco | MariaDB; H2 no incremento de 19/11 | MariaDB é imposto pelo projeto; o H2 evita infraestrutura nos primeiros 15 dias | Decidido; MariaDB entra depois do incremento |
| Linguagem e build | Java 21 com Maven, usando o Maven Wrapper | Padrão do ecossistema Spring; o grupo tem máquinas com Java 21 e com Java 26, então a versão é fixada no `pom.xml` | Decidido |
| Acesso a dados | Spring Data JPA; esquema gerado pelo Hibernate no H2; Flyway entra junto com o MariaDB | Menos configuração em 15 dias; a tabela `usuarios` é a única do incremento | Decidido                                                                                                                                                                                                                              |
| API | REST em JSON, prefixo `/api/v1`, OpenAPI com springdoc | Sem front-end, o Swagger UI é a forma de demonstrar o incremento | Decidido |
| Autenticação | Sessão no servidor (cookie), Spring Security e BCrypt; somente e-mail e senha nesta entrega | O timeout de 30 minutos do RNF05 vira uma linha de configuração, sem refresh token | Decidido |
| Dependência entre módulos | Combinação do grupo, sem ArchUnit | O grupo não tem familiaridade com os pacotes que o ArchUnit exige | Decidido |
| Modelo de dados | Tabela `usuarios` completa, com as colunas do DER | É a única tabela que o login e o cadastro precisam | Decidido para o incremento |
| Contêineres | Dockerfile e docker-compose com MariaDB | Sem MariaDB no incremento, não há o que subir | Adiado |
| CI/CD | Build e testes a cada pull request | Pega erro cedo, mas não bloqueia a entrega de 19/11 | Construido |

## Estrutura de pacotes e convenções

Cada módulo tem uma pasta própria com os mesmos cinco subpacotes, nos nomes que o grupo já conhece, então quem abre qualquer módulo sabe onde procurar. No incremento de 19/11 só existem `identidade` e `compartilhado`; os demais módulos são criados quando alguém implementa o primeiro requisito deles.

```text
br.pierre
├── PierreApplication.java
├── compartilhado/        tratamento de erros, segurança, Clock
└── identidade/
    ├── controller/       endpoints REST
    ├── service/          regras de negócio
    ├── repository/       interfaces Spring Data
    ├── model/            entidades JPA e enums
    └── dto/              records de entrada e saída
```

Convenções para seguir desde o primeiro commit:

- Controllers recebem e devolvem DTOs (records do Java); entidades JPA nunca saem pela API.
- Regras de negócio ficam no `service` ou na entidade, nunca no controller.
- O usuário logado vem sempre do `Authentication` do Spring, nunca do caminho nem do corpo da requisição.
- Cada tabela pertence a um módulo, e só o módulo dono grava nela. Consultas de leitura com JOIN entre módulos são permitidas. Isso é um combinado do grupo, sem verificação automática.
- Área, nível, escolaridade e status são enums Java; no banco, VARCHAR.
- Datas e horas via `java.time`, com um `Clock` injetado nos serviços, para testar regras de tempo sem esperar o relógio real.
- Nomes: classes em português sem acento (como no DER), colunas em `snake_case`, chaves primárias no padrão `cod_<entidade>`.
- Todo erro de negócio vira uma exceção própria, traduzida em um único lugar para a resposta HTTP em ProblemDetail (RFC 7807).

## Módulos do monolito

São sete módulos que cobrem os 13 requisitos funcionais, e só `identidade` entra no incremento de 19/11. A divisão segue o que muda junto: pontos e streaks mudam por regras de gamificação, enquanto trilhas e vídeos mudam por conteúdo.

| Módulo | Responsabilidade | RFs | Tabelas principais | Em 19/11 |
| --- | --- | --- | --- | --- |
| `identidade` | Cadastro, login por e-mail e senha, sessão, exclusão de conta (LGPD) | RF01, RF02, RNF09 | `usuarios` | Cadastro e login |
| `diagnostico` | Questionário inicial por área, percentual de acerto, recomendação de trilhas | RF03, RF04 | `diagnosticos`, perguntas, respostas | Não |
| `catalogo` | Trilhas, conteúdos e vídeos; consulta e leitura | RF05, RF06 | `trilhas`, `conteudo`, `videos` | Não |
| `estudo` | Ciclo de estudo de 20 minutos, contagem de tempo, sugestão de pausa | RF06, RF11 | `ciclos_estudo` | Não |
| `avaliacao` | Quizzes, correção, resultado e feedback de desempenho | RF07, RF08 | `quizzes`, `questoes`, `resultados_quiz` | Não |
| `progresso` | Conteúdos concluídos, pontos e streaks | RF09, RF12, RF13 | `conteudos_concluidos`, `pontos`, `streaks` | Não |
| `flashcards` | Criação e consulta de flashcards do usuário | RF10 | `flashcards` | Não |

Regras de convivência entre módulos, depois da revisão:

- A regra de que um módulo só chama outro pela classe de serviço continua como combinado do grupo, sem verificação automática.
- A proibição de JOIN SQL entre módulos foi retirada. Cada entidade pode guardar só o id (por exemplo `cod_usuario`) da tabela de outro módulo, sem relação JPA direta.
- O pacote `compartilhado` não depende de nenhum módulo.

Duas decisões ficam para depois de 19/11, porque só pesam quando o trabalho chegar ao quiz:

- Juntar `estudo` e `avaliacao` em um módulo só. No MVP o ciclo de estudo serve apenas para liberar o quiz, e a pausa (RF11) é da Release 2.
- Manter o evento `ConteudoConcluidoEvent` entre `avaliacao` e `progresso` ou fazer `avaliacao` chamar `progresso` direto, na mesma transação. O evento é mais desacoplado; a chamada direta é mais fácil de entender e testar. Em qualquer caso, a conclusão do conteúdo é decidida no fim do quiz, e o registro de pontos fica na mesma transação.

## Modelo de dados

O incremento de 19/11 usa uma única tabela, `usuarios`, com todas as colunas que já existem no DER. O Hibernate gera o esquema no H2 a partir da entidade.

| Coluna | Tratamento no incremento |
| --- | --- |
| `cod_usuario` | Chave primária |
| `nome` | Obrigatório (RF01) |
| `email` | Obrigatório e único |
| `senha_hash` | Hash BCrypt; obrigatório para contas cadastradas por e-mail e senha |
| `escolaridade` | Opcional; enum Java gravado como VARCHAR |
| `curso` | Opcional |
| `status` | Enum com `ATIVO`, `EXCLUSAO_SOLICITADA` e `EXCLUIDO`; o cadastro grava `ATIVO` |
| `consentimento_lgpd_em` | Data e hora do consentimento, preenchida em todo cadastro (RNF09) |
| `versao_termos` | Versão dos termos aceita no cadastro |
| `data_criacao`, `data_atualizacao` | Preenchidas pela aplicação |
| `data_exclusao` | Marca o pedido de exclusão; sem uso até a exclusão de conta entrar |

Quando o MariaDB entrar, o primeiro passo é escrever o `V1__usuarios.sql` do Flyway a partir das entidades prontas e trocar o `ddl-auto` para `validate`. Por isso as entidades já usam os nomes do DER, e o script sai quase direto das anotações.

O que fica para as próximas entregas:

- Ajustes em tabelas existentes: `trilhas` ganha a área e um indicador de trilha comum; `conteudo` ganha o texto e aceita dificuldade nula; `videos` guarda o id do vídeo do YouTube em vez da URL; `diagnosticos` troca FLOAT por DECIMAL (ou guarda acertos e total) e recebe `cod_usuario` direto, eliminando `registro_diagnostico`.
- Tabelas novas: `ciclos_estudo`, `quizzes`, `questoes`, `alternativas`, `resultados_quiz`, `conteudos_concluidos`, `pontos` e `streaks`.
- A chave única (usuário, conteúdo) em `conteudos_concluidos` impede, no próprio banco, que o mesmo conteúdo pontue duas vezes, inclusive na conclusão simultânea em dois dispositivos (RF12).
- O streak é guardado como contador mais a data da última conclusão, com o dia calculado no fuso America/Sao\_Paulo.
- Exclusão de conta (RNF09): o prazo de até 15 dias exige uma rotina que apague ou anonimize os dados depois do pedido; o formato dessa rotina ainda não foi decidido.

## API, autenticação e regras de negócio

A API é REST em JSON com prefixo `/api/v1`, e o OpenAPI gerado pelo springdoc é a fonte de verdade do contrato. O incremento de 19/11 expõe quatro endpoints:

| Endpoint | O que faz | Acesso |
| --- | --- | --- |
| `POST /api/v1/auth/cadastro` | Cria o usuário com BCrypt e registra o consentimento LGPD | Público |
| `POST /api/v1/auth/login` | Autentica e cria a sessão | Público |
| `POST /api/v1/auth/logout` | Encerra a sessão | Logado |
| `GET /api/v1/usuarios/me` | Devolve os dados do usuário logado e prova que a sessão funciona | Logado |

O Swagger UI (`/swagger-ui/**` e `/v3/api-docs/**`) fica liberado na configuração de segurança, porque é a demonstração do incremento.

**Autenticação por sessão.** O login por e-mail e senha cria uma sessão no servidor, identificada por cookie, e o `server.servlet.session.timeout=30m` atende os 30 minutos de inatividade do RNF05 sem refresh token. Login com Google e JWT, caso o app nativo exija, estão fora do escopo atual e podem ser avaliados em uma entrega futura. Como os controllers sempre leem o usuário do `Authentication`, uma futura troca de autenticação não exige mudanças nos controllers.

**Isolamento dos dados.** O id do usuário vem sempre da sessão, nunca do caminho nem do corpo da requisição. Assim não existe endpoint em que um usuário consiga pedir os dados de outro, e o teste de autorização do RNF05 fica simples de escrever.

Dois pontos de atenção no incremento:

- No Spring Security 6, o login feito por um controller em JSON precisa salvar o contexto de segurança na sessão de forma explícita. Sem isso, o usuário "loga" e a requisição seguinte volta sem autenticação. É a parte mais traiçoeira, então deve começar assim que o cadastro básico existir, sem esperar o resto do cadastro.
- O CSRF fica desativado no incremento, porque sem front-end o Swagger UI não consegue enviar o token. Essa proteção precisa voltar quando o front-end existir.

Regras de negócio que entram nas próximas entregas:

- RN04: enum `Dificuldade` com os pontos em cada constante (FACIL 100, MEDIA 200, DIFICIL 400); dificuldade nula usa 100.
- RN05: o dia da conclusão decide o streak, com o `Clock` injetado e o fuso America/Sao\_Paulo.
- RN03 e RF11: o tempo de estudo seguido é guardado no servidor, porque o contador precisa valer entre dispositivos.
- RN02: o servidor registra início e fim do ciclo, e o quiz só é liberado ao fim dele (RF07).
- O registro de pontos e streak acontece em uma única transação com a conclusão do conteúdo, e o retry (até 2 tentativas em 5 segundos, pelo ERS) fica na chamada do cliente.

## Ambiente, testes e CI/CD

No incremento de 19/11 a aplicação roda direto na máquina de cada integrante, com banco H2 em memória e sem Docker. O MariaDB, o Docker Compose e o CI entram depois da entrega.

**Como rodar.** Com o Maven Wrapper, qualquer integrante executa `./mvnw spring-boot:run` (`mvnw.cmd` no Windows), sem instalar o Maven. A versão do Java é fixada no `pom.xml` (`<java.version>21</java.version>`), e o `.gitignore` exclui `.idea/` e `.vscode/`, para que o IntelliJ e o VS Code importem o projeto a partir do `pom.xml`.

**Testes do incremento:**

- Unitário do serviço de cadastro, cobrindo e-mail duplicado e a regra de senha.
- Integração com MockMvc no H2, cobrindo cadastro e login.
- Autorização: `GET /api/v1/usuarios/me` sem login devolve 401.

O H2 não reproduz todas as diferenças de SQL do MariaDB. Com uma tabela só o risco é pequeno, mas na troca de banco os testes de integração passam a usar Testcontainers com MariaDB.

**Adiado para depois de 19/11:**

- Dockerfile em duas etapas e docker-compose com MariaDB.
- Perfis `dev`, `test` e `prod`, com URL do banco, usuário, senha e segredos em variáveis de ambiente e um `.env.example` no Git.
- Pipeline de CI a cada pull request (build, testes e relatório de cobertura do JaCoCo), com a imagem Docker construída na branch principal.
- Testes de integração com Testcontainers e a meta de 60% de cobertura nas regras puras (RNF10).
- Endpoint de saúde do Actuator para o monitor de uptime (RNF06), testes de carga (RNF01 e RNF11) e varredura OWASP ZAP (RNF04), perto da Release 1.

## Gerência de configuração

- **Branches e revisão:** uma única branch estável, `main`, sem `develop`. O trabalho acontece em branches curtas nomeadas pelo requisito (`feature/RF07-quiz`, `fix/RF12-pontos-duplicados`) e volta por pull request com pelo menos uma aprovação de outro integrante. A proteção da `main` bloqueia push direto; a exigência de CI verde só vale quando o CI existir.
- **Commits:** padrão Conventional Commits, citando o módulo e o requisito, como `feat(avaliacao): registra resultado do quiz (RF07)`.
- **Rastreabilidade:** cada RF e RNF vira uma issue, com rótulos de módulo, prioridade MoSCoW e release. O pull request fecha a issue, e o teste leva o código CT correspondente, de modo que a matriz da seção 7 da ERS fica verificável: requisito, issue, PR, teste.
- **Versões e baselines:** a ERS v1.0, de 14/10/2026, é marcada no Git com a tag `ers-v1.0`. O software usa versionamento semântico, com 0.x durante o MVP e v1.0.0 na Release 1 (junho/2027), e um CHANGELOG com os RFs entregues.
- **Controle de mudanças:** mudança em requisito aprovado abre uma issue do tipo mudança, listando os impactos (RFs, regras de negócio, tabelas, casos de teste). Precisa da aprovação do responsável da equipe e, quando há impacto no escopo, da professora orientadora. A ERS é atualizada com nova linha no histórico de revisões e na matriz antes de implementar.
- **Definição de pronto:** o código foi revisado, os testes do CT correspondente passam, a migração (se houver) está versionada e a documentação do endpoint está atualizada.
- **Segredos:** nunca no repositório; só o `.env.example`, com valores fictícios.
- **Estrutura do repositório:** `README.md` (instalação e como rodar, exigido pelo RNF10), `CONTRIBUTING.md`, `pom.xml`, `src/` e `docs/` com as pastas `ers/`, `der/` e `decisoes/`.