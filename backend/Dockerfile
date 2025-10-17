# ---------- BUILD ----------
FROM gradle:8.8-jdk21-alpine AS builder
WORKDIR /home/gradle/src
# Kopier hele prosjektet med riktig eierskap
COPY --chown=gradle:gradle . .

RUN gradle --no-daemon clean bootJar

# ---------- RUNTIME ----------
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
COPY --from=builder /home/gradle/src/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
