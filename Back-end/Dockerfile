
# ***
FROM maven:3.8.5-jdk-11-slim AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -Dmaven.test.skip package

FROM openjdk:11-jdk-slim
ARG JAR_FILE=/app/target/*.jar
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]

