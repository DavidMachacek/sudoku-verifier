# sudoku-verifier

Stand alone microservice for validating Sudoku.

When everything is ok, the service will return 204. Otherwise it returns 422 - Unprocessable entity along with error description why the sudoku application could not have been processed or be verified as ok.

Uses coroutines and supports distributed logging.

#### Technologies
- Kotlin / Kotlinx
- Spring Boot (with default embedded Tomcat)

#### Request
```
curl --request POST \
  --url http://localhost:8080/sudoku \
  --header 'x-b3-spanid: 7596dc9c9411c0f0' \
  --header 'x-b3-traceid: 7596dc9c9411c0f0' \
  --form sudoku=<your-sudoku-file>
  ```

#### Documentation
To generate Swagger documentation use maven profile "generateSwagger".
```
mvn clean package -P generateSwagger
```

#### Configuration variables
| VARIABLE      | Description | default |
| ----------- | ----------- | ----------- |
| SUDOKU_PARSER_DELIMITER | sets delimiter for file parser | , |