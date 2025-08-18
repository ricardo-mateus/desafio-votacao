# Desafio Votação
## 📖 Sobre o Projeto
Esta é uma API REST desenvolvida em Java com Spring Boot para gerenciar sessões de votação em assembleias de cooperativas. A solução permite o cadastro de pautas, abertura de sessões de votação com duração customizável, registro de votos por associados (com validação de CPF opcional) e apuração de resultados. O projeto foi desenvolvido como parte de um desafio técnico, com foco em boas práticas, arquitetura limpa e manutenibilidade.

# ✨ Funcionalidades Principais

- Gestão de Pautas: Cadastrar e listar pautas para votação.
- Sessões de Votação: Abrir sessões para pautas específicas, com duração configurável (padrão: 1 minuto).
- Registro de Votos: Permitir que associados votem ("Sim" ou "Não") em uma pauta com sessão aberta, com votação única por associado.
- Validação de CPF: (Opcional) Consulta a um serviço fake para verificar se o CPF do associado está apto a votar.
- Apuração de Resultados: Contabilizar votos e exibir o resultado final de uma pauta.

# 🛠️ Tecnologias Utilizadas

- **Java 17**: Linguagem principal.
- **Spring Boot 3.5.4**: Framework para construção da aplicação.
- **Maven**: Gerenciador de dependências e build.
- **Spring Data MongoDB**: Persistência em banco NoSQL (MongoDB Atlas).
- **Lombok**: Redução de código boilerplate (getters, setters, etc.).
- **Springdoc OpenAPI (Swagger)**: Documentação interativa da API.

# 🚀 Como Executar o Projeto

## Pré-requisitos

- **Java 17**: Instale o JDK 17 (Adoptium).
- **Maven**: Instale o Maven 3.8 ou superior (Maven).

A aplicação estará disponível em http://localhost:8080.


# 📚 Documentação da API (Swagger)
A API está documentada com Swagger UI, permitindo visualizar e testar os endpoints interativamente:

Acesse: 
- http://localhost:8080/swagger-ui/index.html

OpenAPI JSON: 
- http://localhost:8080/v3/api-docs

## 🗺️ Endpoints da API
Todos os endpoints estão sob o prefixo `/api/v1`.

| Método | Endpoint                               | Descrição                                                               | Exemplo de Body (Request)                                     |
| :----- | :------------------------------------- | :---------------------------------------------------------------------- | :------------------------------------------------------------ |
| POST   | `/api/v1/pautas`                      | Cria uma nova pauta.                                                    | `{"titulo": "Aprovação de contas", "descricao": "Detalhes..."}` |
| GET    | `/api/v1/pautas`                      | Lista todas as pautas cadastradas.                                      | N/A                                                           |
| POST   | `/api/v1/pautas/{id}/abrir-sessao`    | Abre uma sessão de votação para uma pauta.                              | `{"duracaoSegundos": 120}`                                    |
| POST   | `/api/v1/pautas/{id}/votos`           | Registra o voto de um associado em uma pauta com sessão aberta.         | `{"associadoId": "id-do-associado", "cpf": "12345678900", "votoSim": true}` |
| GET    | `/api/v1/pautas/{id}/resultado`       | Apura e exibe o resultado da votação para uma pauta.                    | N/A                                                           |


# 🏛️ Arquitetura e Decisões de Design
O projeto segue uma arquitetura em camadas:

- **Controller**: Exibe endpoints REST, valida dados de entrada (DTOs) e retorna respostas, sem lógica de negócio.
- **Service**: Contém a lógica de negócio, orquestrando operações e interagindo com o repositório.
- **Repository**: Abstrai o acesso ao MongoDB com Spring Data MongoDB.
- **Entity**: Representa documentos persistidos no MongoDB.
- **DTO**: Objetos para transferência de dados entre camadas, protegendo as entidades de domínio.
- **Exception**: Centraliza o tratamento de erros com @ControllerAdvice para respostas consistentes.

## 🚀 Performance e Escalabilidade
A aplicação foi projetada para lidar com cenários de alta carga, como centenas de milhares de votos, garantindo desempenho e escalabilidade. As estratégias abaixo foram implementadas:

- **Agregação no Banco de Dados**:
  - O endpoint `GET /api/v1/pautas/{id}/resultado` utiliza o **Aggregation Framework** do MongoDB para contabilizar votos ("Sim" e "Não") diretamente no banco.
  - Apenas o resultado consolidado é retornado, reduzindo o consumo de memória e o tráfego de rede entre a aplicação e o MongoDB Atlas.

- **Cache de Resultados**:
  - Resultados de pautas com sessões encerradas são imutáveis e armazenados em cache com o mecanismo do Spring (`@Cacheable`).
  - Na primeira requisição, o resultado é calculado e armazenado. Requisições subsequentes são atendidas diretamente pelo cache, eliminando consultas ao banco de dados.

# Versionamento da API
A API usa URL Path Versioning (/api/v1/...), uma abordagem simples e amplamente adotada que facilita a manutenção de versões sem impactar clientes existentes (URL Path Versioning).

