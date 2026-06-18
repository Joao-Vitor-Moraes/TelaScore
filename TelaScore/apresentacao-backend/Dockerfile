FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn -pl apresentacao-backend -am package -Dmaven.test.skip=true

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/apresentacao-backend/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
