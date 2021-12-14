# ---- Prod ----
FROM amazoncorretto:8-alpine3.12-jre
RUN mkdir /opt/app
WORKDIR /opt/app
COPY src/de.rnd7.mqtttelegram/target/mqtttelegram.jar .
COPY src/logback.xml .

CMD java -jar ./mqtttelegram.jar /var/lib/mqtttelegram/config.json
