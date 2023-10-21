FROM openjdk:17-slim
EXPOSE 8080

ARG APP_HOME=/app

WORKDIR $APP_HOME

COPY build/libs/telegram-bot-*-SNAPSHOT.jar telegram-app.jar

CMD ["java","-Dspring.profiles.active=docker", "-jar", "telegram-app.jar"]
