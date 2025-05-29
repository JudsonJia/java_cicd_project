FROM openjdk:8-jdk-slim as builder

WORKDIR /app

# Copy pom.xml first
COPY pom.xml .

# Copy source code
COPY src src

# Build with system Maven
RUN apt-get update && apt-get install -y maven && \
    mvn dependency:go-offline -B && \
    mvn clean package -DskipTests -B

# Production stage
FROM openjdk:8-jre-slim

RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

RUN useradd --create-home --shell /bin/bash app

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

RUN chown -R app:app /app
USER app

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "/app/app.jar"]