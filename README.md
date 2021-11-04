# Totem Magic Slot Game
<p align="center">
<img src="https://user-images.githubusercontent.com/85931447/140210362-1b5152a7-2a73-4a32-b14e-7d517f26b23c.jpg" alt="Totem Magic">
</p>

## Description
This project is a simple realization of "casino slot" game. User can bet some coins and win.<br/>
After every win user will see interstitial advertisement.

## Features
- Background music
- Sounds for: bet, spin and winning events
- Ability to change the sum of the bet
- App installs tracked with Appsflyer android SDK and Facebook android SDK
- OneSignal android SDK lets us sign users up for push notifications
- Push notifications can be sent from OneSignal dashboard or API

## Technologies & Tools
- Java 8
- Gradle 4.1.3
- Facebook android SDK 11.1.1
- Appsflyer android SDK 6.2.0
- OneSignal android SDK 4.6.1
- Android GIF drawable 1.2.22
- Android studio

## Usage

1. Fork and clone this project.
2. Add `OneSignal App ID` and `Appsflyer Dev key` to `key.properties` file in the root directory.
      ```properties
        ONESIGNAL_DEV_KEY="PUT_ONESIGNAL_KEY"
        APPFLYER_DEV_KEY="PUT_APPSFLYER_KEY"
      ```
3. Build app.
4. Or install <a target="_blank" href="https://drive.google.com/file/d/1boQoxMDpiFqb4nFW9c8gG0ZSmDLhC2nr/view?usp=sharing">APK</a> file.
