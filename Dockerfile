# Build
FROM eclipse-temurin:23-jdk-noble AS build
WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./
RUN chmod +x gradlew

# Warm dependency cache (fails fast / caches better on rebuilds)
COPY src src

ENV GRADLE_OPTS="-Dorg.gradle.daemon=false -Dorg.gradle.vfs.watch=false -Xmx512m"
RUN ./gradlew bootJar -x test --no-daemon --stacktrace \
	&& cp build/libs/demo-0.0.1-SNAPSHOT.jar /app/app.jar

# Run
FROM eclipse-temurin:23-jre-noble
WORKDIR /app

COPY --from=build /app/app.jar app.jar

# Cap heap/metaspace/stack so RSS fits a 512MB Render instance.
# Override JAVA_OPTS in the Render dashboard if you bump RAM.
ENV JAVA_OPTS="-XX:MaxRAMPercentage=40 -XX:MaxMetaspaceSize=128m -Xss512k -XX:+UseSerialGC -XX:+ExitOnOutOfMemoryError"

EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar --server.port=${PORT:-8080}"]
