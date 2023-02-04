FROM springboot-docker-compose-app:latest

FROM gradle:7.5.1-jdk17-alpine AS build

# Тут перечисляем аргументы, которые передаем через --build-args
ARG TEST_ARG

# Тут переприсваиваем аргументы переменным окружения
ENV TEST_VAR=$TEST_ARG

RUN echo $TEST_VAR

COPY --chown=gradle:gradle . /home/gradle/src

WORKDIR /home/gradle/src

RUN ./gradlew build

FROM openjdk:17-alpine

EXPOSE 8080

RUN mkdir /app

COPY --from=build /home/gradle/src/build/* /app/

WORKDIR /app

ENTRYPOINT ["java", "-Dserver.port=8080", "-jar", "/app/backend-0.0.1-SNAPSHOT-boot.jar"]
