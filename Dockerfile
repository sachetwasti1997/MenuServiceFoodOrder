FROM openjdk:17-jdk-slim

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} menuservice.jar

ENTRYPOINT [ "java", "-jar", "/menuservice.jar" ]