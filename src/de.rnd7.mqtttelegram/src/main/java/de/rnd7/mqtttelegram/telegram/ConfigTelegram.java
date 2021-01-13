package de.rnd7.mqtttelegram.telegram;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

public class ConfigTelegram {
    @SerializedName("token")
    private String token;

    @SerializedName("start-code")
    private String code;

    @SerializedName("chat-ids")
    private Map<String, Long> chatIds = new HashMap<>();

    public String getToken() {
        return token;
    }

    public ConfigTelegram setToken(final String token) {
        this.token = token;
        return this;
    }

    public Map<String, Long> getChatIds() {
        return chatIds;
    }

    public String getCode() {
        return code;
    }
}
