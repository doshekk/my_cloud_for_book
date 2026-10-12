FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /workspace

COPY pom.xml ./
RUN mvn -q dependency:go-offline

COPY src ./src
RUN mvn -q -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app

RUN mkdir -p /app/FileCloudFileBase

COPY --from=build /workspace/target/FileCloud-0.0.1-SNAPSHOT.jar /app/app.jar

EXPOSE 8080

ENV FILE_STORAGE_PATH=/app/FileCloudFileBase

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
