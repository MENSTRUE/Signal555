package com.wafa.signal555.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
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
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.FlashOff
import androidx.compose.material.icons.outlined.FlashOn
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.wafa.signal555.data.local.LocalHistoryItem
import com.wafa.signal555.data.local.LocalHistoryStore
import com.wafa.signal555.data.local.LocalRiskAnalyzer
import com.wafa.signal555.data.local.LocalRiskResult
import com.wafa.signal555.data.local.RiskLevel
import com.wafa.signal555.ui.theme.SignalRed
import java.io.File

private data class CameraScanResult(
    val text: String,
    val risk: LocalRiskResult
)

@Composable
fun CameraAssistScreen(onBack: () -> Unit, onAsk: () -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val imageCapture = remember {
        ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()
    }
    val recognizer = remember { TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS) }

    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
        )
    }
    var permissionDenied by remember { mutableStateOf(false) }
    var torchEnabled by remember { mutableStateOf(false) }
    var processing by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var scanResult by remember { mutableStateOf<CameraScanResult?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
        permissionDenied = !granted
    }

    LaunchedEffect(Unit) {
        if (!hasPermission) permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    DisposableEffect(Unit) {
        onDispose { recognizer.close() }
    }

    fun captureAndAnalyze() {
        if (!hasPermission || processing) return

        processing = true
        errorMessage = null
        scanResult = null

        val photoFile = File(context.cacheDir, "signal555_camera_${System.currentTimeMillis()}.jpg")
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val image = runCatching {
                        InputImage.fromFilePath(context, Uri.fromFile(photoFile))
                    }.getOrElse {
                        processing = false
                        errorMessage = "Foto berhasil diambil, tetapi tidak dapat dibaca."
                        return
                    }

                    recognizer.process(image)
                        .addOnSuccessListener { visionText ->
                            val text = visionText.text.trim()
                            if (text.isBlank()) {
                                processing = false
                                errorMessage = "Tidak ada teks yang terbaca. Arahkan kamera lebih dekat dan coba lagi."
                                return@addOnSuccessListener
                            }

                            val risk = LocalRiskAnalyzer.analyze(text)
                            scanResult = CameraScanResult(text = text, risk = risk)
                            LocalHistoryStore.add(
                                context,
                                LocalHistoryItem(
                                    type = "camera",
                                    title = text.lineSequence().firstOrNull()?.take(55) ?: "Camera Check",
                                    status = when (risk.level) {
                                        RiskLevel.HIGH -> "Risiko tinggi"
                                        RiskLevel.MEDIUM -> "Perlu perhatian"
                                        RiskLevel.LOW -> "Risiko rendah"
                                    },
                                    score = risk.score
                                )
                            )
                            processing = false
                        }
                        .addOnFailureListener {
                            processing = false
                            errorMessage = "OCR gagal membaca foto. Coba ambil ulang dengan pencahayaan lebih baik."
                        }
                }

                override fun onError(exception: ImageCaptureException) {
                    processing = false
                    errorMessage = "Kamera gagal mengambil foto: ${exception.message ?: "unknown error"}"
                }
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0E1013))
    ) {
        when {
            hasPermission -> {
                CameraPreview(
                    imageCapture = imageCapture,
                    torchEnabled = torchEnabled,
                    modifier = Modifier.fillMaxSize(),
                    onError = { errorMessage = it }
                )
            }

            else -> {
                PermissionState(
                    denied = permissionDenied,
                    onRequest = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black.copy(alpha = .42f))
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Outlined.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text("Camera Assist", color = Color.White, fontWeight = FontWeight.Bold)
                Text("Local OCR + risk analysis", color = Color.White.copy(alpha = .62f), fontSize = 10.sp)
            }
            IconButton(
                onClick = { torchEnabled = !torchEnabled },
                enabled = hasPermission
            ) {
                Icon(
                    if (torchEnabled) Icons.Outlined.FlashOn else Icons.Outlined.FlashOff,
                    contentDescription = "Flash",
                    tint = if (torchEnabled) SignalRed else Color.White
                )
            }
        }

        if (hasPermission && scanResult == null) {
            ScanFrame(modifier = Modifier.align(Alignment.Center))
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color(0xE8111316), RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .padding(horizontal = 18.dp, vertical = 18.dp)
        ) {
            when {
                processing -> ProcessingState()
                scanResult != null -> ResultState(
                    result = scanResult!!,
                    onScanAgain = {
                        scanResult = null
                        errorMessage = null
                    },
                    onAsk = onAsk
                )
                else -> CaptureState(
                    errorMessage = errorMessage,
                    enabled = hasPermission,
                    onCapture = ::captureAndAnalyze
                )
            }
        }
    }
}

@Composable
private fun CameraPreview(
    imageCapture: ImageCapture,
    torchEnabled: Boolean,
    modifier: Modifier,
    onError: (String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember {
        PreviewView(context).apply {
            scaleType = PreviewView.ScaleType.FILL_CENTER
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
        }
    }

    var camera by remember { mutableStateOf<Camera?>(null) }
    var cameraProvider by remember { mutableStateOf<ProcessCameraProvider?>(null) }

    DisposableEffect(lifecycleOwner, previewView, imageCapture) {
        var active = true
        val providerFuture = ProcessCameraProvider.getInstance(context)

        providerFuture.addListener(
            {
                if (!active) return@addListener
                runCatching {
                    val provider = providerFuture.get()
                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }
                    provider.unbindAll()
                    val boundCamera = provider.bindToLifecycle(
                        lifecycleOwner,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview,
                        imageCapture
                    )
                    cameraProvider = provider
                    camera = boundCamera
                }.onFailure {
                    onError("Kamera tidak dapat dimulai pada perangkat ini.")
                }
            },
            ContextCompat.getMainExecutor(context)
        )

        onDispose {
            active = false
            cameraProvider?.unbindAll()
        }
    }

    LaunchedEffect(torchEnabled, camera) {
        camera?.cameraControl?.enableTorch(torchEnabled)
    }

    AndroidView(
        factory = { previewView },
        modifier = modifier
    )
}

@Composable
private fun ScanFrame(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth(.78f)
            .height(270.dp)
            .clip(RoundedCornerShape(26.dp))
            .background(Color.White.copy(alpha = .045f))
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(.92f)
                .height(2.dp)
                .background(SignalRed.copy(alpha = .9f))
        )
        Text(
            "ARAHKAN KE TEKS",
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 14.dp),
            color = Color.White.copy(alpha = .72f),
            fontSize = 9.sp,
            letterSpacing = 1.5.sp
        )
    }
}

@Composable
private fun PermissionState(
    denied: Boolean,
    onRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.padding(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1D21)),
        shape = RoundedCornerShape(22.dp)
    ) {
        Column(
            modifier = Modifier.padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Outlined.PhotoCamera, null, tint = SignalRed, modifier = Modifier.size(38.dp))
            Spacer(Modifier.height(12.dp))
            Text("Camera permission", color = Color.White, fontWeight = FontWeight.Bold)
            Text(
                if (denied) "Izin kamera ditolak. Berikan izin agar Camera Assist dapat digunakan."
                else "Camera Assist membutuhkan akses kamera untuk memindai teks secara lokal.",
                color = Color.White.copy(alpha = .62f),
                fontSize = 12.sp
            )
            Spacer(Modifier.height(16.dp))
            Button(onClick = onRequest, colors = ButtonDefaults.buttonColors(containerColor = SignalRed)) {
                Text("Izinkan kamera")
            }
        }
    }
}

@Composable
private fun CaptureState(
    errorMessage: String?,
    enabled: Boolean,
    onCapture: () -> Unit
) {
    Text("Scan text in front of you", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
    Text(
        "Ambil foto. OCR dan analisis dilakukan di perangkat.",
        color = Color.White.copy(alpha = .58f),
        fontSize = 11.sp
    )

    if (errorMessage != null) {
        Spacer(Modifier.height(10.dp))
        Text(errorMessage, color = Color(0xFFFFB4AB), fontSize = 11.sp)
    }

    Spacer(Modifier.height(16.dp))
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        IconButton(
            onClick = onCapture,
            enabled = enabled,
            modifier = Modifier
                .size(72.dp)
                .background(Color.White, CircleShape)
        ) {
            Icon(Icons.Outlined.PhotoCamera, contentDescription = "Capture", tint = Color(0xFF17191C), modifier = Modifier.size(30.dp))
        }
    }
}

@Composable
private fun ProcessingState() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        CircularProgressIndicator(color = SignalRed, modifier = Modifier.size(28.dp), strokeWidth = 3.dp)
        Column(modifier = Modifier.padding(start = 14.dp)) {
            Text("ANALYZING", color = Color.White, fontWeight = FontWeight.Black, letterSpacing = 1.2.sp)
            Text("OCR lokal sedang membaca foto...", color = Color.White.copy(alpha = .58f), fontSize = 11.sp)
        }
    }
}

@Composable
private fun ResultState(
    result: CameraScanResult,
    onScanAgain: () -> Unit,
    onAsk: () -> Unit
) {
    val title = when (result.risk.level) {
        RiskLevel.HIGH -> "RISIKO TINGGI"
        RiskLevel.MEDIUM -> "PERLU PERHATIAN"
        RiskLevel.LOW -> "RISIKO RENDAH"
    }
    val icon = when (result.risk.level) {
        RiskLevel.HIGH, RiskLevel.MEDIUM -> Icons.Outlined.WarningAmber
        RiskLevel.LOW -> Icons.Outlined.CheckCircle
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = SignalRed, modifier = Modifier.size(28.dp))
        Column(modifier = Modifier.padding(start = 12.dp).weight(1f)) {
            Text(title, color = Color.White, fontWeight = FontWeight.Black)
            Text("Risk indicator ${result.risk.score}/100", color = Color.White.copy(alpha = .6f), fontSize = 11.sp)
        }
        Icon(Icons.Outlined.Security, null, tint = Color.White.copy(alpha = .7f))
    }

    Spacer(Modifier.height(12.dp))
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(165.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(result.risk.summary, color = Color.White.copy(alpha = .83f), fontSize = 12.sp)
        Spacer(Modifier.height(10.dp))
        result.risk.signals.take(4).forEach { signal ->
            Text("• ${signal.title}", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            Text(signal.detail, color = Color.White.copy(alpha = .55f), fontSize = 10.sp)
            Spacer(Modifier.height(7.dp))
        }
        Spacer(Modifier.height(5.dp))
        Text("Teks terbaca", color = Color.White.copy(alpha = .55f), fontSize = 9.sp, letterSpacing = 1.sp)
        Text(
            result.text,
            color = Color.White.copy(alpha = .78f),
            fontSize = 11.sp,
            maxLines = 5,
            overflow = TextOverflow.Ellipsis
        )
    }

    Spacer(Modifier.height(14.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Button(
            onClick = onScanAgain,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2B2F34)),
            shape = RoundedCornerShape(14.dp)
        ) {
            Icon(Icons.Outlined.Refresh, null, modifier = Modifier.size(17.dp))
            Text(" Scan lagi")
        }
        Button(
            onClick = onAsk,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(containerColor = SignalRed),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text("Tanya 555")
        }
    }
}
