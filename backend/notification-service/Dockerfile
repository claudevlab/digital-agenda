# notification-service/Dockerfile

# Stage 1: Build (Immagine ufficiale Maven + Java 21)
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -q
COPY src ./src
RUN mvn package -DskipTests

# Stage 2: Runtime (solo JRE leggero)
FROM eclipse-temurin:21-jre-alpine AS runtime
WORKDIR /app
# Crea utente non-root per sicurezza
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
COPY --from=builder /app/target/*.jar app.jar
USER appuser
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
