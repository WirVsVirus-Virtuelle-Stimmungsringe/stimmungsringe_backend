# Backend Service (Spring Boot) für die [Familiarise App](https://devpost.com/software/virtuelle-stimmungsringe)


## Entwicklerinformationen
### Branches
**master** - Branch für Entwicklung  
**ci/release** - Branch für Buildpipeline (automatisches deployment auf AWS)

### DynamoDB
Die DynamoDB kann lokal gestartet werden - [siehe Anleitung](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/DynamoDBLocal.DownloadingAndRunning.html)
`java -Djava.library.path=./DynamoDBLocal_lib -jar DynamoDBLocal.jar -sharedDb`

Spring Boot Profile-Konstellationen: dynamodb-localdev, dynamodb

`aws dynamodb list-tables --endpoint-url http://localhost:8000`

### Spring Boot Profiles
local dev with push: dynamodb-localdev, dynamodb, firebase-push-secrets


### Sonstiges
Port für Backend (localhost): 5000

