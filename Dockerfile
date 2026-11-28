# ---- build stage ----
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY maven-mirror-settings.xml /tmp/settings.xml
COPY pom.xml .
COPY src ./src
# Maven Central sometimes answers "429 Too Many Requests" to shared servers.
# So: try a mirror, then plain Maven Central, and retry a few times.
RUN for i in 1 2 3 4; do \
      mvn -B -q -s /tmp/settings.xml -DskipTests package && exit 0; \
      echo "Attempt $i with mirror failed"; sleep 15; \
      mvn -B -q -DskipTests package && exit 0; \
      echo "Attempt $i with Maven Central failed"; sleep 30; \
    done; exit 1

# ---- run stage ----
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-Xmx256m","-XX:MaxMetaspaceSize=128m","-XX:+UseSerialGC","-Xss512k","-jar","app.jar"]
