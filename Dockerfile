FROM openjdk:17-slim
EXPOSE 8080

ARG APP_HOME=/app

WORKDIR $APP_HOME

COPY build/libs/telegram-bot-*.jar ./app.jar

ENTRYPOINT ["java","-Dspring.profiles.active=docker", "-jar", "./app.jar"]
