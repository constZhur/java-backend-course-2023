FROM openjdk:21
WORKDIR app/bot
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} bot.jar
ENTRYPOINT ["java", "-jar", "bot.jar"]
