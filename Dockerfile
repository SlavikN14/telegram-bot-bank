FROM openjdk:17-slim
EXPOSE 8080
ENV APP_HOME=/app
RUN mkdir $APP_HOME
COPY /build/libs/telegram-bot-*.jar $APP_HOME/app.jar
RUN mkdir $APP_HOME/resources
COPY /build/resources/main/texts/texts.properties $APP_HOME/resources/texts.properties
WORKDIR $APP_HOME
ENTRYPOINT ["java","-Dspring.profiles.active=docker", "-jar", "./app.jar"]
