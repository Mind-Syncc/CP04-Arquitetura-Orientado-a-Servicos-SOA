# Integrantes

- Heloísa Fleury Jardim - RM556378
- Juan Fuentes Rufino - RM557673
- Rickelmyn de Souza Ruescas - RM556055
- Paulo Henrique Monteiro Golovanevsky - RM555300
- Pedro Henrique Silva Batista - RM558137

# AutoEscola3ESPV — Melhorias Implementadas (CP05 + CP04)

Este documento descreve as melhorias desenvolvidas sobre o projeto base da AutoEscola. O projeto é a **junção das entregas do CP04 e do CP05**:

- **CP04:** segurança, gestão de usuários e regras de negócio para o cancelamento de instruções.
- **CP05:** consumo de uma API externa (ViaCEP), documentação automática com Swagger, configuração de CORS e testes automatizados.

## Sumário

- Cadastro de Alunos
- Cadastro de Usuários com Senha Criptografada
- Controle de Acesso por Perfil (ADMIN / USER)
- Redefinição de Senha pelo Próprio Usuário
- Cancelamento de Instruções
- Consumo de API Externa (ViaCEP)
- Documentação com Swagger (OpenAPI)
- Configuração de CORS
- Testes Automatizados
- Exemplos de Requisições
- Stack e Decisões Técnicas

---

## Cadastro de Alunos

Endpoint responsável por registrar novos alunos na base de dados, com validação dos dados obrigatórios (nome, e-mail e CPF) e bloqueio de CPF já cadastrado, garantindo a integridade das informações antes da persistência.

O cliente informa apenas o **CEP** e o **número**: o restante do endereço é preenchido automaticamente a partir do ViaCEP (veja [Consumo de API Externa](#consumo-de-api-externa-viacep)).

## Cadastro de Usuários com Senha Criptografada

Implementado o cadastro de usuários do sistema com **criptografia de senha via BCrypt** (`BCryptPasswordEncoder`), eliminando o armazenamento de senhas em texto plano no banco de dados.

- A senha informada no cadastro nunca é persistida em texto puro — ela passa por hashing (`passwordEncoder.encode(...)`) antes de ser salva.
- Na autenticação, a comparação é feita via `passwordEncoder.matches(...)`, validando a senha informada contra o hash armazenado, sem nunca reverter o hash.
- Autenticação stateless via **JWT**, com token gerado no login e validado a cada requisição por um filtro dedicado (`SecurityFilter`).

## Controle de Acesso por Perfil (ADMIN / USER)

Adicionado um sistema de perfis de acesso (`Role`: `ADMIN`, `USER`) vinculado a cada usuário, com autorização aplicada via Spring Security.

Regras de acesso implementadas:

| Ação | Perfil exigido |
|---|---|
| Cadastrar usuários | ADMIN |
| Listar usuários | ADMIN |
| Atualizar perfil de usuários | ADMIN |
| Excluir usuários | ADMIN |
| Redefinir a própria senha | USER (autenticado) |
| Agendar/cancelar instruções | ADMIN ou USER |

A configuração de segurança (`SecurityConfig`) define essas regras por rota, com atenção à **ordem de avaliação dos matchers** — regras mais específicas (como `/usuarios/redefinicao_senha/**`) são declaradas antes das mais genéricas (`/usuarios/**`), evitando que uma regra abrangente sobreponha uma mais restrita.

## Redefinição de Senha pelo Próprio Usuário

Usuários autenticados podem redefinir a própria senha, com as seguintes validações de negócio:

- A nova senha é obrigatória e não pode ser igual à senha atual (validado via `passwordEncoder.matches`, comparando a senha em texto puro informada contra o hash já salvo).
- Apenas usuários **ativos** podem redefinir a senha.
- Um usuário só pode alterar a **própria** senha — o usuário autenticado (extraído do contexto de segurança via JWT) é validado contra o registro que está sendo alterado, impedindo que um usuário redefina a senha de outro.

## Cancelamento de Instruções

Serviço para cancelamento de instruções previamente agendadas, seguindo as regras de negócio definidas:

- É obrigatório informar o motivo do cancelamento, dentre as opções: **aluno desistiu**, **instrutor cancelou** ou **outros**.
- Uma instrução só pode ser cancelada com **antecedência mínima de 24 horas** em relação ao horário agendado.

A validação das regras de cancelamento foi implementada seguindo o padrão **Strategy** (mesmo padrão já utilizado no fluxo de agendamento), permitindo que novas regras de cancelamento sejam adicionadas no futuro sem alterar a lógica principal do serviço — cada regra é uma implementação independente de `ValidadorCancelamento`, injetada automaticamente na lista de validadores.

O status de cancelamento e o motivo são persistidos na própria instrução (`cancelada`, `motivo_cancelamento`), preservando o histórico do agendamento em vez de removê-lo da base.

## Consumo de API Externa (ViaCEP)

O cadastro de alunos consome a API pública [ViaCEP](https://viacep.com.br) para obter o endereço a partir do CEP, em vez de exigir que o cliente envie todos os campos.

Fluxo do `POST /alunos`:

1. O cliente envia `nome`, `email`, `cpf`, `cep` e `numero`.
2. O `AlunoService` verifica se o CPF já está cadastrado (consulta local, antes de qualquer chamada de rede).
3. A aplicação consulta o ViaCEP com o CEP informado (classe `utils/BuscaEnderecoViaCep`).
4. Logradouro, bairro, cidade e UF retornados pela API são usados, junto com o número informado, para montar o endereço do aluno.

Pontos de atenção:

- O DTO `DadosEndereco` mapeia a cidade com o nome `localidade` (mesmo nome retornado pelo ViaCEP). Por isso, nos JSONs que trazem o endereço completo (como o do instrutor), a cidade vai na chave `localidade`.
- O cadastro de aluno passa a depender da disponibilidade do serviço ViaCEP.

## Documentação com Swagger (OpenAPI)

A API possui documentação automática gerada com **springdoc-openapi**, com suporte a autenticação JWT na própria interface.

| Recurso | Endereço |
|---|---|
| Swagger UI | `http://localhost:8080/swagger-ui.html` |
| Contrato OpenAPI (JSON) | `http://localhost:8080/v3/api-docs` |
| Contrato OpenAPI (YAML) | `http://localhost:8080/v3/api-docs.yaml` |
| Contrato offline versionado | `api-docs.yaml` (raiz do repositório) |

A configuração fica na classe `config/OpenApiConfig`, que define título, descrição e o esquema de segurança `bearerAuth` (JWT). As rotas da documentação estão liberadas no Spring Security.

Como testar rotas protegidas pelo Swagger:

1. Execute o `POST /login` direto na interface e copie o valor de `tokenJWT` da resposta.
2. Clique em **Authorize**, cole **apenas o token** (sem o prefixo `Bearer`) e confirme.
3. Execute normalmente os endpoints protegidos (os cadeados aparecem fechados).

> O token expira em 60 minutos. Após esse prazo, faça o login novamente.

## Configuração de CORS

Foi configurada a política de CORS para permitir que aplicações front-end hospedadas em outra origem consumam a API. A configuração está na classe `infra/cors/CorsConfig` e é integrada ao `SecurityFilterChain` (`.cors(Customizer.withDefaults())`), para que as requisições *preflight* (`OPTIONS`), que não carregam o header `Authorization`, não sejam bloqueadas pelo Spring Security.

| Item | Valor permitido |
|---|---|
| Origens | `http://localhost:3000` e `http://localhost:5173` |
| Métodos | `GET`, `POST`, `PUT`, `DELETE`, `OPTIONS` |
| Headers | `Authorization` e `Content-Type` |

A configuração foi validada simulando um preflight: `OPTIONS /login` com a origem `http://localhost:5173` retorna `200` com os headers `Access-Control-Allow-*`, e com uma origem não permitida (por exemplo `http://localhost:9999`) retorna `403`.

## Testes Automatizados

Há um exemplo de teste automatizado para cada entidade do projeto, usando **JUnit 5**, **Mockito** e **MockMvc** em modo *standalone*. O controller é testado de forma isolada, com o service mockado, validando o contrato HTTP e as validações do Bean Validation, sem depender de banco de dados nem de internet.

| Entidade | Classe de teste | Cenários |
|---|---|---|
| Aluno | `AlunoControllerTest` | `POST /alunos` retorna `201 Created`; CEP inválido retorna `400 Bad Request` |
| Instrutor | `InstrutorControllerTest` | `POST /instrutor` retorna `201 Created`; CNH inválida retorna `400 Bad Request` |
| Instrução | `InstrucaoAgendamentoControllerTest` | `POST /instrucao` retorna `200 OK`; data no passado retorna `400 Bad Request` |
| Usuário | `UsuarioControllerTest` | `POST /usuarios` retorna `201 Created` (sem expor a senha na resposta); `role` inexistente retorna `400 Bad Request` |

Para executar todos os testes:

```bash
./mvnw test
```

No Windows (PowerShell), use `.\mvnw test`.

> Esses testes verificam o contrato dos controllers; as regras de autorização do Spring Security não são exercitadas por eles.

## Exemplos de Requisições
> É indicado criar um Usuário ADMIN manualmente no banco de dados para testar melhor os recursos da aplicação (a senha deve ser gravada já como hash BCrypt, caso contrário o login falha).

### Cadastro de Aluno

`POST /alunos`

```json
{
  "nome": "João Pedro Silva",
  "email": "joao.pedro@email.com",
  "cpf": "12345678900",
  "cep": "01310100",
  "numero": "123"
}
```

> O CEP deve ter 8 dígitos numéricos, sem hífen. O restante do endereço é preenchido pelo ViaCEP.

### Cadastro de Instrutor

`POST /instrutor`

```json
{
  "nome": "Mario Eduardo Santos",
  "telefone": "11976543210",
  "email": "mario.eduardo@autoescola.com",
  "cnh": "12345678900",
  "especialidade": "CARROS",
  "endereco": {
    "logradouro": "Avenida Paulista",
    "numero": "1500",
    "complemento": "Sala 10",
    "bairro": "Bela Vista",
    "localidade": "São Paulo",
    "uf": "SP",
    "cep": "01310200"
  }
}
```

> No endereço, a cidade é informada na chave `localidade`.

### Cadastro de Usuário (restrito a ADMIN)

`POST /usuarios`

```json
{
  "login": "admin",
  "senha": "123456",
  "role": "ADMIN"
}
```

### Login

`POST /login`

```json
{
  "login": "admin",
  "senha": "123456"
}
```

Resposta (`200 OK`):

```json
{
  "tokenJWT": "eyJhbGciOiJIUzI1NiJ9..."
}
```

O token retornado deve ser enviado nas requisições seguintes no header:

```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

### Redefinição da Própria Senha (usuário autenticado)

`PUT /usuarios/redefinicao_senha/{id}`

```json
{
  "senha": "novaSenha123"
}
```

### Agendamento de Instrução

`POST /instrucao`

Informando o instrutor diretamente:

```json
{
  "id_aluno": 1,
  "id_instrutor": 1,
  "data_hora": "25/11/2026 - 09:00"
}
```

Ou deixando o sistema escolher um instrutor disponível pela especialidade:

```json
{
  "id_aluno": 1,
  "especialidade": "CARROS",
  "data_hora": "25/11/2026 - 09:00"
}
```

> A data deve estar no formato `dd/MM/yyyy - HH:mm`, ser uma data futura (ajuste o exemplo acima se ela já tiver passado), e respeitar o horário de funcionamento (dias úteis, entre 06:00 e 20:59).

### Cancelamento de Instrução

`DELETE /instrucao`

```json
{
  "idInstrucao": 1,
  "motivoCancelamento": "DESISTENCIA"
}
```

> Requer antecedência mínima de 24 horas em relação ao horário agendado; caso contrário, a API retorna erro de validação.

---

## Stack e Decisões Técnicas

- **Java 25** / **Spring Boot 4**
- **Spring Security** com autenticação **JWT** stateless
- **Spring Data JPA** + **MySQL**
- **Flyway** para versionamento e controle de migrações do banco de dados
- **Bean Validation** (Jakarta Validation) para validação de DTOs de entrada
- Padrão **Strategy** para regras de negócio extensíveis (agendamento e cancelamento de instruções)
- **ViaCEP** como API externa para consulta de endereço por CEP
- **springdoc-openapi** (Swagger UI) para documentação automática da API
- **CORS** configurado e integrado ao Spring Security
- **JUnit 5**, **Mockito** e **MockMvc** para testes automatizados
