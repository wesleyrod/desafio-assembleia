# Sistema de Gerenciamento de Sessões de Votação

Este projeto é uma API RESTful desenvolvida em Spring Boot para gerenciar sessões de votação em pautas de assembleias. Ele permite o cadastro de pautas e associados, abertura de sessões de votação com tempo determinado, registro de votos com validação externa de CPF e contabilização assíncrona dos resultados utilizando mensageria.

##  Tecnologias Utilizadas

* **Java 17+** (Linguagem principal)
* **Spring Boot 3+** (Framework base, Web, Data JPA, Validation)
* **PostgreSQL** (Banco de dados relacional)
* **Apache Kafka & Zookeeper** (Mensageria para notificação de resultados)
* **Docker & Docker Compose** (Containerização da infraestrutura)
* **JUnit 5 & Mockito** (Testes Unitários)
* **RestTemplate** (Integração com API externa Mockada)

##  Decisões Arquiteturais

Durante o desenvolvimento, decisões estratégicas foram tomadas para garantir escalabilidade, segurança e resiliência:

1. **Isolamento de Domínio (DTOs):** A camada de visualização (Controllers) é estritamente separada da persistência (Entities) via *Records*. Isso previne vazamento de dados sensíveis e vulnerabilidades como *Mass Assignment*.
2. **Mensageria Assíncrona (Kafka):** O fechamento da sessão e a contagem de votos não bloqueiam a thread principal. Um `Scheduler` verifica sessões expiradas de forma assíncrona e publica o resultado no tópico `voting-session-results.v1` do Kafka.
3. **Validação de Domínio no Voto:** Seguindo princípios de auditoria, a entidade `Vote` e a entidade `VotingSession` não possuem endpoints de `PUT` ou `DELETE`. Um voto ou uma sessão nunca podem ser alterados ou apagados após registrados.
4. **Integração Externa Resiliente:** A validação de CPF (`CpfValidationClient`) consome uma API externa tratando adequadamente retornos de erro (ex: 404 Not Found) e traduzindo para exceções de negócio controladas.
5. **Global Exception Handler:** Centralização do tratamento de erros (Status 400, 404, 409, 422) entregando respostas em formato JSON padronizado e amigável.

## Melhorias Futuras (Evolução da Arquitetura)

Pensando em um cenário de produção com alta volumetria e rigorosos padrões de segurança, as seguintes melhorias seriam os próximos passos ideais para o projeto:

* **Autenticação e Autorização:** Implementação de Spring Security com JWT (JSON Web Token) ou OAuth2 para garantir que apenas usuários autenticados possam criar pautas ou registrar votos.
* **Paginação de Resultados:** Alterar os endpoints de listagem (`GET`) para utilizar o padrão `Pageable` do Spring Data, evitando gargalos de memória ao retornar milhares de votos ou associados de uma só vez.
* **Cache Distribuído (Redis):** Implementar cache nas consultas de Pautas e, principalmente, no resultado da validação de CPFs externos, reduzindo a latência da API e o custo de *networking*.
* **Observabilidade (APM):** Adicionar Spring Boot Actuator, Micrometer (Prometheus) e integração com Grafana para monitoramento em tempo real da saúde da aplicação e das filas do Kafka.
* **Pipeline CI/CD:** Criação de *workflows* no GitHub Actions ou GitLab CI para rodar os testes unitários, análise de qualidade de código (SonarQube) e build da imagem Docker automaticamente a cada *commit* e/ou *Pull Requests*.

## Como Executar o Projeto

**Pré-requisitos:** Ter o [Docker](https://www.docker.com/) e o [Docker Compose](https://docs.docker.com/compose/) instalados.

1. Clone o repositório:
   ```bash
   git clone [https://github.com/seu-usuario/seu-repositorio.git](https://github.com/seu-usuario/seu-repositorio.git)
   cd seu-repositorio
   ```
2. Suba a infraestrutura (Banco de Dados, Kafka e Zookeeper) via Docker:
    ```bash
    docker-compose up -d
    ```
3. Execute a aplicação Spring Boot usando sua IDE favorita ou via Maven:
    ```bash
    ./mvnw spring-boot:run
    ```

## Como escutar o resultado no Kafka

Para verificar a notificação de resultado sendo publicada automaticamente após o encerramento de uma sessão, execute este comando no terminal:
```bash
docker exec -it kafka kafka-console-consumer --bootstrap-server localhost:9092 --topic voting-session-results.v1 --from-beginning
```

## Como rodar os testes unitários

Para executar a suíte de testes que valida as regras de negócio de votação (impedimento de voto duplo, sessão encerrada, etc):
```bash
./mvnw test
```

## Documentação dos Endpoints (API)

A API foi versionada em `/v1` e dividida por domínios.

### Associados (`/v1/associates`)
Gerencia o cadastro dos membros que podem votar.
* `POST /v1/associates`: Cadastra um novo associado (Valida CPF duplicado no banco).
* `GET /v1/associates`: Lista todos os associados.
* `GET /v1/associates/{id}`: Busca um associado específico pelo ID.
* `PUT /v1/associates/{id}`: Atualiza nome e/ou CPF de um associado.
* `DELETE /v1/associates/{id}`: Remove um associado (apenas se não houver votos relacionados).

### Pautas (`/v1/topics`)
Gerencia os assuntos que serão votados.
* `POST /v1/topics`: Cria uma nova pauta.
* `GET /v1/topics`: Lista todas as pautas.
* `GET /v1/topics/{id}`: Busca os detalhes de uma pauta pelo ID.
* `PUT /v1/topics/{id}`: Atualiza a descrição da pauta.
* `DELETE /v1/topics/{id}`: Remove uma pauta (apenas se não houver sessões relacionados).

### Sessões de Votação (`/v1/voting-sessions`)
Gerencia a abertura e encerramento das urnas. *(Não possui PUT/DELETE por regras de auditoria)*.
* `POST /v1/voting-sessions`: Abre uma sessão em uma pauta (tempo padrão: 1 min, ou configurável via JSON).
* `GET /v1/voting-sessions`: Lista o histórico de todas as sessões criadas.
* `GET /v1/voting-sessions/{id}`: Busca dados de uma sessão específica.
* `GET /v1/voting-sessions/topic/{topicId}`: Lista todas as sessões que uma determinada pauta já teve.
* `GET /v1/voting-sessions/{id}/result`: Calcula e retorna o resultado final consolidado da votação (Aprovada, Reprovada ou Empate).

### Votos (`/v1/votes`)
Gerencia os votos individuais. *(Não possui PUT/DELETE por regras de auditoria)*.
* `POST /v1/votes`: Registra um voto ("SIM" ou "NAO"). Checa duplicidade, valida se a sessão está aberta e consulta a API externa de CPF.
* `GET /v1/votes`: Lista todos os votos do sistema (Auditoria geral).
* `GET /v1/votes/{id}`: Busca o registro de um voto específico.
* `GET /v1/votes/session/{sessionId}`: Lista todos os votos computados em uma sessão específica (Auditoria de sessão).

## Carga Inicial de Dados (Seed Script)

Para facilitar e agilizar a avaliação deste desafio, preparei um script SQL com uma massa de dados estruturada. Isso evita que precise cadastrar pautas e associados manualmente um a um.

**Como utilizar:**
Sugiro conectar-se ao banco de dados PostgreSQL rodando no Docker utilizando um client de sua preferência (como **DBeaver**, **pgAdmin** ou **DataGrip**) e executar o script abaixo.

```sql
-- =========================================================================
-- SCRIPT DE POPULAÇÃO INICIAL (SEED) PARA TESTES
-- =========================================================================

-- 1. Inserir Pautas (Topics)
INSERT INTO topics (id, description) VALUES
('11111111-1111-1111-1111-111111111111', 'Aprovação do orçamento anual para 2026'),
('22222222-2222-2222-2222-222222222222', 'Mudança da sede oficial do sindicato');

-- 2. Inserir Associados (Associates)
-- O primeiro associado usa o CPF real cadastrado no MockAPI
INSERT INTO associates (id, cpf, name) VALUES
('33333333-3333-3333-3333-333333333333', '99340012097', 'João Silva (Apto - MockAPI)'),
('44444444-4444-4444-4444-444444444444', '11122233344', 'Maria Souza'),
('55555555-5555-5555-5555-555555555555', '55566677788', 'Carlos Pereira');

-- 3. Inserir Sessões de Votação (Voting Sessions)
-- Sessão 1: FECHADA (Relacionada à Pauta 1). Fechou há 10 minutos. Pronta para testar o cálculo de resultado.
INSERT INTO voting_sessions (id, topic_id, opening_date, closing_date, status) VALUES
('66666666-6666-6666-6666-666666666666', '11111111-1111-1111-1111-111111111111', CURRENT_TIMESTAMP - INTERVAL '20 minutes', CURRENT_TIMESTAMP - INTERVAL '10 minutes', 'CLOSED');

-- Sessão 2: ABERTA (relacionada à Pauta 2). Ficará aberta por mais 1 hora. Pronta para receber novos votos.
INSERT INTO voting_sessions (id, topic_id, opening_date, closing_date, status) VALUES
('77777777-7777-7777-7777-777777777777', '22222222-2222-2222-2222-222222222222', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '1 hour', 'OPEN');

-- 4. Inserir Votos (Votes) apenas na Sessão 1 (A que já está fechada)
-- Cenário: 2 votos SIM, 1 voto NAO. A pauta deve retornar como "APROVADA".
INSERT INTO votes (id, session_id, associate_id, vote_choice) VALUES
('88888888-8888-8888-8888-888888888888', '66666666-6666-6666-6666-666666666666', '33333333-3333-3333-3333-333333333333', 'SIM'),
('99999999-9999-9999-9999-999999999999', '66666666-6666-6666-6666-666666666666', '44444444-4444-4444-4444-444444444444', 'NAO'),
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '66666666-6666-6666-6666-666666666666', '55555555-5555-5555-5555-555555555555', 'SIM');
```


## Implementação dos Bônus Pendentes

Em um cenário de desenvolvimento ágil com tempo restrito (time-box do desafio), a prioridade técnica foi garantir a entrega de um **core de domínio**. A decisão arquitetural foi focar na integridade dos dados (ausência de *Update/Delete* em votos), na cobertura de testes das regras de negócio e na resiliência da mensageria (Kafka). 

Funcionalidades extras, se feitas às pressas, podem comprometer a estabilidade da aplicação principal. Sendo assim, detalho abaixo o planejamento arquitetural de como eu implementaria os dois requisitos bônus caso houvesse mais tempo hábil:

### 1. Bônus: Testes de Performance / Carga (JMeter)
**Por que não foi implementado agora:** Testes de carga reais exigem um ambiente espelhado de produção (banco de dados populado, instâncias dedicadas) para gerar métricas confiáveis. Rodar um teste de *stress* rodando o Kafka e o Postgres no Docker local (junto com a IDE) geraria gargalos de CPU da máquina host, sujando os resultados.

**Como eu implementaria:**
* **Ferramenta:** Apache JMeter
* **Cenário de Teste:** Simularia milhares de usuários simultâneos batendo no endpoint `POST /v1/votes` em uma janela de 1 minuto.
* **Foco da Análise:** Validar se o banco de dados suportaria a concorrência, além de verificar se a API do Mock de CPF não se tornaria o gargalo.

### 2. Bônus: Versionamento Semântico da API / Autenticação (Spring Security)
**Por que não foi implementado agora:** O versionamento estrutural já foi iniciado no nível da URI (`/v1/...`), mas uma implementação avançada de segurança (JWT/OAuth2) ou versionamento por *Headers* exigiria a criação de um servidor de autorização e o gerenciamento de *Roles* (Admin para criar pautas, User para votar), o que fugiria do escopo mínimo viável (MVP) proposto pelo desafio.

**Como eu implementaria:**

* **Segurança (JWT):** Adicionaria o `spring-boot-starter-security` e configuraria um filtro de interceptação (`OncePerRequestFilter`). Usuários comuns receberiam um token JWT ao logar e o sistema extrairia o CPF diretamente do *Payload* do Token (via `SecurityContextHolder`), impedindo que um usuário enviasse o CPF de outro no corpo do JSON da requisição de voto.

 **🔴 OBSERVAÇÃO IMPORTANTE SOBRE A API EXTERNA DE CPF:**
 
 O endpoint original de validação de CPF fornecido no PDF do desafio encontra-se atualmente **inativo/offline**. 
 Para não bloquear o teste e garantir que a aplicação possa ser avaliada de ponta a ponta com todas as regras de negócio funcionando, **criei um novo MockAPI** com o contrato exato exigido pelo desafio (retornando `ABLE_TO_VOTE` ou `UNABLE_TO_VOTE`).
 
 * **Nova URL utilizada na integração:** `https://69a101ba2e82ee536f9ff7cd.mockapi.io/teste/associates/`
