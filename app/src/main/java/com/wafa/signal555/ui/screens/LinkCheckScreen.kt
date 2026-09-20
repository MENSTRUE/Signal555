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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wafa.signal555.ui.components.AppTopBar
import com.wafa.signal555.ui.theme.SignalBlack
import com.wafa.signal555.ui.theme.SignalGreen
import com.wafa.signal555.ui.theme.SignalMuted
import com.wafa.signal555.ui.theme.SignalRed
import com.wafa.signal555.ui.theme.SignalSurface
import com.wafa.signal555.ui.theme.SignalWarning

@Composable
fun LinkCheckScreen(
    onBack: () -> Unit,
    onAsk: () -> Unit,
    onNextAction: () -> Unit
) {
    var url by remember { mutableStateOf("https://hadiah-special.com") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SignalSurface)
    ) {
        AppTopBar(
            title = "Link Check",
            onBack = onBack,
            trailing = { Icon(Icons.Outlined.MoreVert, contentDescription = null) }
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(18.dp)
        ) {
            Text(
                "Periksa tautan sebelum dibuka",
                color = SignalBlack,
                fontSize = 21.sp,
                fontWeight = FontWeight.Black
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "Analisis ini masih demo UI. Nanti akan disambungkan ke pemeriksaan domain dan AI.",
                color = SignalMuted,
                fontSize = 11.sp,
                lineHeight = 16.sp
            )

            Spacer(Modifier.height(18.dp))
            OutlinedTextField(
                value = url,
                onValueChange = { url = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = { Icon(Icons.Outlined.Link, contentDescription = null) },
                label = { Text("Tautan") },
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(Modifier.height(18.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SignalWarning)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(Icons.Outlined.WarningAmber, contentDescription = null, tint = SignalRed)
                    Column(modifier = Modifier.padding(start = 12.dp)) {
                        Text("Perlu Perhatian", color = SignalRed, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(3.dp))
                        Text(
                            "Domain terlihat tidak familiar dan menyerupai pola tautan promosi palsu.",
                            color = SignalBlack,
                            fontSize = 12.sp,
                            lineHeight = 17.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(22.dp))
            LinkRiskRow("Domain tidak dikenal", "Nama domain tidak sesuai layanan resmi yang umum digunakan.")
            LinkRiskRow("Menggunakan kata pemicu hadiah", "Tautan memakai pola nama yang mendorong rasa penasaran atau urgensi.")
            LinkRiskRow("Butuh verifikasi sumber", "Pastikan tautan berasal dari kanal resmi sebelum melanjutkan.")

            Spacer(Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(9.dp)
                        .background(SignalGreen, CircleShape)
                )
                Text("  Pemeriksaan UI selesai", color = SignalMuted, fontSize = 11.sp)
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = onNextAction,
                modifier = Modifier.weight(1f).height(52.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Outlined.Security, contentDescription = null, modifier = Modifier.size(18.dp))
                Text("  Langkah Aman")
            }
            Button(
                onClick = onAsk,
                modifier = Modifier.weight(1f).height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SignalBlack)
            ) {
                Icon(Icons.Outlined.ChatBubbleOutline, contentDescription = null, modifier = Modifier.size(18.dp))
                Text("  Tanya AI")
            }
        }
    }
}

@Composable
private fun LinkRiskRow(title: String, subtitle: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .padding(top = 5.dp)
                .size(8.dp)
                .background(SignalRed, CircleShape)
        )
        Column(modifier = Modifier.padding(start = 12.dp)) {
            Text(title, color = SignalBlack, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            Text(subtitle, color = SignalMuted, fontSize = 11.sp, lineHeight = 16.sp)
        }
    }
}
