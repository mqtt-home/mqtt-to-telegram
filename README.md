# mqtt-to-telegram
[![mqtt-smarthome](https://img.shields.io/badge/mqtt-smarthome-blue.svg)](https://github.com/mqtt-smarthome/mqtt-smarthome)

This app provides a bridge between telegram and mqtt.
You can post messages to a chat channel by publish a message to mqtt topic.

## Example: 
1) Publish the mqtt message `hello` to `telegram/displayname/set`
   and the user with the given chat-id will be informed.

2) Send a message using telegram, and you will get this message as mqtt message
   with the topic `telegram/displayname`

# How to

You have to create a chat-bot first. Use the telegram `BotFather` to do this.
You just need to know the telegram token for your bot to use this app.

# Config

```json
{
  "mqtt": {
    "url": "tcp://192.168.2.98:1883",
    "topic": "telegram",
    "username": "user",
    "password": "password"
  },
  "telegram": {
    "token": "telegram-chat-bot-token",
    "start-code": "start-code-chosen-by-yourself",
    "chat-ids": {
      "displayname1": <chat-id-1>,
      "displayname2": 123456789
    }
  }
}
```

# Docker

```yaml
  mqttrules:
    image: pharndt/mqtttelegram:1.0.1
    volumes:
     - ./config/mqtttelegram:/var/lib/mqtttelegram:ro
    environment:
      TZ: "Europe/Berlin"
    restart: always 
    depends_on:
     - mosquitto
 ```

