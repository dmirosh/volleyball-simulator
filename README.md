### Run client in development mode on **8080** port
```
./gradlew browserDevelopmentRun
```

### Run server with embedded client on **8081** port
```
./scripts/start-server
```
or 
```
./gradlew :server:run
```

### Persistence
All data is stored in **H2** db in file **~/volleyball.db.mv.db**