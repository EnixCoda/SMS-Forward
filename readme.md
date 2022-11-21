# SMS Forward

Sometimes we have more phone numbers than SIM card slots in the phone we use most. You do not want to miss any call and text message so you carry multiple phones everyday, what a pain!

Calls can be forwarded to a single phone thanks to carriers' call forwarding service. What about SMS messages? This app can handle them. It forwards them between an Android phone and a target phone.

## Usages
1. Receive messages
![image](https://user-images.githubusercontent.com/7480839/154650144-1e0bdf7b-a42c-48d3-a84d-46b614496dfb.png)
    When a message is received, it will be redirect to target phone. Message body will have `from {source number}:` (and a line break) prepended, message content starts at the second line.

2. Send messages
![image](https://user-images.githubusercontent.com/7480839/154650135-4f7f2274-4397-4077-bc70-b423ed195271.png)
    You can even control the Android phone to send messages. Simply send message to the Android phone, with `to {receive number}:` (and a line break) prepended in message body, message content starting from the second line will be forwarded.

## Setup
1. Pick up the Android phone, install this app with the `.apk` file downloaded from the [releases page](https://github.com/EnixCoda/SMS-Forward/releases). You can build the app from source to ensure safety.
2. Launch the app, there will be a prompt requesting for SMS receive and send permissions. The app won't work without them, please grant.
3. Enter the target phone number.
4. Put down the Android phone and keep it charged for continuous service.

## Note
This app has only been tested on Android 7.1.1, but it should work for Android 5.0+ devices.

This project is the minimal implementation of a text message forwarding app. There are some app in app markets offering similiar functionalities, but are too large.

## License

MIT
