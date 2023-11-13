FROM eclipse-temurin:17-jre-alpine
ENV TZ="America/Bogota"
WORKDIR /usr/local/
COPY vabcscp /usr/local/vabcscp
WORKDIR /usr/local/vabc_scp

CMD ["java", "main/java/Main"]