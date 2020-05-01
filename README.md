# Backend Service (Spring Boot) für die [Familiarise App](https://devpost.com/software/virtuelle-stimmungsringe)


## Entwicklerinformationen
### Branches
**master** - Branch für Entwicklung  
**ci/release** - Branch für Buildpipeline (automatisches deployment auf AWS)

### DynamoDB
Die DynamoDB kann lokal gestartet werden - [siehe Anleitung](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/DynamoDBLocal.DownloadingAndRunning.html)

Spring Boot Profile-Konstellationen: dynamodb-localdev, dynamodb

`aws dynamodb list-tables --endpoint-url http://localhost:8000`


### Sonstiges
Port für Backend (localhost): 5000
