FROM maven:3-eclipse-temurin-17 as build
RUN mkdir /app/
COPY ./ /app/
WORKDIR /app/
RUN mvn clean package -Pdocker_local

FROM eclipse-temurin:17
LABEL authors="Grzegorz Jewusiak"
COPY --from=build /app/target/grzesbank-api-0.0.1-SNAPSHOT.jar app.jar
RUN mkdir "db"

ENTRYPOINT ["java","-jar","/app.jar"]