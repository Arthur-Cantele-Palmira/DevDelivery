# 🛵 DevDelivery — Plataforma de Microserviços de Delivery

O DevDelivery é uma solução de backend baseada em arquitetura de microserviços, desenvolvida com Java 21 e Spring Boot. O objetivo da aplicação é gerenciar de forma escalável e segura estabelecimentos, cardápios, pedidos e autenticação de usuários em uma plataforma de delivery.

## 🏛️ Arquitetura do Projeto (Monorepo)

O repositório está estruturado no formato **Monorepo**, contendo os microserviços isolados em suas respectivas pastas:

```text
Delivery/
├── account-service/        # Serviço de gestão de contas e autenticação de usuários
├── restaurant-service/     # Serviço de gestão de restaurantes, cardápios e produtos
├── docker-compose.yml      # Orquestração de bancos de dados (PostgreSQL/Redis) e serviços
└── README.md
```

## 🚀 Tecnologias Utilizadas

- **Java 21 e Spring Boot 3**
- **Spring Security e JWT (JSON Web Tokens)**
- **Spring Data JPA e Hibernate**
- **PostgreSQL 16** — Banco de dados relacional principal
- **Flyway** — Gerenciamento de migrações de banco de dados
- **Redis 7** — Cache para otimização de consultas
- **H2 Database** — Banco de dados em memória para testes
- **JUnit 5 e Mockito** — Testes unitários e de repositório
- **Docker e Docker Compose** — Containerização e orquestração do ambiente
- **Maven** — Gerenciamento de dependências

## 📦 Microserviços Implementados

### 🍽️ restaurant-service

Responsável pelo domínio dos estabelecimentos e pelo catálogo de produtos e itens de cardápio.

#### Restaurantes (`Restaurant`)

Recursos e regras de negócio implementados:

- Cadastro de restaurantes.
- Atualização de informações.
- Listagem paginada.
- Consulta por ID.
- Vínculo obrigatório com o proprietário (`ownerId`).
- Histórico de auditoria automatizado (`created_at` e `updated_at`) por meio das anotações JPA `@PrePersist` e `@PreUpdate`.

#### Produtos (`Product`)

Recursos e regras de negócio implementados:

- Cadastro de produtos.
- Alteração de informações.
- Remoção de produtos.
- Busca de produtos associados a um restaurante específico.
- Validação das regras de negócio, garantindo que o produto pertença ao restaurante manipulado.

#### Tratamento Global de Exceções

- **`ResourceNotFoundException`**: tratamento de recursos não encontrados, retornando HTTP `404`.
- **`BusinessRuleException`**: tratamento de violações de regras de negócio, retornando HTTP `400` ou `422`, conforme o cenário.

#### Testes Unitários e Integrados

- Testes de serviços e repositórios utilizando JUnit 5 e Mockito.
- Cobertura dos cenários de sucesso e falha das camadas de serviço e repositório.
- Suíte de testes estabilizada para validar as regras de negócio e o acesso aos dados.

## ⚙️ Como Executar o Projeto Localmente

### Pré-requisitos

Antes de iniciar, certifique-se de ter instalado:

- Java 21 ou superior.
- Docker e Docker Compose.
- Git.

### 1. Configuração das Variáveis de Ambiente

Crie um arquivo `.env` na raiz do projeto (`Delivery/.env`) para definir as credenciais do ambiente de desenvolvimento.

> **Nota de segurança:** o arquivo `.env` contém segredos locais e deve estar listado no `.gitignore` para evitar o envio de credenciais ao repositório.

Exemplo de arquivo `.env`:

```dotenv
# Banco de Dados PostgreSQL Local
DB_URL=jdbc:postgresql://localhost:5432/restaurant_db
DB_USERNAME=postgres
DB_PASSWORD=sua_senha_local

# Chave Secreta JWT
JWT_SECRET=sua_chave_jwt_secreta_para_desenvolvimento

# Porta da aplicação
PORT=8081
```

> **Importante:** substitua os valores de exemplo pelas credenciais configuradas no seu ambiente local.

### 2. Subindo a Infraestrutura com Docker Compose

Na raiz do projeto, execute o comando abaixo para iniciar o PostgreSQL e o Redis utilizados pelo `restaurant-service`:

```bash
docker compose up -d postgres-restaurant redis-restaurant
```

### 3. Executando o Microserviço

Acesse a pasta do microserviço:

```bash
cd restaurant-service
```

Execute a aplicação utilizando o Maven Wrapper:

```bash
./mvnw spring-boot:run
```

Após a inicialização, a aplicação estará disponível em:

[http://localhost:8081](http://localhost:8081)

## 🧪 Executando os Testes

Para executar a suíte de testes do `restaurant-service`, incluindo os testes unitários e de repositório, utilize os comandos abaixo a partir da raiz do projeto:

```bash
cd restaurant-service
./mvnw clean test
```

O Maven executará os testes e apresentará o resultado da execução no terminal.

## 🛡️ Segurança e Boas Práticas

O projeto adota práticas para proteger informações sensíveis e manter a organização do repositório.

- **Parametrização de segredos:** credenciais do banco de dados e chaves JWT são configuradas por meio de variáveis de ambiente, evitando valores sensíveis fixos nos arquivos de configuração.
- **Isolamento via `.gitignore`:** arquivos sensíveis, como `.env`, são ignorados pelo Git para evitar o envio acidental de credenciais ao repositório público.
- **Exclusão de artefatos de compilação:** diretórios como `target/` são ignorados para manter o repositório limpo.
- **Exclusão de arquivos de IDEs:** diretórios e arquivos temporários, como `.idea/` e `.vscode/`, são ignorados quando aplicável.
- **Testes automatizados:** utilização de JUnit 5 e Mockito para validar o comportamento dos serviços e repositórios.

## 📌 Objetivo do Projeto

O DevDelivery tem como objetivo aplicar conceitos de arquitetura de microserviços, desenvolvimento de APIs REST, persistência de dados, segurança e testes automatizados utilizando o ecossistema Java e Spring Boot.

A estrutura em monorepo permite manter os serviços organizados em um único repositório, preservando a separação de responsabilidades entre os domínios da aplicação.
