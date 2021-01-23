package de.rnd7.mqtttelegram.telegram;

import com.google.common.eventbus.Subscribe;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import de.rnd7.mqttgateway.Events;
import de.rnd7.mqttgateway.Message;
import de.rnd7.mqttgateway.PublishMessage;
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
    private final Map<String, Long> chatIdByTopic;
    private final Map<Long, String> topicByChatId;
    private final Set<Long> chatIds;
    private final String code;
    private final String broadcastTopic;

    public BotService(final ConfigTelegram config, final String topic) {
        this.token = config.getToken();
        this.chatIds = new HashSet<>(config.getChatIds().values());
        this.code = config.getCode();

        this.chatIdByTopic = new HashMap<>();
        this.topicByChatId = new HashMap<>();
        this.broadcastTopic = topic + "/*/set";

        config.getChatIds().forEach((name, id) -> {
            chatIdByTopic.put(topic + "/" + name + "/set", id);
            chatIdByTopic.put(topic + "/" + id + "/set", id);
            topicByChatId.put(id, topic + "/" + name);
        });
    }

    public BotService start() {
        bot = new TelegramBot(token);
        bot.setUpdatesListener(updates -> {
            updates.forEach(this::handleUpdate);

            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });

        return this;
    }

    private void handleUpdate(final Update update) {
        final Long chatId = update.message().chat().id();
        if (chatIds.contains(chatId)) {
            final String topic = topicByChatId.get(chatId);
            if (topic != null) {
                Events.post(PublishMessage.absolute(topic, update.message().text()));
                sendTelegram(chatId, "ok");
            } else {
                sendTelegram(chatId, "no topic found for the current chat id.");
            }
        } else {
            LOGGER.info("New chat with id: {}", chatId);

            if (update.message().text().equals(code)) {
                sendTelegram(chatId, "Ok! You chat id is:\n" + chatId);
            } else {
                sendTelegram(chatId, "Hi! Please enter your start-code.");
            }
        }
    }

    private void sendTelegram(final long chatId, final String message) {
        final SendResponse response = bot.execute(
            new SendMessage(chatId, message)
        );

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(response.toString());
        }
    }

    @Subscribe
    public void onMessage(final Message message) {
        if (message.getTopic().equals(this.broadcastTopic)) {
            sendBroadcastMessage(message);
        } else {
            sendMessage(message);
        }
    }

    private void sendMessage(final Message message) {
        final Long chatId = this.chatIdByTopic.get(message.getTopic());
        if (chatId != null) {
            sendTelegram(chatId, message.getRaw());
        }
    }

    private void sendBroadcastMessage(final Message message) {
        this.chatIds.forEach(chatId -> {
            sendTelegram(chatId, message.getRaw());
        });
    }
}
