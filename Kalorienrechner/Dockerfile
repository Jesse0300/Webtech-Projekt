# ---------- Build Stage ----------
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
# nur build files cachen
COPY gradlew ./
COPY gradle gradle
RUN chmod +x gradlew
COPY build.gradle settings.gradle ./


RUN ./gradlew --no-daemon dependencies || true

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