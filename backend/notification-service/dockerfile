# monolita/Dockerfile

# Stage 1: Build (ha Maven, JDK, tutto)
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
COPY pom.xml .
# Scarica le dipendenze in cache separata (ottimizzazione layer cache Docker)
RUN mvn dependency:go-offline -q
COPY src ./src
RUN mvn package -DskipTests

# Stage 2: Runtime (solo JRE leggero, niente build tools)
FROM eclipse-temurin:21-jre-alpine AS runtime
WORKDIR /app
# Crea utente non-root per sicurezza
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
COPY --from=builder /app/target/*.jar app.jar
USER appuser
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]