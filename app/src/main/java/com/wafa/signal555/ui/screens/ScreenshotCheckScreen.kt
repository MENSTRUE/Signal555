package com.wafa.signal555.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wafa.signal555.ui.components.AppTopBar
import com.wafa.signal555.ui.theme.SignalBlack
import com.wafa.signal555.ui.theme.SignalBorder
import com.wafa.signal555.ui.theme.SignalGreen
import com.wafa.signal555.ui.theme.SignalMuted
import com.wafa.signal555.ui.theme.SignalRed
import com.wafa.signal555.ui.theme.SignalSurface
import com.wafa.signal555.ui.theme.SignalWarning

@Composable
fun ScreenshotCheckScreen(
    onBack: () -> Unit,
    onAsk: () -> Unit,
    onNextAction: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SignalSurface)
    ) {
        AppTopBar(
            title = "Screenshot Check",
            onBack = onBack,
            trailing = { Icon(Icons.Outlined.MoreVert, contentDescription = null) }
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(18.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE9EAEC))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("+62 812 3456 7890", fontSize = 11.sp, color = SignalMuted)
                    Spacer(Modifier.height(8.dp))
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Text(
                            buildAnnotatedString {
                                append("Selamat!\nAnda terpilih sebagai pemenang hadiah terbaru. Segera klaim di ")
                                withStyle(SpanStyle(color = SignalRed, fontWeight = FontWeight.Bold)) {
                                    append("https://hadiah-special.com")
                                }
                                append(" sebelum kedaluwarsa!")
                            },
                            modifier = Modifier.padding(16.dp),
                            color = SignalBlack,
                            fontSize = 13.sp,
                            lineHeight = 19.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SignalWarning)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                    Icon(Icons.Outlined.WarningAmber, null, tint = SignalRed)
                    Column(modifier = Modifier.padding(start = 12.dp)) {
                        Text("Perlu Perhatian", color = SignalRed, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(3.dp))
                        Text(
                            "Pesan ini memiliki beberapa pola yang sering digunakan dalam penipuan digital.",
                            color = SignalBlack,
                            fontSize = 12.sp,
                            lineHeight = 17.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
            RiskItem("Pengirim tidak jelas", "Nomor tidak ada di kontak Anda")
            RiskItem("Ada unsur mendesak", "Pesan mendorong Anda bertindak segera")
            RiskItem("Terdapat tautan asing", "Domain tidak terlihat seperti layanan resmi")
            RiskItem("Menawarkan hadiah", "Imbalan digunakan untuk memancing tindakan")

            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(9.dp).background(SignalGreen, RoundedCornerShape(20.dp))
                )
                Text(
                    "  Verification complete",
                    color = SignalMuted,
                    fontSize = 11.sp
                )
            }
            Spacer(Modifier.height(22.dp))
            Text(
                "Catatan: hasil ini adalah indikator risiko, bukan kepastian bahwa pesan tersebut penipuan.",
                color = SignalMuted,
                fontSize = 11.sp,
                lineHeight = 16.sp
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = onNextAction,
                modifier = Modifier.weight(1f).height(52.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Outlined.Link, contentDescription = null, modifier = Modifier.size(18.dp))
                Text("  Cek Tautan")
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
private fun RiskItem(title: String, subtitle: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 9.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .padding(top = 5.dp)
                .size(8.dp)
                .background(SignalRed, RoundedCornerShape(20.dp))
        )
        Column(modifier = Modifier.padding(start = 12.dp)) {
            Text(title, color = SignalBlack, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            Text(subtitle, color = SignalMuted, fontSize = 11.sp)
        }
    }
}
