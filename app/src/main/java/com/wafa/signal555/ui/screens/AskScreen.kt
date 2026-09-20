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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import com.wafa.signal555.ui.theme.SignalMuted
import com.wafa.signal555.ui.theme.SignalRed
import com.wafa.signal555.ui.theme.SignalSurface

@Composable
fun AskScreen(onBack: () -> Unit) {
    var input by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().background(SignalSurface)) {
        AppTopBar(title = "Ask 555", onBack = onBack)
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(18.dp)
        ) {
            Text("AI Assistant", color = SignalBlack, fontWeight = FontWeight.Black, fontSize = 24.sp)
            Text("Memahami konteks, bukan sekadar menjawab.", color = SignalMuted, fontSize = 12.sp)
            Spacer(Modifier.height(24.dp))

            MessageBubble(
                text = "Ada yang ingin kamu periksa, pahami, atau putuskan? Kirim konteksnya dan saya bantu menguraikannya.",
                isUser = false
            )
            MessageBubble(
                text = "Saya dapat pesan dengan link yang mencurigakan. Apa yang sebaiknya saya cek dulu?",
                isUser = true
            )
            MessageBubble(
                text = "Mulai dari tiga hal: identitas pengirim, alamat domain, dan apakah pesannya mendesak atau meminta data sensitif. Jangan buka tautan sebelum ketiganya masuk akal.",
                isUser = false
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth().background(Color.White).padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = input,
                onValueChange = { input = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Tanya sesuatu...", fontSize = 13.sp) },
                shape = RoundedCornerShape(18.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF1F2F3),
                    unfocusedContainerColor = Color(0xFFF1F2F3),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
            Spacer(Modifier.size(8.dp))
            Box(
                modifier = Modifier.size(48.dp).background(SignalRed, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                IconButton(onClick = { input = "" }) {
                    Icon(Icons.Outlined.Send, null, tint = Color.White)
                }
            }
        }
    }
}

@Composable
private fun MessageBubble(text: String, isUser: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(if (isUser) .78f else .88f),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isUser) SignalRed else Color.White
            )
        ) {
            Text(
                text,
                modifier = Modifier.padding(15.dp),
                color = if (isUser) Color.White else SignalBlack,
                fontSize = 13.sp,
                lineHeight = 19.sp
            )
        }
    }
}
