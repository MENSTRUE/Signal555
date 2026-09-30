package com.wafa.signal555.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteSweep
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wafa.signal555.data.local.LocalAssistantEngine
import com.wafa.signal555.data.local.LocalChatMessage
import com.wafa.signal555.data.local.LocalChatStore
import com.wafa.signal555.data.local.LocalHistoryStore
import com.wafa.signal555.ui.components.AppTopBar
import com.wafa.signal555.ui.components.BottomDestination
import com.wafa.signal555.ui.components.SignalBottomBar
import com.wafa.signal555.ui.theme.SignalBlack
import com.wafa.signal555.ui.theme.SignalMuted
import com.wafa.signal555.ui.theme.SignalRed
import com.wafa.signal555.ui.theme.SignalSurface

private val quickPrompts = listOf(
    "Hasil check terakhir bagaimana?",
    "Kenapa skornya tinggi?",
    "Apa yang harus saya lakukan?",
    "Ringkas riwayat check saya"
)

@Composable
fun AskScreen(
    onHome: () -> Unit,
    onCheck: () -> Unit,
    onHistory: () -> Unit,
    onProfile: () -> Unit
) {
    val context = LocalContext.current
    var input by remember { mutableStateOf("") }
    var messages by remember {
        mutableStateOf(
            LocalChatStore.getAll(context).ifEmpty {
                listOf(
                    LocalChatMessage(
                        role = "assistant",
                        text = "Saya berjalan lokal di perangkat. Tanyakan hasil check terakhir, skor risiko, link, screenshot, kamera, dokumen, atau langkah aman."
                    )
                )
            }
        )
    }
    val listState = rememberLazyListState()

    fun send(raw: String) {
        val prompt = raw.trim()
        if (prompt.isBlank()) return

        val userMessage = LocalChatMessage(role = "user", text = prompt)
        val reply = LocalAssistantEngine.reply(
            prompt = prompt,
            history = LocalHistoryStore.getAll(context)
        )
        val assistantMessage = LocalChatMessage(role = "assistant", text = reply.text)

        LocalChatStore.add(context, userMessage)
        LocalChatStore.add(context, assistantMessage)
        messages = messages + userMessage + assistantMessage
        input = ""
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.lastIndex)
        }
    }

    Scaffold(
        containerColor = SignalSurface,
        bottomBar = {
            SignalBottomBar(
                selected = BottomDestination.Ask,
                onHome = onHome,
                onCheck = onCheck,
                onAsk = {},
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
                title = "Ask 555",
                trailing = {
                    if (messages.size > 1) {
                        IconButton(
                            onClick = {
                                LocalChatStore.clear(context)
                                messages = listOf(
                                    LocalChatMessage(
                                        role = "assistant",
                                        text = "Chat lokal dihapus. Saya siap mulai lagi."
                                    )
                                )
                            }
                        ) {
                            Icon(Icons.Outlined.DeleteSweep, "Hapus chat", tint = SignalMuted)
                        }
                    } else {
                        Icon(Icons.Outlined.Lock, null, tint = SignalMuted, modifier = Modifier.size(18.dp))
                    }
                }
            )

            Column(modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp)) {
                Text("Local Assistant", color = SignalBlack, fontWeight = FontWeight.Black, fontSize = 23.sp)
                Text("Offline contextual engine • data tetap di perangkat", color = SignalMuted, fontSize = 11.sp)
                Spacer(Modifier.height(10.dp))
                QuickPromptRow(onPrompt = ::send)
            }

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 8.dp)
            ) {
                items(messages) { message ->
                    MessageBubble(
                        text = message.text,
                        isUser = message.role == "user"
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = input,
                    onValueChange = { input = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Tanya konteks lokal...", fontSize = 13.sp) },
                    shape = RoundedCornerShape(18.dp),
                    singleLine = false,
                    maxLines = 4,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(onSend = { send(input) }),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFF1F2F3),
                        unfocusedContainerColor = Color(0xFFF1F2F3),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
                Spacer(Modifier.size(8.dp))
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(if (input.isBlank()) SignalMuted else SignalRed, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        enabled = input.isNotBlank(),
                        onClick = { send(input) }
                    ) {
                        Icon(Icons.Outlined.Send, null, tint = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickPromptRow(onPrompt: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
        quickPrompts.chunked(2).forEach { rowPrompts ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                rowPrompts.forEach { prompt ->
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onPrompt(prompt) },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Text(
                            prompt,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 9.dp),
                            color = SignalBlack,
                            fontSize = 10.sp,
                            lineHeight = 13.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MessageBubble(text: String, isUser: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(if (isUser) .78f else .90f),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isUser) SignalRed else Color.White
            )
        ) {
            Text(
                text = text,
                modifier = Modifier.padding(15.dp),
                color = if (isUser) Color.White else SignalBlack,
                fontSize = 13.sp,
                lineHeight = 19.sp
            )
        }
    }
}
