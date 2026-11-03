# My Personal VPN

Android starter project inspired by the feature set of MLM VPN.
This is an initial UI and architecture scaffold, **not yet a working VPN client**.
No VPN tunnel or protocol engine is bundled at this stage.

## Included
- Persian RTL Compose interface
- Connection screen and engine selection placeholders
- Settings screen placeholder
- Android `VpnService` declaration (service implementation is intentionally not connected yet)

## Build
Open this folder in Android Studio (Giraffe or newer), allow Gradle sync, then run on an Android device.
Use JDK 17 and Android SDK 35.

## Next development stages
1. Implement and test the VPN service lifecycle and permission flow.
2. Integrate one legally distributable engine (start with sing-box or Xray).
3. Add secure configuration import and local storage.
4. Add diagnostics, DNS controls, per-app routing, and additional engines only after testing.

Do not put private keys, subscription URLs, or credentials in Git.
