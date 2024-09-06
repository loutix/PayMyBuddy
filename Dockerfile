#DOCKERIZE
FROM eclipse-temurin:21-jre-alpine
LABEL authors="Loïc_Dubrulle"
WORKDIR /app
COPY  target/PayMyBuddy-0.0.1-SNAPSHOT.jar pay-my-buddy.jar
RUN echo ".jar pay-my-buddy copied"
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "pay-my-buddy.jar", "--spring.profiles.active=docker"]