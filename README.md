# Backend Cooperativa

# Visão Geral

Api Rest implementada para gestão de pautas de uma Cooperativa

# Executando o projeto

A forma mais rapida de você executar este projeto é através do **Docker** e **Docker Compose** que irá subir três contanainers:
```Backend Java Spring Boot - Api Rest```, ```RabbitMq``` e ```Postgresql```.     

Veja como é facil:

> Certifique-se de ter o Docker (com docker-composer) instalado na sua máquina e que esteja em funcionamento.
> Verifique se a versão do Java instalado é seja maioe igual a 11

1 - Clonando o repositorio:
> ```git git@github.com:BSTK/backend-cooperativa.git```

2 - Dentro do diretório do projeto ```backend-cooperativa```, execute o script ```start.sh```
> ```cd ./backend-cooperativa```
> 
> ```./start.sh```

3 - Através de um cliente rest como Postman/Insomia execute os testes de acordo com a sessão "Endpoints"
> ```http://localhost:4200/#/okk-url/```

4 - Para finalizar, basta parar os containers
> ```CTRL + C```

# Tecnologias utilizadas
- Java 11
- JUnit 5
- Mockito
- AssertJ  
- Spring Boot 2.7
- PostgreSql
- RabbitMq
- Docker
- Swagger

# Documentação Swagger
- http://localhost:8080/swagger-ui.html#/

# Visualizando mensagens na fila do RabbitMq
- http://localhost:15672/#/queues/%2F/queue.pauta-finalizada
- Clique no menu "Get Messages" e no campo coloque a quantidade de mensagens, ex: 100

## Endpoints

- Cadastrar uma Pauta
````json
POST - /v1/api/pautas

curl --location 'http://localhost:8080/v1/api/pautas' \
--header 'Content-Type: application/json' \
--data '{
"titulo": "Titulo da Pauta"
}'
````

- Iniciar uma Sessão
````json
POST - /v1/api/pautas/{pautaId}/iniciar-sessao

curl --location --request POST 'http://localhost:8080/v1/api/pautas/1/iniciar-sessao?tempoDuracao=2' \
--data ''
````

- Votar pauta
````json
POST - /v1/api/pautas/{pautaId}/votar

curl --location 'http://localhost:8080/v1/api/pautas/1/votar' \
--header 'Content-Type: application/json' \
--data '{
    "associadoId": 1,
    "voto": "Sim" // "Sim" / "Não"
}'
````

- Visualizar resultado
````json
GET - /v1/api/pautas/{pautaId}/resultado

curl --location 'http://localhost:8080/v1/api/pautas/1/resultado' \
--data ''
````
