<h1 align="center">Light Mediator</h1>
<h2 align="center">Ambient Intelligence</h2>
<h3 align="center">Instituto Superior Técnico, Universidade de Lisboa</h3>
<h4 align="center">2021/2022</h4>

<br>

## Authors:
- [Duarte Bento](https://github.com/DuBento)
- [Susana Monteiro](https://github.com/susmonteiro)

## Demo:


https://github.com/user-attachments/assets/867392cd-acfa-4bd8-80de-1fc14692997d



## Required Platform
- Smartphone with Android (it was tested in Android 12 and therefore we cannot guarantee that it works in other versions of Android). Permission to use the microphone of the device is also required
- In order to integrate with the light (the connection to the light is not mandatory), we also need access to the bluetooth of the device. 
- To flash the program to our microcontroller of choice we require Arduino IDE
- We require the built microcontroller assembly, which is based on a microcontroller with 5 digital pins (we used `arduino nano` for development), a bluetooth serial module (HC-06) and RGB LED
## Setup Instructions
### Android App:
1. Install Android Studio
2. Activate Developer Mode in your Android device and allow USB Debugging 
3. Connect the device to Android Studio and "run the app" on the connected device
4. After that the app is installed. You can either use it that way, or you can disconnect the device and use the app normally
5. When starting the app, there is a button with the text `CONNECT LIGHT`. This connects the device to the light. It is not mandatory to connect to the light.

> :warning: **Testing mobile application**: the mobile application is experimental, and is therefore using a set of payed keys. We ask that you don't use it during too much time.

### Microcontroller:

1. Install Arduino IDE
2. Select Board type and port
3. Compile and upload the code
4. Open `Tools > Serial monitor` to check incoming messages

> :warning: **Testing microcontroller code**: requires access to the microcontroller assembly
