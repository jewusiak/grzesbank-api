FROM eclipse-temurin:17
LABEL authors="Grzegorz Jewusiak"
COPY target/grzesbank-api-0.0.1-SNAPSHOT.jar app.jar
RUN mkdir "db"

ENTRYPOINT ["java","-jar","/app.jar"]