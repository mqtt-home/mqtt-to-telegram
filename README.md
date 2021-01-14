# mqtt-to-telegram
[![mqtt-smarthome](https://img.shields.io/badge/mqtt-smarthome-blue.svg)](https://github.com/mqtt-smarthome/mqtt-smarthome)

This app provides a bridge between Telegram and MQTT.
You can post messages to a chat channel by publishing a message on an MQTT topic.

# Why?

With this project, it is possible to publish messages like `your dishwasher has just finished!`
or request the lights that are turned on through Telegram. You will need to use
separate tools to create such messages or react to a message you get from Telegram.
This tool will convert the messages for you. It follows the principles of `mqtt-smarthome`.

## Example:
1) Publish the MQTT message `hello` to `telegram/displayname/set`
   and the user with the given chat-id will be informed.

2) Send a message using Telegram, and you will get this message as MQTT message on the topic `telegram/displayname`.

3) Publish the MQTT message `hello` to `telegram/*/set`
   and all known users (chat ids) will get the message.

# How to

You have to create a chat-bot first. Use the telegram `BotFather` to do this.
You just need to know the telegram token for your bot to use this app.

Open the chat-bot using Telegram and execute `/start`.
The bot will ask you for your `start-code`. You will get the chat-id as a result.
The `chat-id` can be inserted into your configuration. At the moment, you have to
do this yourself. When it turns out that more users, and not just me, use this, we can add this feature.

# Config

```json
{
   "mqtt": {
      "url": "tcp://192.168.2.98:1883",
      "client-id": "mqtt-telegram",
      "topic": "telegram",
      "username": "user",
      "password": "password"
   },
   "telegram": {
      "token": "telegram-chat-bot-token",
      "start-code": "start-code-chosen-by-yourself",
      "chat-ids": {
         "displayname1": 456,
         "displayname2": 123456789
      }
   }
}
```

# Docker

```yaml
  mqtttelegram:
     image: pharndt/mqtttelegram:1.0.4
     volumes:
        - ./config/mqtttelegram:/var/lib/mqtttelegram:ro
     environment:
        TZ: "Europe/Berlin"
     restart: always
     depends_on:
        - mosquitto
 ```
