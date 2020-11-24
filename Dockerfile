FROM openjdk:8-jre-buster

COPY target/fun-*.jar /app.jar

EXPOSE 8080
