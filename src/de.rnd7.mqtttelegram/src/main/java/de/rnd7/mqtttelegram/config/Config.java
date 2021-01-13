package de.rnd7.mqtttelegram.config;

import de.rnd7.mqtttelegram.telegram.ConfigTelegram;

public class Config {

	private ConfigMqtt mqtt;
	private ConfigTelegram telegram;

	public ConfigMqtt getMqtt() {
		return mqtt;
	}

	public ConfigTelegram getTelegram() {
		return telegram;
	}
}
