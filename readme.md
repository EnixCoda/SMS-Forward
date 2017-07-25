# SMS Forward

This is a Android Studio Project for a simple SMS forward app.

## Who need it
For the ones like me having more than one phone number but want to carry only one single-SIM-slot device in pocket, this app helps. To use one number to perform the functionality of two numbers, we need to forward calls and messages from one to another. Forwarding calls to the phone in your pocket from another is pretty easy as carriers offering call forward services.

But when it comes to SMS, things get hard.

There are some app in the app markets offering similiar functionalities, but some are too large (I just need to forward SMS, nothing more), while some others looks unsafe. So within less than 100 lines of codes' work, I built this simple, safe, tiny but satisfying my needs app on my own and share it with you.

## How to use

### setup
1. pick one Android phone (let's call it phone A), install this app with the `.apk` file downloaded from [releases page](https://github.com/EnixCoda/SMS-Forward/releases) (you can also build this app on your own).
2. start the app, there will be a prompt requesting for SMS receive and send permissions(the app won't be able to work without these two, please grant).
3. enter the target phone number, i.e. the number of the phone to receive forwarded SMS.
4. You can close the app and throw the Android phone away now (but don't shut it down).

### send & reply SMS
Let's pick up the phone you daily use now (call it phone B), and here is how this app works:

When phone A receives a message, and if the message is from a normal number, it will be forwarded to the target number you set in above step 3. The forwarded message will be in format like:
```
from 10000:
this is the content of the message.
```

If the message is from the target number, this app will try to match the content with this format:
```
to 10000:
replying the message.
```

If matching succeeds, the message's body content (i.e. `replying the message.` in above example) will be forwarded to the specified number (i.e. `10000` in above example). In this case, you can use your target phone to send/reply messages to any other number, and the phone B even do not need any special settings! 🎉

If matching fails, the message won't be forwarded to anywhere.

## Note
This app is only tested on Android 7.1.1, it should work for Android 5.0+ devices and might not work on some old versions like Android 4.3.