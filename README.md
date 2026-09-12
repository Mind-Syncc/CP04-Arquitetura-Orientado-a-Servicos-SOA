# Integrantes

- Heloísa Fleury Jardim - RM556378
- Juan Fuentes Rufino - RM557673
- Rickelmyn de Souza Ruescas - RM556055
- Paulo Henrique Monteiro Golovanevsky - RM555300
- Pedro Henrique Silva Batista - RM558137

# AutoEscola3ESPV — Melhorias Implementadas

Este documento descreve as melhorias desenvolvidas sobre o projeto base da AutoEscola, com foco em segurança, gestão de usuários e regras de negócio para o cancelamento de instruções.

## Sumário

- Cadastro de Alunos
- Cadastro de Usuários com Senha Criptografada
- Controle de Acesso por Perfil (ADMIN / USER)
- Redefinição de Senha pelo Próprio Usuário
- Cancelamento de Instruções
- Stack e Decisões Técnicas

---

## Cadastro de Alunos

Endpoint responsável por registrar novos alunos na base de dados, com validação de dados obrigatórios (nome, telefone, e-mail, CPF) e endereço completo, garantindo integridade das informações antes da persistência.

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

## Exemplos de Requisições 
> É indicado criar um Usuário ADMIN manualmente no banco de dados para testar melhor os recursos da aplicação

### Cadastro de Aluno

`POST /alunos`

```json
{
  "nome": "João Pedro Silva",
  "telefone": "11987654321",
  "email": "joao.pedro@email.com",
  "cpf": "12345678900",
  "dadosEndereco": {
    "logradouro": "Rua das Flores",
    "numero": "123",
    "complemento": "Apto 45",
    "bairro": "Centro",
    "cidade": "São Paulo",
    "uf": "SP",
    "cep": "01310100"
  }
}
```

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
    "cidade": "São Paulo",
    "uf": "SP",
    "cep": "01310200"
  }
}
```

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
  "data_hora": "21/09/2026 - 09:00"
}
```

Ou deixando o sistema escolher um instrutor disponível pela especialidade:

```json
{
  "id_aluno": 1,
  "especialidade": "CARROS",
  "data_hora": "21/09/2026 - 09:00"
}
```

> A data deve estar no formato `dd/MM/yyyy - HH:mm`, ser uma data futura, e respeitar o horário de funcionamento (dias úteis, entre 06:00 e 20:59).

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

- **Java 25** / **Spring Boot**
- **Spring Security** com autenticação **JWT** stateless
- **Spring Data JPA** + **MySQL**
- **Flyway** para versionamento e controle de migrações do banco de dados
- **Bean Validation** (Jakarta Validation) para validação de DTOs de entrada
- Padrão **Strategy** para regras de negócio extensíveis (agendamento e cancelamento de instruções)
