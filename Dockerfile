#FROM ubuntu:22.04
#FROM openjdk:17
#FROM amazoncorretto:17
FROM eclipse-temurin:17
LABEL authors="Grzegorz Jewusiak"
COPY target/grzesbank-api-0.0.1-SNAPSHOT.jar app.jar
RUN mkdir "db"
#RUN apt update && apt install nginx -yq
#RUN service nginx stop
#COPY docker_resources/default /etc/nginx/sites-available/default
#RUN service nginx start


ENTRYPOINT ["java","-jar","/app.jar"]

