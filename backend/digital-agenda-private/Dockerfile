

# Usa SOLO Java, niente Maven, perché il JAR lo ha già buildato nella pipeline
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copia il jar compilato dalla cartella target

COPY target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]