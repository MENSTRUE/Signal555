package com.wafa.signal555.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteSweep
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wafa.signal555.data.local.LocalHistoryItem
import com.wafa.signal555.data.local.LocalHistoryStore
import com.wafa.signal555.ui.components.AppTopBar
import com.wafa.signal555.ui.components.BottomDestination
import com.wafa.signal555.ui.components.SignalBottomBar
import com.wafa.signal555.ui.theme.SignalBlack
import com.wafa.signal555.ui.theme.SignalMuted
import com.wafa.signal555.ui.theme.SignalRed
import com.wafa.signal555.ui.theme.SignalSurface
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    onHome: () -> Unit,
    onCheck: () -> Unit,
    onAsk: () -> Unit,
    onProfile: () -> Unit
) {
    val context = LocalContext.current
    var items by remember { mutableStateOf(LocalHistoryStore.getAll(context)) }

    Scaffold(
        containerColor = SignalSurface,
        bottomBar = {
            SignalBottomBar(
                selected = BottomDestination.History,
                onHome = onHome,
                onCheck = onCheck,
                onAsk = onAsk,
                onHistory = {},
                onProfile = onProfile
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(SignalSurface)
        ) {
            AppTopBar(
                title = "History",
                trailing = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (items.isNotEmpty()) {
                            IconButton(onClick = {
                                LocalHistoryStore.clear(context)
                                items = emptyList()
                            }) {
                                Icon(Icons.Outlined.DeleteSweep, contentDescription = "Hapus riwayat", tint = SignalMuted)
                            }
                        }
                        Icon(Icons.Outlined.History, contentDescription = null, tint = SignalRed)
                    }
                }
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                Text("Recent checks", fontSize = 23.sp, fontWeight = FontWeight.Black, color = SignalBlack)
                Text("Riwayat pemeriksaan disimpan lokal di perangkat.", color = SignalMuted, fontSize = 12.sp)
                Spacer(Modifier.height(20.dp))

                if (items.isEmpty()) {
                    EmptyHistory()
                } else {
                    items.forEach { item ->
                        HistoryRow(item)
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyHistory() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 36.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Outlined.History, null, tint = SignalMuted, modifier = Modifier.size(38.dp))
        Spacer(Modifier.height(10.dp))
        Text("Belum ada pemeriksaan", color = SignalBlack, fontWeight = FontWeight.SemiBold)
        Text("Hasil Screenshot, Link, dan Camera Check akan muncul di sini.", color = SignalMuted, fontSize = 11.sp)
    }
}

@Composable
private fun HistoryRow(item: LocalHistoryItem) {
    val icon: ImageVector = when (item.type) {
        "link" -> Icons.Outlined.Link
        "screenshot", "camera" -> Icons.Outlined.PhotoCamera
        else -> Icons.Outlined.Description
    }
    val scoreText = item.score?.let { " • $it/100" }.orEmpty()
    val time = remember(item.createdAt) {
        SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault()).format(Date(item.createdAt))
    }

    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(42.dp).background(SignalRed.copy(alpha = .08f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = SignalRed, modifier = Modifier.size(20.dp))
        }
        Column(modifier = Modifier.padding(start = 12.dp).weight(1f)) {
            Text(item.title, fontWeight = FontWeight.SemiBold, color = SignalBlack, fontSize = 13.sp)
            Text(item.status + scoreText, color = SignalMuted, fontSize = 11.sp)
        }
        Text(time, color = SignalMuted, fontSize = 10.sp)
    }
}
