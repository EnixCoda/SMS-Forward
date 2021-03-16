# SMS Forward

This app can help your Android phone to forward text messages to & from other phones. To use one number to perform the functionality of two numbers, we need to forward calls and messages from one to another. Forwarding calls to the phone in your pocket from another is pretty easy as carriers offering call forward services.

This project could be the minimal implementation of a text message forwarding app. There are some app in app markets offering similiar functionalities, but are too large, as all I need is just forwarding messages.

## How to use

### Setup
1. Pick up one Android phone (let's name it phone A), install this app with the `.apk` file downloaded from the [releases page](https://github.com/EnixCoda/SMS-Forward/releases). You can also build this app on your own to ensure safety.
2. Launch the app, there will be a prompt requesting for SMS receive and send permissions. The app won't be able to work without these two, please grant.
3. Enter the target phone number, which is the number of the phone to receive forwarded text messages.
4. Put down the phone A and make it charged for continuous service.

### Sending & replying SMS
Let's pick up the phone you daily use now (name it phone B), and here is how this app works:

When phone A receives a message, it will be forwarded to the target number you've set in the step 3 above. The forwarded message will be in the format like:
```
from 10000:
this is the content of the message.
```

This app also supports replying messages directly from phone B! It will try to match the message content with this format:
```
to 10000:
replying the message.
```

If matched, the message's body content, will be forwarded to the specified number, i.e. `replying the message.` will be sent to `10000` in the above example. In this case, you can use phone B to send/reply messages as if you are using phone A directly! 🎉

## Note
This app has only been tested on Android 7.1.1, but it should work for Android 5.0+ devices.
