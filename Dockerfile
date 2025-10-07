FROM openjdk:21-jdk-slim

# Install Maven
RUN apt-get update && \
  apt-get install -y maven && \
  apt-get clean && \
  rm -rf /var/lib/apt/lists/*

# Set working directory
WORKDIR /app

# Copy Maven files first (for better caching)
COPY pom.xml .

# Download dependencies INCLUDING runtime dependencies
RUN mvn dependency:go-offline -B
RUN mvn dependency:copy-dependencies -DoutputDirectory=target/dependency

# Copy source code
COPY src ./src

# Compile the application
RUN mvn compile

# Expose application port
EXPOSE 8080

# Run with proper classpath including all dependencies
CMD ["java", "-cp", "target/classes:target/dependency/*", "at.fhtw.swen1.Main"]