# MyBudget API

<img src = "Logo - MyBudget.png" alt="Logo MyBudget">


## Descrição

A API MyBudget é projetada para gerenciar usuários e despesas. A seguir, você encontrará a documentação detalhada para os endpoints disponíveis, incluindo exemplos de solicitações e respostas.

---

## Tecnologias e Bibliotecas Utilizadas

### Frameworks e Ferramentas

- **Spring Boot**: Plataforma para simplificar o desenvolvimento de aplicações Java.
  - `spring-boot-starter-web`: Facilita a criação de aplicações web e APIs RESTful, incluindo suporte para Tomcat, Jackson e outras bibliotecas essenciais.
  - `spring-boot-starter-data-jpa`: Integra JPA (Java Persistence API) para manipulação de dados com Hibernate ou outras implementações JPA.
  - `spring-boot-starter-security`: Adiciona funcionalidades de segurança para autenticação e autorização.
  - `spring-boot-starter-validation`: Suporte para validação de dados usando a especificação Bean Validation (JSR 380).

### Banco de Dados

- **MySQL**: Sistema de gerenciamento de banco de dados relacional.
  - `mysql-connector-j`: Conector JDBC para interagir com bancos de dados MySQL.

### Programação e Ferramentas

- **Lombok**: Reduz o boilerplate de código Java, gerando automaticamente métodos comuns como getters e setters.

### Segurança

- **JWT (JSON Web Token)**: Utilizado para criar e validar tokens de autenticação.
  - `java-jwt`: Biblioteca da Auth0 para manipulação de JWTs.

### Documentação da API

- **OpenAPI (Swagger)**: Para geração de documentação interativa da API.
  - `springdoc-openapi-starter-webmvc-ui`: Gera documentação OpenAPI (Swagger) para a API, facilitando a visualização e interação.

___

## Endpoints

### 1. Registro de Usuário

**Endpoint:** `POST /mybudget/user-register`

**Descrição:** Cria um novo usuário no sistema.

**Request Body:**

```json
{
  "name": "Nome completo",
  "email": "email@example.com",
  "password": "password"
}
``` 
___
## Autenticação

### 2. Login

**Endpoint:** `POST /mybudget/login`

**Descrição:** Este endpoint autentica um usuário e retorna um token JWT que pode ser usado para acessar endpoints protegidos.

**Request Body:**

```json
{
  "email": "email@example.com",
  "password": "password"
}
```
___
## Gerenciamento de Despesas

### 3. Registrar Despesa

**Endpoint:** `POST /mybudget/expense/register`

**Descrição:** Este endpoint permite que um usuário autenticado registre uma nova despesa.

**Headers:**

- `Authorization` (string, obrigatório): Token JWT do usuário. Deve ser incluído no header da requisição para autenticação.

**Request Body:**

```json
{
  "amount": 160.62,
  "description": "NET Claro - Internet e telefone"
}
```

- **Status 201 Created:**

**Exemplo de Resposta:**
```json
{
  "id": "8d0d24c4-1cc0-4769-85c2-584632b7e997",
  "description": "NET Claro - Internet e telefone",
  "amount": 160.62,
  "status": "PENDING",
  "date": "2024-08-20"
}
```
___

### Atualizar Despesa

**Endpoint:** `PUT /mybudget/expense/update/{id}`

**Descrição:** Este endpoint permite que um usuário autenticado atualize uma despesa existente. É possível atualizar o `status`, `description` e `amount` da despesa.

**Headers:**

- `Authorization` (string, obrigatório): Token JWT do usuário. Deve ser incluído no header da requisição para autenticação.

**Path Parameters:**

- `id` (UUID, obrigatório): Identificador único da despesa que será atualizada.

**Request Body:**

Você pode enviar um ou mais dos seguintes campos no corpo da requisição:

- **Atualizar Status:**

```json
{
  "status": "PAID"
}
```
- **Atualizar Descrição:**

```json
{
  "description": "Nova descrição da despesa"
}
```

- **Atualizar valor:**

```json
{
  "amount": 200.75
}
```

- **Atualizar todos os campos:**

```json
{
  "amount": 200.75,
  "description": "Nova descrição da despesa",
  "status": "PAID"
}
```
___
### Listar Todas Despesas

**Endpoint:** `GET /mybudget/expense/list-all`

**Descrição:** Este endpoint retorna a lista de despesas associadas ao usuário autenticado. As despesas são retornadas em ordem crescente pela data.

**Headers:**

- `Authorization` (string, obrigatório): Token JWT do usuário. Deve ser incluído no header da requisição para autenticação.

**Request Parameters:**

- **Nenhum**. Todos os parâmetros são passados através dos headers.

**Respostas:**

- **Status 200 OK:**

  Se a requisição for bem-sucedida, o servidor retornará uma lista de despesas do usuário autenticado.

  **Exemplo de Resposta:**

```json
  [
    {
      "id": "123e4567-e89b-12d3-a456-426614174000",
      "description": "NET Claro - Internet e telefone",
      "amount": 160.62,
      "status": "PENDING",
      "date": "2024-08-08"
    },
    {
      "id": "123e4567-e89b-12d3-a456-426614174001",
      "description": "Compra de supermercado",
      "amount": 85.50,
      "status": "PAID",
      "date": "2024-08-07"
    }
  ]
```

- **Status Status 404 Not Found:**

```json
{
  "error": "Você não tem despesas registradas no momento."
}
```
___

### Listar Despesas Pendentes

**Endpoint:** `GET /mybudget/expense/pending`

**Descrição:** Este endpoint retorna a lista de despesas pendentes associadas ao usuário autenticado. As despesas são retornadas em ordem crescente pela data.

**Headers:**

- `Authorization` (string, obrigatório): Token JWT do usuário. Deve ser incluído no header da requisição para autenticação.

**Request Parameters:**

- **Nenhum**. Todos os parâmetros são passados através dos headers.

**Respostas:**

- **Status 200 OK:**

  Se a requisição for bem-sucedida, o servidor retornará uma lista de despesas do usuário autenticado.

**Exemplo de Resposta:**

```json
[
  {
    "id": "123e4567-e89b-12d3-a456-426614174000",
    "description": "NET Claro - Internet e telefone",
    "amount": 160.62,
    "status": "PENDING",
    "date": "2024-08-08"
  }
]
```

- **Status Status 404 Not Found:**

```json
{
  "error": "Não há despesas pendentes no momento."
}
```
___

### Listar Despesas Pagas

**Endpoint:** `GET /mybudget/expense/paid`

**Descrição:** Este endpoint retorna a lista de despesas pagas associadas ao usuário autenticado. As despesas são retornadas em ordem crescente pela data.

**Headers:**

- `Authorization` (string, obrigatório): Token JWT do usuário. Deve ser incluído no header da requisição para autenticação.

**Request Parameters:**

- **Nenhum**. Todos os parâmetros são passados através dos headers.

**Respostas:**

- **Status 200 OK:**

  Se a requisição for bem-sucedida, o servidor retornará uma lista de despesas do usuário autenticado.

**Exemplo de Resposta:**

```json
[
 {
   "id": "123e4567-e89b-12d3-a456-426614174001",
   "description": "Compra de supermercado",
   "amount": 85.50,
   "status": "PAID",
   "date": "2024-08-07"
 }
]
```

- **Status Status 404 Not Found:**

```json
{
  "error": "Não há despesas pendentes no momento."
}
```
___

### Excluir Despesa

**Endpoint:** `DELETE /mybudget/expense/delete/{id}`

**Descrição:** Este endpoint permite que um usuário autenticado exclua uma despesa existente. A exclusão é feita com base no identificador único da despesa.

**Headers:**

- `Authorization` (string, obrigatório): Token JWT do usuário. Deve ser incluído no header da requisição para autenticação.

**Path Parameters:**

- `id` (UUID, obrigatório): Identificador único da despesa que será excluída.

**Respostas:**

- **Status 204 No Content:**

  Se a despesa for excluída com sucesso, o servidor retornará um status `204 No Content` indicando que a exclusão foi bem-sucedida e que não há conteúdo a ser retornado.


  **Exemplo de Resposta:**
  `Não há corpo na resposta`

- **Status 401 Unauthorized:**

  Se o token JWT fornecido for inválido ou ausente, o servidor retornará um erro.


  **Exemplo de Resposta:**

```json
{
  "error": "Token de autenticação inválido."
}
```

**Outras Respostas:**
- **Status Status 403 Forbidden:**

```json
{
  "error": "Você não tem permissão para excluir esta despesa."
}
```

- **Status Status 404 Not Found:**

```json
{
  "error": "Despesa não encontrada."
}
```