package com.wafa.signal555.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wafa.signal555.ui.theme.SignalBlack
import com.wafa.signal555.ui.theme.SignalBorder
import com.wafa.signal555.ui.theme.SignalCard
import com.wafa.signal555.ui.theme.SignalMuted
import com.wafa.signal555.ui.theme.SignalRed

@Composable
fun SignalMark(modifier: Modifier = Modifier, dark: Boolean = false) {
    Row(modifier = modifier, verticalAlignment = Alignment.Bottom) {
        Text(
            text = "555",
            color = SignalRed,
            fontSize = 28.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = (-1).sp
        )
        Text(
            text = " Assist",
            color = if (dark) Color.White else SignalBlack,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun SectionHeader(title: String, action: String? = null, onAction: (() -> Unit)? = null) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = SignalBlack)
        if (action != null) {
            Text(
                text = action,
                color = SignalMuted,
                fontSize = 12.sp,
                modifier = Modifier.clickable(enabled = onAction != null) { onAction?.invoke() }
            )
        }
    }
}

@Composable
fun QuickAssistCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SignalCard),
        border = BorderStroke(1.dp, SignalBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(SignalRed.copy(alpha = 0.08f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = SignalRed, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.height(18.dp))
            Text(title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = SignalBlack)
            Spacer(Modifier.height(3.dp))
            Text(subtitle, fontSize = 11.sp, color = SignalMuted, lineHeight = 15.sp)
        }
    }
}

@Composable
fun PrimaryButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().height(54.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = SignalRed)
    ) {
        Text(text, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun AppTopBar(title: String, onBack: (() -> Unit)? = null, trailing: (@Composable () -> Unit)? = null) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (onBack != null) {
            IconButton(onClick = onBack) {
                Text("‹", fontSize = 34.sp, color = SignalBlack, fontWeight = FontWeight.Light)
            }
        } else {
            Spacer(Modifier.size(48.dp))
        }
        Text(
            title,
            modifier = Modifier.weight(1f),
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            color = SignalBlack
        )
        Box(modifier = Modifier.size(48.dp), contentAlignment = Alignment.Center) {
            trailing?.invoke()
        }
    }
    HorizontalDivider(color = SignalBorder)
}

enum class BottomDestination { Home, Check, Ask, History, Profile }

@Composable
fun SignalBottomBar(
    selected: BottomDestination,
    onHome: () -> Unit,
    onCheck: () -> Unit,
    onAsk: () -> Unit,
    onHistory: () -> Unit,
    onProfile: () -> Unit
) {
    val items = listOf(
        Triple(BottomDestination.Home, Icons.Outlined.Home, "Home"),
        Triple(BottomDestination.Check, Icons.Outlined.Security, "Check"),
        Triple(BottomDestination.Ask, Icons.Outlined.ChatBubbleOutline, "Ask"),
        Triple(BottomDestination.History, Icons.Outlined.History, "History"),
        Triple(BottomDestination.Profile, Icons.Outlined.PersonOutline, "Profile")
    )
    val handlers = mapOf(
        BottomDestination.Home to onHome,
        BottomDestination.Check to onCheck,
        BottomDestination.Ask to onAsk,
        BottomDestination.History to onHistory,
        BottomDestination.Profile to onProfile
    )

    Column(modifier = Modifier.background(Color.White)) {
        HorizontalDivider(color = SignalBorder)
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 7.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            items.forEach { (destination, icon, label) ->
                val active = destination == selected
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { handlers[destination]?.invoke() }
                        .padding(vertical = 2.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        icon,
                        contentDescription = label,
                        tint = if (active) SignalRed else SignalMuted,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(Modifier.height(3.dp))
                    Text(
                        label,
                        fontSize = 10.sp,
                        fontWeight = if (active) FontWeight.Bold else FontWeight.Normal,
                        color = if (active) SignalRed else SignalMuted
                    )
                }
            }
        }
    }
}
