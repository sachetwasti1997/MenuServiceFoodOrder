# Stage 1: Build the Maven project
FROM maven:3.8.3-openjdk-17-slim AS build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src/ /app/src/
RUN mvn package -DskipTests

# Stage 2: Create the final Docker image
FROM openjdk:17-jdk-slim

WORKDIR /app
COPY --from=build /app/target/*.jar my-project.jar

EXPOSE 8072
CMD ["java", "-jar", "my-project.jar"]