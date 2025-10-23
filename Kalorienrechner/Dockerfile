# ---------- Build Stage ----------
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
# nur build files cachen
COPY gradlew ./
COPY gradle gradle
RUN chmod +x gradlew
COPY build.gradle settings.gradle ./
# optional: falls du versionskataloge o.ä. hast -> kopieren
# COPY gradle/libs.versions.toml gradle/libs.versions.toml

# resolve deps (ohne sources) für besseren Cache
RUN ./gradlew --no-daemon dependencies || true

# rest des codes
COPY src src
RUN ./gradlew --no-daemon clean bootJar

# ---------- Runtime Stage ----------
FROM eclipse-temurin:21-jre
WORKDIR /app
# Render setzt $PORT -> wir reichen an Spring weiter
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75 -Dserver.port=${PORT}"
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]