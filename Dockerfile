# === Tahap Build ===
FROM openjdk:17-slim AS build
WORKDIR /app

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# izin eksekusi pada mvnw
RUN chmod +x mvnw

RUN ./mvnw dependency:go-offline
COPY src/ ./src/
RUN ./mvnw clean install -DskipTests

# === Tahap Runtime ===
FROM openjdk:17-slim
WORKDIR /app
COPY --from=build /app/target/flix-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]