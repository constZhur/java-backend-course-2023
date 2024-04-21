FROM openjdk:21 as builder
WORKDIR app/bot
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} bot.jar
RUN java -Djarmode=layertools -jar bot.jar extract

FROM openjdk:21
WORKDIR app/bot
COPY --from=builder app/bot/dependencies/ ./
COPY --from=builder app/bot/spring-boot-loader/ ./
COPY --from=builder app/bot/snapshot-dependencies/ ./
COPY --from=builder app/bot/application/ ./
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]
