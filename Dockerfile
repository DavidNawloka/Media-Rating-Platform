FROM maven:3-eclipse-temurin-21
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn compile dependency:copy-dependencies -DoutputDirectory=target/dependency -B

EXPOSE 8080
CMD ["java", "-cp", "target/classes:target/dependency/*", "at.fhtw.swen1.Main"]