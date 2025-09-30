FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q dependency:go-offline
COPY src src
RUN mvn -q package -DskipTests

FROM eclipse-temurin:21-jre-alpine
RUN apk add --no-cache ffmpeg
WORKDIR /opt/app
COPY --from=build /app/target/*.jar service.jar
COPY src/main/resources/application.properties /opt/app/config/
ENTRYPOINT ["java","-jar","service.jar"]
EXPOSE 8080
