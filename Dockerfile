# ---------- Build stage ----------
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

# Kopier Gradle-wrapper og buildfiler først (cache-vennlig)
COPY gradlew gradlew
COPY gradle gradle
COPY build.gradle* settings.gradle* ./

# Gjør wrapper kjørbar + fix CRLF hvis du committer fra Windows
RUN chmod +x gradlew && sed -i 's/\r$//' gradlew || true

# Hent dependencies (for bedre lag-cache)
RUN ./gradlew --no-daemon dependencies || true

# Kopier resten av koden og bygg fat JAR
COPY . .
RUN ./gradlew --no-daemon clean bootJar

# ---------- Runtime stage ----------
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Kjør som ikke-root (valgfritt)
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Kopier JAR fra builder
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
