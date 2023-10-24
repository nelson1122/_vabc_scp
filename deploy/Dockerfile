FROM eclipse-temurin:17-jre-alpine
ENV TZ="America/Bogota"
WORKDIR /usr/local/
COPY vabc_scp /usr/local/vabc_scp
WORKDIR /usr/local/vabc_scp

CMD ["java", "main/java/Main"]