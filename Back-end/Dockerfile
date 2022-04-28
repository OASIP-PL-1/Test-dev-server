FROM maven:3.6.1-jdk-8-slim AS build
WORKDIR /Back-end
COPY pom.xml .
COPY src/ ./src/
RUN mvn -Dmaven.test.skip package

FROM openjdk:8-alpine
COPY --from=build /Back-end/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT exec java -jar app.jar