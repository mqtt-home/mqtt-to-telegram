# ---- Prod ----
FROM openjdk:17-jdk-alpine
RUN mkdir /opt/app
WORKDIR /opt/app
COPY src/de.rnd7.mqtttelegram/target/mqtttelegram.jar .
COPY src/logback.xml .

CMD java -jar ./mqtttelegram.jar /var/lib/mqtttelegram/config.json
