# ---- Stage 1: Build ----
# Use an official Maven image with Java 21 to compile the project
# This image has both Maven and JDK installed
FROM maven:3.9-eclipse-temurin-21 AS builder

# Set the working directory inside the container
WORKDIR /app

# Copy pom.xml first and download dependencies
# This is a Docker caching trick — if pom.xml hasn't changed,
# Docker reuses the cached dependency layer and skips re-downloading
# Only re-downloads when pom.xml actually changes
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Now copy the source code
COPY src ./src

# Build the jar, skip tests (tests need a running DB which we don't have here)
# -B means batch mode — no interactive prompts
RUN mvn package -DskipTests -B

# ---- Stage 2: Run ----
# Use a lightweight JRE-only image — no Maven, no JDK, just the runtime
# Much smaller than the builder image (~200MB vs ~600MB)
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy only the built jar from Stage 1 — nothing else
# The *.jar wildcard picks up whatever version jar Maven produced
COPY --from=builder /app/target/*.jar app.jar

# Tell Docker this container listens on port 8080
# This is documentation — doesn't actually open the port
EXPOSE 8080

# The command that runs when the container starts
# -jar app.jar tells Java to run our Spring Boot jar
ENTRYPOINT ["java", "-jar", "app.jar"]