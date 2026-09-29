package com.wafa.signal555.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wafa.signal555.ui.components.AppTopBar
import com.wafa.signal555.ui.components.BottomDestination
import com.wafa.signal555.ui.components.QuickAssistCard
import com.wafa.signal555.ui.components.SignalBottomBar
import com.wafa.signal555.ui.theme.SignalBlack
import com.wafa.signal555.ui.theme.SignalMuted
import com.wafa.signal555.ui.theme.SignalRed
import com.wafa.signal555.ui.theme.SignalSurface

@Composable
fun CheckScreen(
    onScreenshot: () -> Unit,
    onLink: () -> Unit,
    onCamera: () -> Unit,
    onDocument: () -> Unit,
    onHome: () -> Unit,
    onAsk: () -> Unit,
    onHistory: () -> Unit,
    onProfile: () -> Unit
) {
    Scaffold(
        containerColor = SignalSurface,
        bottomBar = {
            SignalBottomBar(
                selected = BottomDestination.Check,
                onHome = onHome,
                onCheck = {},
                onAsk = onAsk,
                onHistory = onHistory,
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
                title = "Check",
                trailing = {
                    Icon(
                        Icons.Outlined.Security,
                        contentDescription = null,
                        tint = SignalRed
                    )
                }
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 22.dp)
            ) {
                Text(
                    "Periksa sebelum percaya.",
                    color = SignalBlack,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    "Pilih jenis informasi yang ingin kamu periksa atau pahami.",
                    color = SignalMuted,
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )

                Spacer(Modifier.height(24.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    QuickAssistCard(
                        icon = Icons.Outlined.PhotoCamera,
                        title = "Screenshot Check",
                        subtitle = "Periksa pesan, poster, atau gambar mencurigakan",
                        onClick = onScreenshot,
                        modifier = Modifier.weight(1f)
                    )
                    QuickAssistCard(
                        icon = Icons.Outlined.Link,
                        title = "Link Check",
                        subtitle = "Tinjau tautan sebelum kamu membukanya",
                        onClick = onLink,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    QuickAssistCard(
                        icon = Icons.Outlined.CameraAlt,
                        title = "Camera Assist",
                        subtitle = "Pahami objek dan teks di depanmu",
                        onClick = onCamera,
                        modifier = Modifier.weight(1f)
                    )
                    QuickAssistCard(
                        icon = Icons.Outlined.Description,
                        title = "Document AI",
                        subtitle = "Baca, ringkas, dan cari isi PDF secara lokal",
                        onClick = onDocument,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(Modifier.height(30.dp))
                Text(
                    "555 / CHECK BEFORE ACTION",
                    color = SignalMuted,
                    fontSize = 9.sp,
                    letterSpacing = 1.5.sp
                )
            }
        }
    }
}
