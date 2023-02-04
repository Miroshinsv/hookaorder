FROM springboot-docker-compose-app:latest

FROM gradle:7.5.1-jdk17-alpine AS build

# Тут перечисляем аргументы, которые передаем через --build-args
ARG CLIENT_EMAIL
ARG CLIENT_ID
ARG PRIVATE_KEY
ARG PRIVATE_KEY_ID
ARG PROJECT_ID

ENV CLIENT_EMAIL=$CLIENT_EMAIL
ENV CLIENT_ID=$CLIENT_ID
ENV PRIVATE_KEY=$PRIVATE_KEY
ENV PRIVATE_KEY_ID=$TEST_ARG
ENV PROJECT_ID=$PROJECT_ID


RUN echo $TEST_VAR

COPY --chown=gradle:gradle . /home/gradle/src

WORKDIR /home/gradle/src

RUN ./gradlew build

RUN cp service-account.json main/resources/service-account.json

RUN ./gradlew build

FROM openjdk:17-alpine

EXPOSE 8080

RUN mkdir /app

COPY --from=build /home/gradle/src/build/* /app/

WORKDIR /app

ENTRYPOINT ["java", "-Dserver.port=8080", "-jar", "/app/backend-0.0.1-SNAPSHOT-boot.jar"]
