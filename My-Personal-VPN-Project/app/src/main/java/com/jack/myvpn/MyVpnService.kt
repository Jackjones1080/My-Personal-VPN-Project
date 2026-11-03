package com.jack.myvpn

import android.net.VpnService

/**
 * Declaration scaffold only. A real tunnel must configure a TUN interface,
 * run a vetted VPN engine, handle lifecycle/foreground notification, and
 * close descriptors safely. Do not report connected until traffic is routed.
 */
class MyVpnService : VpnService()
