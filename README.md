# Backend Cooperativa

# Executando o projeto

A forma mais rapida de você executar este projeto é através do **Docker** e **Docker Compose** que irá subir três containers:
```O projeto da Api Rest```, ```RabbitMq``` e ```Postgresql```.     

Veja como é facil:

> Certifique-se de ter o Docker (com docker-composer) instalado na sua máquina e que esteja em funcionamento.
> Verifique se a versão do Java instalado seja maior igual a 11

1 - Clonando o repositorio:
> ```git clone git@github.com:BSTK/backend-cooperativa.git```

2 - Dentro do diretório do projeto ```backend-cooperativa```, execute o script ```start.sh```
> ```cd ./backend-cooperativa```
> 
> IMPORTANTE: Execute esse script start.sh com um terminal Linux ou no Windows com o Git Bash
> 
> ```./start.sh```

3 - Através de um cliente rest como Postman/Insomia execute os testes de acordo com a sessão "Endpoints"

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
- No navegador, abra a url: http://localhost:15672/#/queues/%2F/queue.pauta-finalizada. 
  Utilize usuário: ```guest``` e senha ```guest```
- Clique no menu "Get Messages" e no campo coloque a quantidade de mensagens, ex: 100

## Endpoints

- Cadastrar uma Pauta
````java
POST - /v1/api/pautas

curl --location 'http://localhost:8080/v1/api/pautas' \
--header 'Content-Type: application/json' \
--data '{
"titulo": "Titulo da Pauta"
}'
````

- Iniciar uma Sessão
````java
POST - /v1/api/pautas/{pautaId}/iniciar-sessao

curl --location --request POST 'http://localhost:8080/v1/api/pautas/1/iniciar-sessao?tempoDuracao=2'
````

- Votar pauta
````java
POST - /v1/api/pautas/{pautaId}/votar

curl --location 'http://localhost:8080/v1/api/pautas/1/votar' \
--header 'Content-Type: application/json' \
--data '{
    "associadoId": 1,
    "voto": "Sim" // "Sim" / "Não"
}'
````

- Visualizar resultado
````java
GET - /v1/api/pautas/{pautaId}/resultado

curl --location 'http://localhost:8080/v1/api/pautas/1/resultado'
````

### Respostas para as 'Tarefas Bônus'

> - Tarefa Bônus 1 - Integração com sistema externo:
> 
> Está tarefa foi descontinuada, segundo informado no inicio do teste

> - Tarefa Bônus 2 - Menssageria e fila:
> 
> Implementando em cima do RabbitMq. Ppenas o ```produtor``` da mensagem

> - Tarefa Bônus 3 - Performance:
> 
> ```Aquilo que não se pode medir, não se pode melhorar```.
> 
> Antes de aplicar qualquer melhoria de performace, o ideal seria acompanhar e medir através de alguma
> ferramenta de monitoramento ou APM o desempenho da aplicação. Depois de algum acompanhamento, coletado os dados ai sim tem-se indicios
> de onde deve ser melhorado.
> Uma alternativa seria escalar de forma horizontal, criando mais instâncias da aplicação.
> Uma outra seria uma melhoria endpoint de /votar, no qual hoje é executado três queries: 
> - 1 para validar se há sessão aberta
> - 1 para válidar se associado já votou
> - 1 para inserir voto
> 
> Daria para fazer numa query só adicionando constrainsts no banco de dados para validar a inserção do voto.
> 
> No mais o uso de ferramentas como JMeter/Gatling/K6 pode ajudar nessa questão

> - Tarefa Bônus 4 - Versionamento da API:
> 
> Foi utilizando o versionamento na própria url da api (URI Path), através do prefixo ```/v1``` para sinalizar 
a versão da api que está em funcionamento. É uma das formas mais comuns tanto de se implementar quando de entender 
> 
> Existem outras formas como:
>  - Custom HTTP Header: ```header: 'X-ApiVersion: 1.0```
>  - Query Parameter: ```https://api.com/api/pautas?api-version=1.0```
>  - Subdomínio: ```https://v1.api.com/api/pautas``` 
