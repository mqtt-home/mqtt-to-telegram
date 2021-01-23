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

        final GwMqttClient client = GwMqttClient.start(config.getMqtt()
            .setDefaultClientId("mqtt-telegram")
            .setDefaultTopic("telegram"));

        client.subscribe(config.getMqtt().getTopic() + "/#");
        client.online();

        Events.register(new BotService(config.getTelegram(), config.getMqtt().getTopic())
            .start());
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
