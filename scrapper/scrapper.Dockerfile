FROM openjdk:21 as builder
WORKDIR app/scrapper
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} scrapper.jar
RUN java -Djarmode=layertools -jar scrapper.jar extract

FROM openjdk:21
WORKDIR app/bot
COPY --from=builder app/scrapper/dependencies/ ./
COPY --from=builder app/scrapper/spring-boot-loader/ ./
COPY --from=builder app/scrapper/snapshot-dependencies/ ./
COPY --from=builder app/scrapper/application/ ./
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]
