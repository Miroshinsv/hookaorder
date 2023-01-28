FROM gradle:7.5.1-jdk17-alpine AS build

ARG CLIENT_EMAIL
ARG CLIENT_ID
ARG PRIVATE_KEY
ARG PRIVATE_KEY_ID
ARG PROJECT_ID

COPY --chown=gradle:gradle . /home/gradle/src

WORKDIR /home/gradle/src

RUN ./gradlew clean build

FROM openjdk:17-alpine

EXPOSE 8080

RUN mkdir /app

COPY --from=build /home/gradle/src/build/* /app/

WORKDIR /app

ENTRYPOINT ["java", "-Dserver.port=8080", "-jar", "/app/backend-0.0.1-SNAPSHOT-boot.jar"]