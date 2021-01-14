package de.rnd7.mqtttelegram.telegram;

import com.google.common.eventbus.Subscribe;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import de.rnd7.mqtttelegram.Events;
import de.rnd7.mqtttelegram.mqtt.Message;
import de.rnd7.mqtttelegram.mqtt.PublishMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BotService {
    private static final Logger LOGGER = LoggerFactory.getLogger(BotService.class);

    private final String token;
    private TelegramBot bot;
    private final Map<String, Long> chatIdMap;
    private final Map<String, Long> chatIdByTopic;
    private final Map<Long, String> topicByChatId;
    private final Set<Long> chatIds;
    private final String code;
    private final String broadcastTopic;

    public BotService(final ConfigTelegram config, String topic) {
        this.token = config.getToken();
        this.chatIdMap = config.getChatIds();
        this.chatIds = new HashSet<>(config.getChatIds().values());
        this.code = config.getCode();

        this.chatIdByTopic = new HashMap<>();
        this.topicByChatId = new HashMap<>();
        this.broadcastTopic = topic + "/*/set";

        config.getChatIds().forEach((name, id) -> {
            chatIdByTopic.put(topic + "/" + name + "/set", id);
            chatIdByTopic.put(topic + "/" + id + "/set", id);
            topicByChatId.put(id, topic+ "/" + name);
        });
    }

    public BotService start() {
        bot = new TelegramBot(token);
        // Register for updates
        bot.setUpdatesListener(updates -> {
            updates.forEach(update -> {
                final Long chatId = update.message().chat().id();
                if (chatIds.contains(chatId)) {
                    final String topic = topicByChatId.get(chatId);
                    if (topic != null) {
                        Events.getBus().post(new PublishMessage(topic, update.message().text()));
                        final SendResponse response = bot.execute(
                                new SendMessage(chatId, "ok")
                        );
                        LOGGER.info(response.toString());
                    }
                    else {
                        final SendResponse response = bot.execute(
                                new SendMessage(chatId, "no topic found for the current chat id.")
                        );
                        LOGGER.info(response.toString());
                    }
                }
                else {
                    LOGGER.info("New chat with id: {}", chatId);

                    if (update.message().text().equals(code)) {
                        final SendResponse response = bot.execute(
                                new SendMessage(chatId, "Ok! You chat id is:\n" + chatId)
                        );
                        LOGGER.info(response.toString());
                    }
                    else {
                        final SendResponse response = bot.execute(
                                new SendMessage(chatId, "Hi! Please enter your start-code.")
                        );
                        LOGGER.info(response.toString());
                    }
                }
            });

            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });

        return this;
    }

    @Subscribe
    public void onMessage(Message message) {
        if (message.getTopic().equals(this.broadcastTopic)) {
            sendBroadcastMessage(message);
        }
        else {
            sendMessage(message);
        }
    }

    private void sendMessage(final Message message) {
        final Long chatId = this.chatIdByTopic.get(message.getTopic());
        if (chatId != null) {
            final SendResponse response = bot.execute(new SendMessage(chatId, message.getRaw()));
            LOGGER.info(response.toString());
        }
    }

    private void sendBroadcastMessage(final Message message) {
        this.chatIds.forEach(chatId -> {
            final SendResponse response = bot.execute(new SendMessage(chatId, message.getRaw()));
            LOGGER.info(response.toString());
        });
    }
}
