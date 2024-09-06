#GENERATE BUILD
FROM maven:3.9.7-amazoncorretto-21-debian-bookworm as build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

#DOCKERIZE
FROM eclipse-temurin:21-jre-alpine
LABEL authors="Loïc_Dubrulle"
WORKDIR /app
COPY  --from=build /app/target/PayMyBuddy-0.0.1-SNAPSHOT.jar ./pay-my-buddy.jar
RUN echo ".jar pay-my-buddy copied"
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "pay-my-buddy.jar", "--spring.profiles.active=docker"]