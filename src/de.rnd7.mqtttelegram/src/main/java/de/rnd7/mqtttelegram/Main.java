package de.rnd7.mqtttelegram;

import de.rnd7.mqttgateway.Events;
import de.rnd7.mqttgateway.GwMqttClient;
import de.rnd7.mqttgateway.config.ConfigParser;
import de.rnd7.mqtttelegram.config.Config;
import de.rnd7.mqtttelegram.telegram.BotService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public Main(final Config config) {
        LOGGER.debug("Debug enabled");
        LOGGER.info("Info enabled");
        try {
            GwMqttClient.start(config.getMqtt()
                .setDefaultTopic("telegram")
            )
            .online()
            .subscribe(config.getMqtt().getTopic() + "/#");

            Events.register(new BotService(config.getTelegram(), config.getMqtt().getTopic())
                .start());
        } catch (final Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public static void main(final String[] args) {
        if (args.length != 1) {
            LOGGER.error("Expected configuration file as argument");
            return;
        }

        try {
            new Main(ConfigParser.parse(new File(args[0]), Config.class));
        } catch (final IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
