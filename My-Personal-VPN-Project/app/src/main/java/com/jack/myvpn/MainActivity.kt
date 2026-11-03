package com.jack.myvpn

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import android.util.Base64

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MaterialTheme { MyVpnApp() } }
    }
}

@Composable
fun MyVpnApp() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val prefs = remember { context.getSharedPreferences("vpn_settings", android.content.Context.MODE_PRIVATE) }
    var selectedTab by remember { mutableStateOf(0) }
    var selectedEngine by remember { mutableStateOf("Xray / V2Ray") }
    var subscriptionUrl by remember { mutableStateOf(prefs.getString("subscription_url", "") ?: "") }
    var status by remember { mutableStateOf("هنوز اشتراکی دریافت نشده است.") }
    var configCount by remember { mutableIntStateOf(prefs.getStringSet("configs", emptySet())?.size ?: 0) }
    var loading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val engines = listOf("Xray / V2Ray", "WireGuard", "Cloudflare WARP", "Amnezia", "Psiphon", "Tor")

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(selectedTab == 0, { selectedTab = 0 }, icon = { Icon(Icons.Default.Shield, null) }, label = { Text("اتصال") })
                NavigationBarItem(selectedTab == 1, { selectedTab = 1 }, icon = { Icon(Icons.Default.Settings, null) }, label = { Text("تنظیمات") })
            }
        }
    ) { padding ->
        if (selectedTab == 0) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Text("مای وی‌پی‌ان", fontSize = 28.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Text("اتصال امن شخصی", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(28.dp))
                    Surface(shape = MaterialTheme.shapes.extraLarge, tonalElevation = 3.dp) {
                        Column(Modifier.fillMaxWidth().padding(22.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Shield, null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.height(12.dp))
                            Text("هنوز متصل نیستید", fontWeight = FontWeight.SemiBold)
                            Text("موتور انتخابی: $selectedEngine", fontSize = 13.sp)
                            Text("کانفیگ‌های دریافت‌شده: $configCount", fontSize = 13.sp)
                            Spacer(Modifier.height(18.dp))
                            Button(onClick = { }) { Text("اتصال — موتور در حال توسعه است") }
                        }
                    }
                    Spacer(Modifier.height(24.dp))
                    Text("موتورهای اتصال", fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(8.dp))
                }
                items(engines) { engine ->
                    Card(
                        onClick = { selectedEngine = engine },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (selectedEngine == engine) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Public, null)
                            Spacer(Modifier.width(12.dp))
                            Text(engine, modifier = Modifier.weight(1f))
                            if (selectedEngine == engine) Icon(Icons.Default.CheckCircle, null)
                        }
                    }
                }
                item {
                    Spacer(Modifier.height(12.dp))
                    Text("برای اتصال واقعی، موتور VPN هنوز باید به برنامه اضافه و آزمایش شود.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            Column(Modifier.fillMaxSize().padding(padding).padding(20.dp)) {
                Text("اشتراک V2Ray / Xray", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(12.dp))
                Text("لینک اشتراک را اینجا وارد کن. لینک فقط روی همین گوشی ذخیره می‌شود و داخل مخزن GitHub قرار نمی‌گیرد.")
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = subscriptionUrl,
                    onValueChange = { subscriptionUrl = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("لینک اشتراک") },
                    placeholder = { Text("https://.../sub") },
                    singleLine = true
                )
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = {
                        prefs.edit().putString("subscription_url", subscriptionUrl.trim()).apply()
                        status = "لینک روی گوشی ذخیره شد."
                    },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("ذخیره لینک") }
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = {
                        val link = subscriptionUrl.trim()
                        if (!link.startsWith("https://") && !link.startsWith("http://")) {
                            status = "لطفاً یک لینک معتبر با http یا https وارد کن."
                        } else {
                            loading = true
                            status = "در حال دریافت اشتراک..."
                            prefs.edit().putString("subscription_url", link).apply()
                            scope.launch {
                                try {
                                    val configs = withContext(Dispatchers.IO) { downloadSubscription(link) }
                                    prefs.edit().putStringSet("configs", configs.toSet()).apply()
                                    configCount = configs.size
                                    status = if (configs.isEmpty()) "فایل دریافت شد، اما کانفیگ قابل‌شناسایی پیدا نشد." else "$configCount کانفیگ دریافت و روی گوشی ذخیره شد."
                                } catch (e: Exception) {
                                    status = "دریافت ناموفق بود: ${e.message ?: "خطای شبکه"}"
                                } finally {
                                    loading = false
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !loading
                ) {
                    if (loading) CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                    else Text("دریافت و به‌روزرسانی اشتراک")
                }
                Spacer(Modifier.height(14.dp))
                Text(status, color = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.height(8.dp))
                Text("تعداد کانفیگ‌های ذخیره‌شده: $configCount")
                Spacer(Modifier.height(20.dp))
                Text("توجه: این مرحله فقط دریافت و ذخیره کانفیگ‌ها را انجام می‌دهد؛ اتصال VPN تا زمان اضافه‌شدن موتور Xray فعال نیست.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(16.dp))
                Text("نسخه 0.2.0", fontSize = 12.sp)
            }
        }
    }
}

private fun downloadSubscription(link: String): List<String> {
    val connection = (URL(link).openConnection() as HttpURLConnection).apply {
        requestMethod = "GET"
        connectTimeout = 20000
        readTimeout = 30000
        setRequestProperty("User-Agent", "MyPersonalVPN/0.2")
    }
    try {
        if (connection.responseCode !in 200..299) {
            throw IllegalStateException("HTTP ${connection.responseCode}")
        }
        val body = connection.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }.trim()
        val decoded = if (body.contains("://")) body else {
            try { String(Base64.decode(body, Base64.DEFAULT), Charsets.UTF_8) } catch (_: Exception) { body }
        }
        return decoded.lineSequence()
            .map { it.trim() }
            .filter { it.startsWith("vless://") || it.startsWith("vmess://") || it.startsWith("trojan://") || it.startsWith("ss://") }
            .distinct()
            .toList()
    } finally {
        connection.disconnect()
    }
}
