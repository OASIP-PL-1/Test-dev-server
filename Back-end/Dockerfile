# # 1
# FROM maven:3.6.1-jdk-8-slim AS build
# RUN mkdir -p /workspace
# WORKDIR /workspace
# COPY pom.xml /workspace
# COPY src /workspace/src
# RUN mvn -f pom.xml clean package

# FROM openjdk:8-alpine
# COPY --from=build /workspace/target/*.jar app.jar
# EXPOSE 8080
# ENTRYPOINT ["java","-jar","app.jar"]

# # 2
# FROM maven:3.8.2-jdk-8
# WORKDIR /bezkoder-app
# COPY . .
# RUN mvn clean install
# CMD mvn spring-boot:run

# # 3
# FROM maven:3.8.5-openjdk-17-slim as build
# WORKDIR /app
# COPY pom.xml .
# COPY src/ ./src/
# RUN mvn -Dmaven.test.skip package

# FROM openjdk:17-jdk-alpine
# ARG JAR_FILE=/app/target/*.jar
# COPY --from=build ${JAR_FILE} app.jar
# EXPOSE 8080
# ENTRYPOINT exec java -jar app.jar

# ***
FROM maven:3.8.5-jdk-11-slim AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -f pom.xml clean package

FROM openjdk:11-jdk-slim
ARG JAR_FILE=/app/target/*.jar
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]

