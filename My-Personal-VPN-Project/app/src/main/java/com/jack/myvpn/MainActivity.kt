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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MaterialTheme { MyVpnApp() } }
    }
}

@Composable
fun MyVpnApp() {
    var selectedTab by remember { mutableStateOf(0) }
    var selectedEngine by remember { mutableStateOf("Xray / V2Ray") }
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
                            Text("موتور اتصال انتخابی: $selectedEngine", fontSize = 13.sp)
                            Spacer(Modifier.height(18.dp))
                            Button(onClick = { }) { Text("اتصال (در نسخه بعد فعال می‌شود)") }
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
                    Text("این نسخه فعلاً پوستهٔ اولیه است؛ اتصال واقعی پس از افزودن موتور و پیکربندی معتبر فعال می‌شود.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            Column(Modifier.fillMaxSize().padding(padding).padding(20.dp)) {
                Text("تنظیمات", fontSize = 26.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(16.dp))
                Text("DNS، مسیریابی برنامه‌ها، اشتراک‌ها و گزارش اتصال در مراحل بعد اضافه می‌شوند.")
                Spacer(Modifier.height(12.dp))
                Text("نسخه 0.1.0")
            }
        }
    }
}
