package com.wafa.signal555.ui.screens

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material.icons.outlined.ImageSearch
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.wafa.signal555.data.local.LocalRiskAnalyzer
import com.wafa.signal555.data.local.LocalRiskResult
import com.wafa.signal555.data.local.RiskLevel
import com.wafa.signal555.ui.components.AppTopBar
import com.wafa.signal555.ui.theme.SignalBlack
import com.wafa.signal555.ui.theme.SignalBorder
import com.wafa.signal555.ui.theme.SignalGreen
import com.wafa.signal555.ui.theme.SignalMuted
import com.wafa.signal555.ui.theme.SignalRed
import com.wafa.signal555.ui.theme.SignalSurface
import com.wafa.signal555.ui.theme.SignalWarning

private sealed interface ScreenshotState {
    data object Empty : ScreenshotState
    data object Processing : ScreenshotState
    data class Success(
        val extractedText: String,
        val result: LocalRiskResult
    ) : ScreenshotState
    data class Error(val message: String) : ScreenshotState
}

@Composable
fun ScreenshotCheckScreen(
    onBack: () -> Unit,
    onAsk: () -> Unit,
    onNextAction: () -> Unit
) {
    val context = LocalContext.current
    val recognizer = remember {
        TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    }

    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    var previewBitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }
    var state by remember { mutableStateOf<ScreenshotState>(ScreenshotState.Empty) }

    DisposableEffect(Unit) {
        onDispose { recognizer.close() }
    }

    fun analyzeScreenshot(uri: Uri) {
        selectedUri = uri
        previewBitmap = runCatching {
            context.contentResolver.openInputStream(uri)?.use { stream -> BitmapFactory.decodeStream(stream) }
        }.getOrNull()
        state = ScreenshotState.Processing

        val image = runCatching { InputImage.fromFilePath(context, uri) }
            .getOrElse {
                state = ScreenshotState.Error("Gambar tidak dapat dibaca. Coba pilih screenshot lain.")
                return
            }

        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                val extracted = visionText.text.trim()
                if (extracted.isBlank()) {
                    state = ScreenshotState.Error(
                        "Tidak ada teks yang terbaca. Gunakan screenshot yang lebih jelas atau tidak terlalu kecil."
                    )
                } else {
                    state = ScreenshotState.Success(
                        extractedText = extracted,
                        result = LocalRiskAnalyzer.analyze(extracted)
                    )
                }
            }
            .addOnFailureListener {
                state = ScreenshotState.Error(
                    "OCR gagal memproses gambar. Coba screenshot lain."
                )
            }
    }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let(::analyzeScreenshot)
    }

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
            PrivacyBanner()
            Spacer(Modifier.height(14.dp))

            when (val current = state) {
                ScreenshotState.Empty -> EmptyState(
                    onPick = { imagePicker.launch("image/*") }
                )

                ScreenshotState.Processing -> ProcessingState(previewBitmap)

                is ScreenshotState.Error -> ErrorState(
                    previewBitmap = previewBitmap,
                    message = current.message,
                    onPickAgain = { imagePicker.launch("image/*") }
                )

                is ScreenshotState.Success -> SuccessState(
                    previewBitmap = previewBitmap,
                    extractedText = current.extractedText,
                    result = current.result
                )
            }
        }

        when (state) {
            is ScreenshotState.Success -> {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = { imagePicker.launch("image/*") },
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(
                            Icons.Outlined.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Text("  Pilih Ulang")
                    }
                    Button(
                        onClick = onAsk,
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SignalBlack)
                    ) {
                        Icon(
                            Icons.Outlined.ChatBubbleOutline,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Text("  Tanya AI")
                    }
                }
            }

            else -> Unit
        }
    }
}

@Composable
private fun PrivacyBanner() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF2F4F3)),
        border = androidx.compose.foundation.BorderStroke(1.dp, SignalBorder)
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
                Text(
                    "ON-DEVICE",
                    color = SignalBlack,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "OCR dan analisis berjalan lokal. Screenshot tidak dikirim ke server.",
                    color = SignalMuted,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )
            }
        }
    }
}

@Composable
private fun EmptyState(onPick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, SignalBorder)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(74.dp)
                    .background(Color(0xFFF4F4F5), RoundedCornerShape(22.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Outlined.ImageSearch,
                    contentDescription = null,
                    tint = SignalRed,
                    modifier = Modifier.size(34.dp)
                )
            }
            Spacer(Modifier.height(18.dp))
            Text(
                "Periksa screenshot",
                color = SignalBlack,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            Spacer(Modifier.height(7.dp))
            Text(
                "Pilih screenshot pesan, SMS, email, lowongan, atau promo. 555 akan membaca teks dan menandai pola yang perlu diverifikasi.",
                color = SignalMuted,
                fontSize = 12.sp,
                lineHeight = 18.sp
            )
            Spacer(Modifier.height(22.dp))
            Button(
                onClick = onPick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SignalBlack)
            ) {
                Icon(Icons.Outlined.PhotoLibrary, contentDescription = null)
                Text("  Pilih Screenshot")
            }
        }
    }
}

@Composable
private fun ProcessingState(previewBitmap: android.graphics.Bitmap?) {
    ScreenshotPreview(previewBitmap)
    Spacer(Modifier.height(22.dp))
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(34.dp),
            color = SignalRed,
            strokeWidth = 3.dp
        )
        Spacer(Modifier.height(12.dp))
        Text(
            "ANALYZING",
            color = SignalBlack,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.4.sp,
            fontSize = 12.sp
        )
        Spacer(Modifier.height(5.dp))
        Text(
            "Membaca teks dan memeriksa pola risiko secara lokal...",
            color = SignalMuted,
            fontSize = 11.sp
        )
    }
}

@Composable
private fun ErrorState(
    previewBitmap: android.graphics.Bitmap?,
    message: String,
    onPickAgain: () -> Unit
) {
    ScreenshotPreview(previewBitmap)
    Spacer(Modifier.height(16.dp))
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SignalWarning)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
            Icon(Icons.Outlined.WarningAmber, null, tint = SignalRed)
            Column(modifier = Modifier.padding(start = 12.dp)) {
                Text("Tidak dapat dianalisis", color = SignalRed, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(3.dp))
                Text(message, color = SignalBlack, fontSize = 12.sp, lineHeight = 17.sp)
            }
        }
    }
    Spacer(Modifier.height(16.dp))
    Button(
        onClick = onPickAgain,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = SignalBlack)
    ) {
        Icon(Icons.Outlined.PhotoLibrary, contentDescription = null)
        Text("  Pilih Screenshot Lain")
    }
}

@Composable
private fun SuccessState(
    previewBitmap: android.graphics.Bitmap?,
    extractedText: String,
    result: LocalRiskResult
) {
    ScreenshotPreview(previewBitmap)
    Spacer(Modifier.height(16.dp))
    RiskSummaryCard(result)
    Spacer(Modifier.height(20.dp))

    Text(
        "Yang terdeteksi",
        color = SignalBlack,
        fontWeight = FontWeight.Bold,
        fontSize = 15.sp
    )
    Spacer(Modifier.height(6.dp))

    if (result.signals.isEmpty()) {
        RiskItem(
            title = "Tidak ada pola kuat yang terdeteksi",
            subtitle = "Tetap lakukan verifikasi jika pesan meminta tindakan penting.",
            color = SignalGreen
        )
    } else {
        result.signals.forEach { signal ->
            RiskItem(
                title = signal.title,
                subtitle = signal.detail,
                color = if (result.level == RiskLevel.LOW) SignalGreen else SignalRed
            )
        }
    }

    if (result.detectedLinks.isNotEmpty()) {
        Spacer(Modifier.height(12.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = androidx.compose.foundation.BorderStroke(1.dp, SignalBorder)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    "Tautan terbaca",
                    color = SignalBlack,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp
                )
                Spacer(Modifier.height(6.dp))
                result.detectedLinks.forEach { link ->
                    Text(link, color = SignalRed, fontSize = 11.sp, lineHeight = 16.sp)
                }
            }
        }
    }

    Spacer(Modifier.height(18.dp))
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF6F6F7))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                "Teks hasil OCR",
                color = SignalMuted,
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp
            )
            Spacer(Modifier.height(6.dp))
            Text(
                extractedText.take(1200),
                color = SignalBlack,
                fontSize = 11.sp,
                lineHeight = 16.sp
            )
            if (extractedText.length > 1200) {
                Spacer(Modifier.height(4.dp))
                Text("…teks dipotong untuk tampilan", color = SignalMuted, fontSize = 10.sp)
            }
        }
    }

    Spacer(Modifier.height(18.dp))
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(9.dp)
                .background(SignalGreen, RoundedCornerShape(20.dp))
        )
        Text("  Verification complete", color = SignalMuted, fontSize = 11.sp)
    }
    Spacer(Modifier.height(12.dp))
    Text(
        "Catatan: skor ini adalah indikator lokal berbasis pola, bukan kepastian bahwa pesan tersebut penipuan.",
        color = SignalMuted,
        fontSize = 11.sp,
        lineHeight = 16.sp
    )
}

@Composable
private fun ScreenshotPreview(previewBitmap: android.graphics.Bitmap?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE9EAEC))
    ) {
        if (previewBitmap != null) {
            Image(
                bitmap = previewBitmap.asImageBitmap(),
                contentDescription = "Screenshot yang dipilih",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                contentScale = ContentScale.Fit
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Outlined.ImageSearch,
                    contentDescription = null,
                    tint = SignalMuted,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

@Composable
private fun RiskSummaryCard(result: LocalRiskResult) {
    val accent = when (result.level) {
        RiskLevel.HIGH -> SignalRed
        RiskLevel.MEDIUM -> Color(0xFFD47A00)
        RiskLevel.LOW -> SignalGreen
    }
    val container = when (result.level) {
        RiskLevel.HIGH -> SignalWarning
        RiskLevel.MEDIUM -> Color(0xFFFFF4E5)
        RiskLevel.LOW -> Color(0xFFECF6EF)
    }
    val label = when (result.level) {
        RiskLevel.HIGH -> "RISIKO TINGGI"
        RiskLevel.MEDIUM -> "PERLU DIVERIFIKASI"
        RiskLevel.LOW -> "RISIKO RENDAH"
    }

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
                Column(modifier = Modifier.padding(start = 12.dp)) {
                    Text(label, color = accent, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text(
                        "Risk indicator ${result.score}/100",
                        color = SignalBlack,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                }
            }
            Spacer(Modifier.height(10.dp))
            Text(result.summary, color = SignalBlack, fontSize = 12.sp, lineHeight = 17.sp)
        }
    }
}

@Composable
private fun RiskItem(
    title: String,
    subtitle: String,
    color: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 9.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .padding(top = 5.dp)
                .size(8.dp)
                .background(color, RoundedCornerShape(20.dp))
        )
        Column(modifier = Modifier.padding(start = 12.dp)) {
            Text(title, color = SignalBlack, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            Text(subtitle, color = SignalMuted, fontSize = 11.sp, lineHeight = 16.sp)
        }
    }
}
