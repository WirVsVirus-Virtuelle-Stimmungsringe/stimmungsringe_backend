# Backend Service (Spring Boot) für die [Familiarise App](https://devpost.com/software/virtuelle-stimmungsringe)


## Entwicklerinformationen
### Branches
**master** - Branch für Entwicklung  
**ci/release** - Branch für Buildpipeline (Google Actions, deployment auf Server media-it)

### Microstream
Der Storage-Pfad muss extern als Property gesetzt werden:

`java .... backend.jar --spring.profiles.active=microstream --backend.microstream.storage-path=/tmp/microstream-database/ --backend.microstream.backup-path=/tmp/microstream-database-backups/`

### Spring Boot Profiles
local dev with microstream and push: microstream, firebase-push-secrets


### Sonstiges
Port für Backend (localhost): 5000

