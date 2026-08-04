# API de Gerenciamento de Reservas

API RESTful desenvolvida em Spring Boot para o gerenciamento de reservas de salas. O sistema permite a criação de agendamentos, validação de conflitos de horários e consulta de estatísticas diárias.

A arquitetura do projeto adota a divisão por domínios (Package by Feature) e implementa controles de concorrência e performance para o armazenamento em memória.

---

## Tecnologias

* **Java 21**
* **Spring Boot 3** (Web, WebMVC)
* **Docker e Docker Compose**
* **Gradle**
* **Swagger / OpenAPI 3**
* **JUnit 5 e Mockito**

---

## Documentação da API

A documentação interativa dos endpoints é gerada via Swagger UI. Com a aplicação em execução, acesse através do navegador:

**[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)**

---

## Execução com Docker

A execução via containers é a opção recomendada.

**1. Clone o repositório**
```bash
git clone <url-do-repositorio>
cd reservas
```
**2. Inicie o container**

```Bash
docker-compose up --build
A API será inicializada e estará disponível na porta 8080.
```
---

## Execução Local (Gradle)
Para executar diretamente no ambiente local sem Docker:

**1. Inicie a aplicação** 

```Bash
./gradlew clean bootRun
```

**2. Execute os testes unitários**

```Bash
./gradlew test
```
---
## Arquitetura de Pacotes
A estrutura do projeto foi dividida seguindo o padrão Package by Feature:

* **/reservas**: Lógica de negócio, validação e persistência dos agendamentos.

* **/estatisticas**: Processamento das métricas diárias, acessando os dados via Shared Database Pattern.

* **/health**: Endpoint para verificação de disponibilidade do serviço.

* **/core**: Tratamento global de exceções e configurações da aplicação.
---
## Análise de Performance e Concorrência
Como o armazenamento de dados é feito em memória, as seguintes práticas foram adotadas para garantir a estabilidade do sistema sob requisições simultâneas:

**1. Concorrência (Thread-Safety)** 

Coleções Seguras: O repositório utiliza estruturas nativas para concorrência (ConcurrentHashMap e CopyOnWriteArrayList). Isso evita falhas como ConcurrentModificationException quando o serviço de estatísticas realiza leituras no exato momento em que novas reservas são cadastradas.

Atomicidade: A criação de agendamentos no ReservaService utiliza sincronização de threads (synchronized) em sua região crítica. Isso previne condições de corrida (Race Conditions) que poderiam resultar em sobreposição de horários e reservas duplicadas.

**2. Performance Algorítmica**

Busca O(1): O armazenamento foi estruturado em um mapa indexado pela data da reserva. O acesso direto pela chave da data reduz a complexidade da busca de O(N) para O(1), eliminando varreduras completas no histórico para calcular as estatísticas do dia.

Processamento: O cálculo analítico das durações e ocupações é feito utilizando a API de Streams do Java, evitando a alocação de variáveis temporárias na memória Heap.

---
## Endpoints
**Reservas**

* **POST /reservas**: Registra uma nova reserva mediante validação de disponibilidade.

* **GET /reservas**: Lista todas as reservas agendadas no sistema.

* **DELETE /reservas**: Remove todos os registros em memória.

**Estatísticas**

* **GET /estatisticas**: Retorna os indicadores processados com base nas reservas referentes à data atual.

**Monitoramento**
* **GET /health**: Retorna HTTP 200 (UP) para validação de integridade e disponibilidade.