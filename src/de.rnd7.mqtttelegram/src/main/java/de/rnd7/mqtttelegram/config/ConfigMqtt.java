package de.rnd7.mqtttelegram.config;

import com.google.gson.annotations.SerializedName;

import java.util.Optional;

public class ConfigMqtt {
    private String url;
    private String username;
    private String password;
    private String topic = "telegram";
    private boolean retain = true;

    @SerializedName("client-id")
    private String clientId;

    public String getUrl() {
        return url;
    }

    public Optional<String> getUsername() {
        return Optional.ofNullable(username);
    }

    public Optional<String> getPassword() {
        return Optional.ofNullable(password);
    }

    public Optional<String> getClientId() {
        return Optional.ofNullable(clientId);
    }

    public boolean isRetain() {
        return retain;
    }

    public String getTopic() {
        return topic;
    }
}
