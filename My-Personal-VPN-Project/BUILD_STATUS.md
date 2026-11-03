# My Personal VPN

A personal Android VPN client project, with a Persian RTL interface.

## Current status

This repository currently contains an early Jetpack Compose UI scaffold and a GitHub Actions APK build workflow. It is **not yet a functional VPN client**: no tunnel engine, Xray core, server configuration importer, or tested VPN connection is included yet.

## Build APK

A GitHub Actions workflow is configured at `.github/workflows/android.yml`. Push a change to this project or manually run **Build Android APK** from the Actions tab. When the build succeeds, download the `MyPersonalVPN-debug` artifact from that workflow run.

## Planned implementation

1. Add secure import and local storage for VLESS, VMess, Trojan and Shadowsocks configurations/subscriptions.
2. Integrate a maintained Android-compatible Xray core and implement the VPN service lifecycle.
3. Add connection diagnostics, DNS and per-app routing.
4. Test on a physical Android device before describing the app as working.

## Important

A VPN client needs a reachable VPN endpoint or subscription. Cloudflare Workers alone are not a general-purpose VPN server.

This project is independent and is not affiliated with MLM VPN. If code from MLM VPN is reused, preserve its GPLv3 license and required attribution to mlmvpn in the app and source distribution.
