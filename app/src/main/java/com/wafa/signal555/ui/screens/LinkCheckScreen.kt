package com.wafa.signal555.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Refresh
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wafa.signal555.data.local.LinkSignal
import com.wafa.signal555.data.local.LocalHistoryItem
import com.wafa.signal555.data.local.LocalHistoryStore
import com.wafa.signal555.data.local.LocalLinkAnalyzer
import com.wafa.signal555.data.local.LocalLinkResult
import com.wafa.signal555.data.local.RiskLevel
import com.wafa.signal555.ui.components.AppTopBar
import com.wafa.signal555.ui.theme.SignalBlack
import com.wafa.signal555.ui.theme.SignalBorder
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
    val context = LocalContext.current
    var url by remember { mutableStateOf("") }
    var result by remember { mutableStateOf<LocalLinkResult?>(null) }
    var validationError by remember { mutableStateOf<String?>(null) }

    fun runCheck() {
        val value = url.trim()
        if (value.isBlank()) {
            validationError = "Masukkan tautan terlebih dahulu."
            result = null
            return
        }

        val checked = LocalLinkAnalyzer.analyze(value)
        if (checked.host == "-") {
            validationError = checked.summary
            result = null
            return
        }

        validationError = null
        result = checked
        val status = when (checked.level) {
            RiskLevel.HIGH -> "Risiko tinggi"
            RiskLevel.MEDIUM -> "Perlu perhatian"
            RiskLevel.LOW -> "Risiko rendah"
        }
        LocalHistoryStore.add(
            context,
            LocalHistoryItem(
                type = "link",
                title = checked.host,
                status = status,
                score = checked.score
            )
        )
    }

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
            OnDeviceLinkBanner()
            Spacer(Modifier.height(16.dp))

            Text(
                "Periksa tautan sebelum dibuka",
                color = SignalBlack,
                fontSize = 21.sp,
                fontWeight = FontWeight.Black
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "Signal555 membaca struktur URL secara lokal. Tidak ada koneksi ke reputation server pada versi ini.",
                color = SignalMuted,
                fontSize = 11.sp,
                lineHeight = 16.sp
            )

            Spacer(Modifier.height(18.dp))
            OutlinedTextField(
                value = url,
                onValueChange = {
                    url = it
                    validationError = null
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = { Icon(Icons.Outlined.Link, contentDescription = null) },
                label = { Text("Tautan") },
                placeholder = { Text("contoh.com/login") },
                isError = validationError != null,
                supportingText = validationError?.let { message -> { Text(message) } },
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(Modifier.height(12.dp))
            Button(
                onClick = ::runCheck,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SignalBlack)
            ) {
                Icon(Icons.Outlined.Security, contentDescription = null, modifier = Modifier.size(18.dp))
                Text("  Periksa Link")
            }

            result?.let { checked ->
                Spacer(Modifier.height(22.dp))
                LinkResultCard(checked)
                Spacer(Modifier.height(18.dp))

                if (checked.signals.isEmpty()) {
                    LinkRiskRow(
                        title = "Tidak ada pola kuat yang ditemukan",
                        subtitle = "Pemeriksaan lokal tidak menemukan indikator heuristik utama. Tetap cocokkan domain dengan kanal resmi.",
                        risky = false
                    )
                } else {
                    checked.signals.forEach { signal ->
                        LinkRiskRow(signal.title, signal.detail, risky = true)
                    }
                }

                Spacer(Modifier.height(8.dp))
                Text(
                    "Domain terbaca: ${checked.host}",
                    color = SignalMuted,
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )
                Text(
                    "Risk indicator ini bukan keputusan bahwa situs aman atau berbahaya.",
                    color = SignalMuted,
                    fontSize = 10.sp,
                    lineHeight = 15.sp
                )
            }
        }

        result?.let {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        url = ""
                        result = null
                        validationError = null
                    },
                    modifier = Modifier.weight(1f).height(52.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Outlined.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                    Text("  Cek Lagi")
                }
                Button(
                    onClick = onNextAction,
                    modifier = Modifier.weight(1f).height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SignalBlack)
                ) {
                    Icon(Icons.Outlined.Security, contentDescription = null, modifier = Modifier.size(18.dp))
                    Text("  Langkah Aman")
                }
            }
            OutlinedButton(
                onClick = onAsk,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(bottom = 12.dp).height(48.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Outlined.ChatBubbleOutline, contentDescription = null, modifier = Modifier.size(18.dp))
                Text("  Tanya Assistant")
            }
        }
    }
}

@Composable
private fun OnDeviceLinkBanner() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF2F4F3)),
        border = BorderStroke(1.dp, SignalBorder)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Outlined.CheckCircleOutline,
                contentDescription = null,
                tint = SignalGreen,
                modifier = Modifier.size(19.dp)
            )
            Column(modifier = Modifier.padding(start = 10.dp)) {
                Text("ON-DEVICE", color = SignalBlack, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Text(
                    "URL dianalisis lokal tanpa dibuka dan tanpa dikirim ke server.",
                    color = SignalMuted,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )
            }
        }
    }
}

@Composable
private fun LinkResultCard(result: LocalLinkResult) {
    val isHigh = result.level == RiskLevel.HIGH
    val isMedium = result.level == RiskLevel.MEDIUM
    val title = when (result.level) {
        RiskLevel.HIGH -> "RISIKO TINGGI"
        RiskLevel.MEDIUM -> "PERLU PERHATIAN"
        RiskLevel.LOW -> "RISIKO RENDAH"
    }
    val accent = if (result.level == RiskLevel.LOW) SignalGreen else SignalRed
    val container = if (isHigh || isMedium) SignalWarning else Color(0xFFF0F7F2)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = container)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    if (result.level == RiskLevel.LOW) Icons.Outlined.CheckCircleOutline else Icons.Outlined.WarningAmber,
                    contentDescription = null,
                    tint = accent
                )
                Text(
                    "  $title",
                    color = accent,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
            Spacer(Modifier.height(10.dp))
            Text(
                "Risk indicator ${result.score}/100",
                color = SignalBlack,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black
            )
            Spacer(Modifier.height(5.dp))
            Text(result.summary, color = SignalBlack, fontSize = 12.sp, lineHeight = 17.sp)
        }
    }
}

@Composable
private fun LinkRiskRow(title: String, subtitle: String, risky: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .padding(top = 5.dp)
                .size(8.dp)
                .background(if (risky) SignalRed else SignalGreen, CircleShape)
        )
        Column(modifier = Modifier.padding(start = 12.dp)) {
            Text(title, color = SignalBlack, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            Text(subtitle, color = SignalMuted, fontSize = 11.sp, lineHeight = 16.sp)
        }
    }
}
