FROM hirokimatsumoto/alpine-openjdk-11
 
EXPOSE 8080 9990
 
WORKDIR /app
 
COPY ./target/sudoku-verifier-1.0.0-SNAPSHOT.jar ./app.jar

CMD java -jar app.jar