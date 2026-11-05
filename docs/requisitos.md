# Requisitos do Sistema — Pierre

> Extraído da **Especificação de Requisitos de Software (ERS)** — *Pierre – Plataforma gamificada de estudo*, versão 1.0, datada de 14/10/2026.

## 1. Visão geral

O Pierre é uma plataforma gamificada de revisão de conteúdos para estudantes ingressantes no Ensino Superior. A versão descrita contempla revisões curtas de **Matemática, Física e Química**, com conteúdos em texto e vídeo, quizzes e flashcards.

### 1.1 Público-alvo

Estudantes ingressantes, definidos no documento como estudantes do 1º ou 2º semestre do Ensino Superior.

### 1.2 Fora do escopo

- Conteúdos de áreas além de Matemática, Física e Química.
- Ações sobre fatores não acadêmicos de evasão (financeiros, sociais ou de adaptação).
- Ensino de conteúdos novos das disciplinas da graduação.

## 2. Requisitos funcionais

Prioridades conforme a ERS: **Must** (essencial), **Should** (importante) e **Could** (desejável, se houver tempo).

| ID | Requisito | Prioridade |
|---|---|---|
| RF01 | Cadastro de usuários | Must |
| RF02 | Login | Must |
| RF03 | Diagnóstico inicial por área | Should |
| RF04 | Recomendação de trilhas | Should |
| RF05 | Consulta de trilhas | Should |
| RF06 | Acesso ao conteúdo | Must |
| RF07 | Quizzes | Must |
| RF08 | Feedback de desempenho | Must |
| RF09 | Visualização de progresso | Must |
| RF10 | Flashcards | Could |
| RF11 | Sugestão de pausa | Should |
| RF12 | Sistema de pontos | Must* |
| RF13 | Sistema de streaks | Should |

### RF01 — Cadastro de usuários

**Descrição:** O sistema deve permitir que o usuário se cadastre informando nome, e-mail, senha e escolaridade.

- **Ator:** Usuário.
- **Prioridade:** Must.
- **Pré-condição:** O usuário não possui conta cadastrada.
- **Fluxo principal:**
  1. O usuário seleciona a opção de cadastro.
  2. Informa nome, e-mail, senha e escolaridade.
  3. O sistema valida os dados.
  4. O sistema registra o usuário.
  5. O sistema envia confirmação ao e-mail informado.
- **Exceções:**
  - Campo obrigatório ausente ou inválido: indicar o campo e solicitar correção, sem registrar.
  - E-mail já cadastrado: informar o erro e não registrar.
  - Falha de conexão ou gravação: informar que o cadastro não foi concluído e manter os dados preenchidos para nova tentativa.
  - Falha no envio da confirmação: manter o cadastro e informar o usuário.
- **Critérios de aceitação:**
  - Com dados válidos, registrar o usuário e enviar confirmação em até 5 minutos.
  - Se o e-mail já estiver cadastrado, recusar o registro e exibir erro.
- **Relacionados:** RF02 e RF03.

### RF02 — Login

**Descrição:** O sistema deve permitir que o usuário cadastrado se autentique na plataforma.

- **Ator:** Usuário.
- **Prioridade:** Must.
- **Pré-condição:** O usuário possui conta cadastrada.
- **Fluxo principal:**
  1. O usuário seleciona login.
  2. Informa e-mail e senha.
  3. O sistema verifica a identidade.
  4. O sistema concede acesso e exibe o perfil.
- **Alternativo:** O usuário pode selecionar “Entrar com Google”; o sistema recebe a confirmação de identidade do Google e concede acesso.
- **Exceções:** Usuário não encontrado ou senha incorreta; falha de comunicação com Google; ou falha de conexão com o servidor. Em todos os casos, alertar e não conceder acesso.
- **Critérios de aceitação:**
  - Credenciais corretas permitem acesso ao perfil.
  - Senha incorreta gera alerta e mantém o acesso bloqueado.
  - Falha na comunicação com Google gera alerta e não concede acesso.
- **Relacionado:** RF01.

### RF03 — Diagnóstico inicial por área

**Descrição:** O sistema deve exigir que o usuário novo responda a um questionário de diagnóstico por área no primeiro acesso.

- **Ator:** Usuário.
- **Prioridade:** Should.
- **Pré-condição:** Usuário autenticado, acessando a plataforma pela primeira vez.
- **Fluxo principal:** direcionar ao questionário; receber respostas de cada área; registrar respostas; marcar questionário como concluído.
- **Alternativo:** Se o usuário sair antes de concluir, salvar o estado e retomar do ponto em que parou no próximo acesso.
- **Exceções:** Se tentar acessar outra função antes de concluir, redirecionar ao questionário. Se houver falha ao salvar, informar o erro, manter as respostas na tela e permitir nova tentativa. Se houver falha de conexão, preservar as respostas já registradas.
- **Critérios de aceitação:**
  - No primeiro acesso, direcionar o usuário novo ao questionário e bloquear as demais funções até a conclusão.
  - Ao finalizar todas as perguntas, registrar as respostas e marcar o questionário como concluído.
- **Relacionados:** RF01 e RF04.

### RF04 — Recomendação de trilhas

**Descrição:** Recomendar trilhas com base nas áreas em que o percentual de acerto no diagnóstico seja inferior a **0,6**.

- **Ator:** Usuário.
- **Prioridade:** Should.
- **Pré-condição:** Questionário RF03 concluído.
- **Fluxo principal:** calcular percentual de acerto por área; identificar lacunas (percentual inferior a 0,6); selecionar trilhas correspondentes; exibir recomendações.
- **Alternativo:** Se nenhuma área apresentar lacuna, recomendar uma trilha comum.
- **Exceção:** Se houver falha no processamento, informar o erro, preservar respostas e permitir nova tentativa.
- **Critérios de aceitação:**
  - Percentual 0,5 em uma área deve gerar ao menos uma recomendação daquela área.
  - Percentual igual ou superior a 0,6 em todas as áreas deve gerar recomendação da trilha comum.
- **Relacionado:** RF03.

### RF05 — Consulta de trilhas

**Descrição:** Permitir que o usuário consulte as trilhas disponíveis na plataforma.

- **Ator:** Usuário.
- **Prioridade:** Should.
- **Pré-condições:** Usuário autenticado e questionário RF03 concluído.
- **Fluxo principal:** acessar a seção de trilhas; listar trilhas disponíveis; usuário seleciona uma; sistema exibe os conteúdos da trilha.
- **Exceções:** Se não houver trilhas cadastradas, informar; em caso de falha de conexão, informar o erro e permitir nova tentativa.
- **Critérios de aceitação:**
  - A seção deve listar todas as trilhas disponíveis.
  - Ao selecionar uma trilha, exibir seus conteúdos.
- **Relacionados:** RF04 e RF06.

### RF06 — Acesso ao conteúdo

**Descrição:** Permitir que o usuário acesse o conteúdo de uma trilha.

- **Ator:** Usuário.
- **Prioridade:** Must.
- **Pré-condições:** Usuário autenticado e trilha selecionada com conteúdo disponível.
- **Fluxo principal:** selecionar conteúdo; escolher iniciar ciclo de estudo; iniciar contagem do tempo; exibir texto e vídeos; ao término, registrar o tempo e marcar conteúdo como concluído.
- **Alternativo:** Se o usuário sair sem concluir, registrar a contagem do tempo.
- **Exceções:**
  - Vídeo não encontrado: exibir mensagem alternativa no lugar do vídeo e manter o texto.
  - Texto não encontrado: exibir mensagem alternativa no lugar do texto e manter o vídeo.
  - Texto e vídeo não encontrados: informar indisponibilidade e não iniciar o ciclo.
  - Falha de conexão durante o ciclo: informar e interromper o ciclo.
- **Critérios de aceitação:** Ao iniciar um ciclo, exibir texto e vídeos. Se o vídeo não for encontrado, exibir mensagem alternativa e manter o texto disponível.
- **Regras:** RN01 e RN02.
- **Relacionados:** RF05, RF07 e RF11.

### RF07 — Quizzes

**Descrição:** Permitir que o usuário responda a um quiz sobre o conteúdo estudado em um ciclo de estudo.

- **Ator:** Usuário.
- **Prioridade:** Must.
- **Pré-condições:** Usuário autenticado e ciclo de estudo de um conteúdo concluído.
- **Fluxo principal:** liberar quiz ao término do ciclo; receber respostas; calcular número de acertos; registrar resultado associado ao usuário, conteúdo e ciclo.
- **Exceções:**
  - Se o usuário abandonar o quiz ou a conexão cair, descartar respostas parciais e reiniciar o quiz no próximo acesso, sem registrar resultado.
  - Se falhar o registro do resultado, informar o usuário e não registrar o resultado.
- **Critérios de aceitação:**
  - Ao terminar o ciclo, liberar o quiz daquele conteúdo.
  - Ao finalizar todas as perguntas, registrar o resultado.
  - Ao abandonar ou perder conexão, reiniciar o quiz sem registrar resultado parcial.
- **Regras:** RN01 e RN02.
- **Relacionados:** RF06, RF08 e RF09.

### RF08 — Feedback de desempenho

**Descrição:** Exibir feedback de desempenho calculado a partir dos quizzes respondidos.

- **Ator:** Usuário.
- **Prioridade:** Must.
- **Pré-condição:** Usuário autenticado e com ao menos um quiz respondido.
- **Fluxo principal:** usuário solicita análise; sistema analisa respostas; exibe conteúdos com menor nota e recomenda ações.
- **Exceção:** Se a análise não for possível por falha do serviço ou de conexão, exibir alerta e não exibir feedback.
- **Critérios de aceitação:**
  - Ao solicitar análise com pelo menos um quiz respondido, exibir percentual de acerto por área.
  - Em caso de falha na análise, exibir alerta.
- **Relacionados:** RF07 e RF09.

### RF09 — Visualização de progresso

**Descrição:** Permitir que o usuário visualize os conteúdos que já estudou.

- **Ator:** Usuário.
- **Prioridade:** Must.
- **Pré-condição:** Usuário autenticado.
- **Fluxo principal:** usuário escolhe ver progresso; sistema exibe lista de conteúdos estudados.
- **Exceções:** Se não houver conteúdo estudado, exibir mensagem informativa. Se houver falha de conexão, informar e permitir nova tentativa.
- **Critérios de aceitação:** Exibir a lista de conteúdos estudados; para usuário sem conteúdos estudados, exibir mensagem informativa.
- **Relacionados:** RF06, RF07 e RF08.

### RF10 — Flashcards

**Descrição:** Permitir que o usuário crie flashcards de estudo.

- **Ator:** Usuário.
- **Prioridade:** Could.
- **Pré-condição:** Usuário autenticado.
- **Fluxo principal:** escolher criar flashcard; informar frente e verso; sistema valida preenchimento; registrar na conta.
- **Exceções:** Se frente ou verso estiver em branco, indicar o campo e solicitar preenchimento sem registrar. Se houver falha de conexão, alertar e não salvar.
- **Critérios de aceitação:** Com frente e verso preenchidos, registrar o flashcard. Em caso de falha de conexão, alertar e não registrar.
- **Relacionado:** RF02.

### RF11 — Sugestão de pausa

**Descrição:** Sugerir pausa após 20 minutos de estudo seguido.

- **Ator:** Usuário.
- **Prioridade:** Should.
- **Pré-condições:** Usuário autenticado e com 20 minutos de estudo seguido.
- **Fluxo principal:** exibir aviso de pausa de 10 minutos; se o usuário aceitar e encerrar a sessão, reiniciar o contador.
- **Alternativo:** Se ignorar o aviso e continuar, manter conteúdo em exibição e repetir a sugestão após mais 10 minutos de estudo, com 5 minutos adicionais na pausa sugerida.
- **Exceções:**
  - Se fechar o app antes do aviso, reiniciar o contador.
  - Durante um quiz, pausar o contador e retomá-lo ao fim.
  - Se estudar em mais de um dispositivo, manter o contador entre dispositivos.
- **Critérios de aceitação:**
  - Ao atingir 20 minutos, exibir sugestão de pausa de 10 minutos em até 5 segundos.
  - Após ignorar o aviso, ao completar mais 10 minutos, exibir nova sugestão de pausa de 15 minutos.
  - Se fechar o app com 15 minutos acumulados, ao reabri-lo o contador deve estar em zero.
- **Regras:** RN01 e RN03.
- **Relacionados:** RF06 e RF07.

### RF12 — Sistema de pontos

**Descrição:** Registrar pontos a cada conteúdo concluído, conforme a dificuldade.

- **Ator:** Usuário.
- **Prioridade:** Should na ficha detalhada; Must na tabela-resumo, na priorização e no MVP.
- **Pré-condições:** Usuário autenticado e conteúdo concluído.
- **Fluxo principal:** identificar dificuldade; registrar pontuação correspondente; exibir pontos ganhos.
- **Exceções:**
  - Dificuldade indefinida: registrar 100 pontos.
  - Conclusão simultânea do mesmo conteúdo em mais de um dispositivo: registrar apenas a primeira recebida.
  - Conteúdo já concluído: não registrar pontos novamente.
  - Erro de conexão ao registrar: realizar até 2 tentativas em 5 segundos; se ambas falharem, avisar o usuário.
  - Falha ao exibir os pontos: alertar, mantendo o registro.
- **Critérios de aceitação:** Fácil = 100 pontos; Média = 200; Difícil = 400; dificuldade não definida = 100. Em conclusão simultânea em dois dispositivos, registrar somente a primeira.
- **Regra:** RN04.
- **Relacionados:** RF06 e RF07.

### RF13 — Sistema de streaks

**Descrição:** Registrar a quantidade de dias consecutivos em que o usuário concluiu ao menos um conteúdo.

- **Ator:** Usuário.
- **Prioridade:** Should.
- **Pré-condições:** Usuário autenticado e conteúdo concluído.
- **Fluxo principal:** verificar se o dia atual já foi marcado; verificar se o dia anterior foi marcado; marcar o dia atual e incrementar a sequência em 1; exibir a sequência.
- **Alternativo:** Se o dia atual já estiver marcado, não incrementar.
- **Exceções:**
  - Se o dia anterior não estiver marcado, zerar a sequência e iniciar em 1 com a conclusão atual.
  - Conteúdo iniciado em um dia e concluído em outro conta apenas no dia da conclusão.
  - Falha de conexão ao registrar: realizar até 2 tentativas em 5 segundos; se ambas falharem, avisar.
- **Critérios de aceitação:** Incrementar em 1 quando o dia anterior estiver marcado; não incrementar mais de uma vez no mesmo dia; iniciar em 1 após quebra; considerar somente o dia da conclusão.
- **Regra:** RN05.
- **Relacionados:** RF06, RF07 e RF09.

## 3. Regras de negócio

| ID | Regra |
|---|---|
| RN01 | Atividades de revisão devem ser curtas e distribuídas regularmente ao longo dos dias, evitando períodos longos e concentrados. |
| RN02 | Um ciclo de estudo é composto por conteúdo de revisão e quiz, com duração prevista de até 20 minutos. |
| RN03 | Uma pausa dura 10 minutos, com 5 minutos adicionais a cada aviso ignorado. |
| RN04 | Cada conteúdo concluído gera pontos conforme a dificuldade: Fácil = 100; Média = 200; Difícil = 400; dificuldade não definida = 100. |
| RN05 | Um dia conta na sequência quando ao menos um conteúdo é concluído. |

### Definições relevantes

- **Ciclo de estudo:** período de 20 minutos de estudo.
- **Conteúdo concluído:** conteúdo finalizado pelo usuário; vídeo assistido e quiz realizado.
- **Estudo seguido:** tempo contínuo de contato com um conteúdo, até atingir 20 minutos.
- **Streak:** quantidade de dias consecutivos com ao menos um conteúdo concluído.
- **Trilha comum:** ponto inicial de uma área que encadeia o estudo dela.
- **Trilhas:** conteúdos (vídeo-aulas, textos e quizzes) organizados por dificuldade e progressão do tema.

## 4. Requisitos não funcionais

| ID | Categoria | Requisito / métrica | Prioridade |
|---|---|---|---|
| RNF01 | Desempenho | Carregar página de conteúdo ou quiz em até 3 s; percentil 95 ≤ 3 s, com 50 usuários simultâneos, conexão de 10 Mbps e base de 500 usuários. | Must |
| RNF02 | Desempenho | Exibir feedback de cada resposta do quiz em até 1 s; percentil 95 ≤ 1 s, com 50 usuários simultâneos e conexão de 10 Mbps. | Should |
| RNF03 | Usabilidade | Iniciar ciclo de estudo em até 3 interações a partir da tela inicial, sem treinamento. Pelo menos 80% dos participantes devem concluir tarefas-chave sem ajuda e SUS ≥ 68. | Must |
| RNF04 | Segurança | Senhas armazenadas somente como hash com salt (bcrypt ou Argon2); 100% do tráfego por HTTPS com TLS 1.2 ou superior. | Must |
| RNF05 | Segurança | Cada usuário acessa somente seus próprios dados de progresso, pontos e streaks. Zero acessos indevidos nos testes; sessão expira após 30 minutos de inatividade. | Must |
| RNF06 | Confiabilidade | Disponibilidade mensal ≥ 99%; backup diário (perda máxima de 24 h); zero respostas perdidas após confirmação ao usuário. | Should |
| RNF07 | Compatibilidade | Funcionar nas duas versões mais recentes de Chrome, Firefox, Edge e Safari; layout adaptável a 360–1920 px, sem rolagem horizontal. | Must |
| RNF08 | Acessibilidade | WCAG 2.1 nível AA nas telas de conteúdo, quiz e flashcard. Contraste mínimo 4,5:1; navegação por teclado; texto alternativo para imagens; legendas em vídeos; gamificação não dependente apenas de cor; Lighthouse de acessibilidade ≥ 90. | Should |
| RNF09 | Conformidade legal | Coletar apenas dados pessoais necessários; registrar consentimento no cadastro; permitir exclusão da conta e dos dados em até 15 dias após solicitação. | Must |
| RNF10 | Manutenibilidade | Manter trilhas, quizzes e flashcards separados do código-fonte; testes automatizados para pontos e streaks; cobertura ≥ 60% nesses módulos; Git e README de instalação. | Should |
| RNF11 | Escalabilidade | Suportar 50 usuários simultâneos; com 100 usuários simultâneos, crescimento do tempo de resposta no percentil 95 de no máximo 20% em relação à carga de 50, com base de 500 usuários. | Could |

### Verificação prevista para os RNFs

- **RNF01:** teste de carga automatizado (k6 ou JMeter) e auditoria Lighthouse a cada release.
- **RNF02:** teste de carga automatizado no fluxo de resposta do quiz.
- **RNF03:** teste moderado com pelo menos 5 estudantes ingressantes, sem instruções prévias, e questionário SUS.
- **RNF04:** inspeção de código e banco; varredura OWASP ZAP sem vulnerabilidades críticas ou altas abertas.
- **RNF05:** testes automatizados de autorização com pelo menos 2 contas e revisão de código.
- **RNF06:** monitoramento de disponibilidade e teste semestral de restauração de backup. Manutenção programada comunicada com 24 h de antecedência é excluída da medição.
- **RNF07:** roteiro manual por navegador e emulação de dispositivos.
- **RNF08:** auditoria automática (axe ou Lighthouse) e revisão manual com leitor de tela.
- **RNF09:** inspeção do modelo de dados e testes de cadastro e exclusão.
- **RNF10:** relatório de cobertura na integração contínua e inclusão de trilha de teste sem alteração de código.
- **RNF11:** teste de carga comparativo (k6 ou JMeter).

## 5. Restrições, dependências e ambiente

### 5.1 Restrições de projeto e implementação

- **RE01:** vídeos devem ser reproduzidos pelo player oficial do YouTube, sem armazenar os arquivos de vídeo.
- **RE02:** autenticação Google deve usar OAuth 2.0 / OpenID Connect; o sistema não deve armazenar a senha do usuário.
- **RE03:** tratamento de dados pessoais deve seguir a Lei nº 13.709/2018 (LGPD).

### 5.2 Suposições e dependências

- **SD01:** assume-se que as videoaulas permanecerão publicadas e acessíveis no YouTube. Se não, RF06 deixa de exibir vídeos.
- **SD02:** assume-se que as videoaulas são de canal da equipe ou que o autor permite incorporação. Caso contrário, há risco de remoção por direitos autorais e pode ser necessário gravar conteúdo próprio.
- **SD03:** assume-se que o conteúdo estará cadastrado antes do uso. Sem isso, RF05 a RF10 ficam sem dados para exibir.

### 5.3 Ambiente operacional

- Aplicação web acessada por navegador e aplicativo nativo.
- Dispositivos: computadores, notebooks, tablets e smartphones.
- Versão web independente do sistema operacional; versão nativa para Android e iOS.
- Navegadores: versões atuais de Google Chrome, Microsoft Edge, Mozilla Firefox e Safari.
- Conexão com a Internet obrigatória; velocidade mínima não definida nesta versão.
- Integrações: Google para autenticação e YouTube para reprodução de vídeo.
- Hospedagem em serviço de nuvem, com execução em contêineres.
- Não há hardware adicional ou periféricos necessários.

## 6. Requisitos de interface e integrações

### 6.1 Diretrizes de interface

- Após o login, a tela inicial reúne plano do dia, acesso às trilhas e diagnóstico.
- O cabeçalho de todas as telas autenticadas exibe identificação do sistema, pontuação acumulada e perfil do usuário.
- Conteúdos são acessados pelas trilhas; cada trilha oferece a ação de continuar de onde o usuário parou.
- Usuário novo é direcionado ao diagnóstico antes de acessar as demais funções.
- Progresso por trilha é exibido em barra com percentual.
- Streak e pontuação ficam visíveis na tela inicial.
- Elementos não devem depender apenas de cor para transmitir informação.
- Erros de preenchimento devem indicar o campo e preservar os dados digitados.
- Falhas de conexão ou de serviço externo devem gerar alerta e permitir nova tentativa.
- Ausência de trilhas ou conteúdos estudados deve gerar mensagem informativa no lugar da lista.
- Conteúdo parcialmente indisponível deve exibir mensagem alternativa e manter disponível a parte existente.
- Layout adaptável de 360 a 1920 px, sem rolagem horizontal.
- Telas de conteúdo, quiz e flashcard devem seguir WCAG 2.1 nível AA.
- O ciclo de estudo deve ser iniciado em até 3 interações a partir da tela inicial.

### 6.2 Integrações externas

| Sistema | Dados / finalidade | Protocolo e formato | Frequência |
|---|---|---|---|
| Google | Solicitação de autenticação; identificador da conta, nome e e-mail. | OAuth 2.0 / OpenID Connect sobre HTTPS; token JWT. | A cada cadastro ou login com Google. |
| YouTube | Identificador do vídeo, transmissão do vídeo e eventos do player. | HTTPS; player incorporado via YouTube IFrame Player API. | A cada acesso a conteúdo em vídeo. |

## 7. Priorização e entregas

### 7.1 Distribuição por prioridade

- **Must:** RF01, RF02, RF06, RF07, RF08, RF09, RF12, RNF01, RNF03, RNF04, RNF05, RNF07 e RNF09.
- **Should:** RF03, RF04, RF05, RF11, RF13, RNF02, RNF06, RNF08 e RNF10.
- **Could:** RF10 e RNF11.
- **Won’t:** nenhum requisito listado (N/A).

### 7.2 Planejamento de releases

| Entrega | Conteúdo | Data prevista |
|---|---|---|
| Release 1 (MVP) | RF01, RF02, RF06, RF07, RF08, RF09, RF12, RNF01, RNF03, RNF04, RNF05, RNF07 e RNF09. | Junho/2027 |
| Release 2 | RF03, RF04, RF05, RF10, RF11, RF13, RNF02, RNF06, RNF08, RNF10 e RNF11. | Novembro/2027 |
| Release 3 | N/A | N/A |

## 8. Critérios gerais de aceitação do sistema

- [ ] Todos os requisitos classificados como Must estão implementados e testados.
- [ ] Nenhum defeito de severidade alta permanece em aberto.
- [ ] Documentação de instalação e uso foi entregue.

## 9. Rastreabilidade dos requisitos

A ERS associa os requisitos funcionais RF01–RF13, respectivamente, aos casos de teste CT01–CT13; e os requisitos não funcionais RNF01–RNF11 aos casos CT14–CT24.

| Requisito | Caso de teste |
|---|---|
| RF01 | CT01 |
| RF02 | CT02 |
| RF03 | CT03 |
| RF04 | CT04 |
| RF05 | CT05 |
| RF06 | CT06 |
| RF07 | CT07 |
| RF08 | CT08 |
| RF09 | CT09 |
| RF10 | CT10 |
| RF11 | CT11 |
| RF12 | CT12 |
| RF13 | CT13 |
| RNF01 | CT14 |
| RNF02 | CT15 |
| RNF03 | CT16 |
| RNF04 | CT17 |
| RNF05 | CT18 |
| RNF06 | CT19 |
| RNF07 | CT20 |
| RNF08 | CT21 |
| RNF09 | CT22 |
| RNF10 | CT23 |
| RNF11 | CT24 |

---

**Fonte:** ERS — *Pierre – Plataforma gamificada de estudo*, versão 1.0, 14/10/2026.
