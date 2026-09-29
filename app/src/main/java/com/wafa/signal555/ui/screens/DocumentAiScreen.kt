package com.wafa.signal555.ui.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color as AndroidColor
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckBox
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.FolderOpen
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Summarize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.wafa.signal555.data.local.LocalDocumentAnalyzer
import com.wafa.signal555.data.local.LocalDocumentResult
import com.wafa.signal555.data.local.LocalHistoryItem
import com.wafa.signal555.data.local.LocalHistoryStore
import com.wafa.signal555.ui.components.AppTopBar
import com.wafa.signal555.ui.theme.SignalBlack
import com.wafa.signal555.ui.theme.SignalBorder
import com.wafa.signal555.ui.theme.SignalGreen
import com.wafa.signal555.ui.theme.SignalMuted
import com.wafa.signal555.ui.theme.SignalRed
import com.wafa.signal555.ui.theme.SignalSurface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.math.roundToInt

private const val MAX_DOCUMENT_PAGES = 12

private enum class DocumentMode {
    SUMMARY,
    SEARCH,
    TASKS
}

private sealed interface DocumentState {
    data object Empty : DocumentState

    data class Processing(
        val fileName: String,
        val currentPage: Int,
        val totalPages: Int
    ) : DocumentState

    data class Success(
        val fileName: String,
        val fileSize: Long?,
        val result: LocalDocumentResult
    ) : DocumentState

    data class Error(val message: String) : DocumentState
}

private data class PdfExtraction(
    val text: String,
    val analyzedPages: Int,
    val totalPages: Int
)

@Composable
fun DocumentAiScreen(
    onBack: () -> Unit,
    onNextAction: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val recognizer = remember {
        TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    }

    var state by remember { mutableStateOf<DocumentState>(DocumentState.Empty) }
    var mode by remember { mutableStateOf(DocumentMode.SUMMARY) }
    var searchQuery by remember { mutableStateOf("") }

    DisposableEffect(Unit) {
        onDispose { recognizer.close() }
    }

    fun processDocument(uri: Uri) {
        val metadata = readDocumentMetadata(context, uri)
        val fileName = metadata.first ?: "Dokumen.pdf"
        val fileSize = metadata.second

        mode = DocumentMode.SUMMARY
        searchQuery = ""
        state = DocumentState.Processing(fileName, currentPage = 0, totalPages = 0)

        scope.launch {
            runCatching {
                extractPdfText(
                    context = context,
                    uri = uri,
                    recognizer = recognizer,
                    onProgress = { current, total ->
                        state = DocumentState.Processing(
                            fileName = fileName,
                            currentPage = current,
                            totalPages = total
                        )
                    }
                )
            }.onSuccess { extraction ->
                if (extraction.text.isBlank()) {
                    state = DocumentState.Error(
                        "Teks tidak terbaca dari PDF. Coba dokumen yang lebih jelas atau tidak terlindungi."
                    )
                    return@onSuccess
                }

                val result = LocalDocumentAnalyzer.analyze(
                    rawText = extraction.text,
                    analyzedPages = extraction.analyzedPages,
                    totalPages = extraction.totalPages
                )

                state = DocumentState.Success(
                    fileName = fileName,
                    fileSize = fileSize,
                    result = result
                )

                LocalHistoryStore.add(
                    context,
                    LocalHistoryItem(
                        type = "document",
                        title = fileName,
                        status = "${result.analyzedPages}/${result.totalPages} halaman dianalisis",
                        score = null
                    )
                )
            }.onFailure { error ->
                state = DocumentState.Error(
                    error.message?.takeIf { it.isNotBlank() }
                        ?: "PDF gagal diproses. Coba dokumen lain."
                )
            }
        }
    }

    val pdfPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let(::processDocument)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SignalSurface)
    ) {
        AppTopBar(
            title = "Document AI",
            onBack = onBack,
            trailing = { Icon(Icons.Outlined.MoreVert, contentDescription = null) }
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(18.dp)
        ) {
            LocalDocumentPrivacyBanner()
            Spacer(Modifier.height(14.dp))

            when (val current = state) {
                DocumentState.Empty -> DocumentEmptyState(
                    onPick = { pdfPicker.launch(arrayOf("application/pdf")) }
                )

                is DocumentState.Processing -> DocumentProcessingState(current)

                is DocumentState.Error -> DocumentErrorState(
                    message = current.message,
                    onRetry = { pdfPicker.launch(arrayOf("application/pdf")) }
                )

                is DocumentState.Success -> DocumentSuccessState(
                    state = current,
                    mode = mode,
                    searchQuery = searchQuery,
                    onModeChange = { mode = it },
                    onSearchChange = { searchQuery = it }
                )
            }
        }

        if (state is DocumentState.Success) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = { pdfPicker.launch(arrayOf("application/pdf")) },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Outlined.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                    Text("  PDF lain")
                }

                Button(
                    onClick = onNextAction,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SignalBlack)
                ) {
                    Icon(Icons.Outlined.CheckBox, contentDescription = null, modifier = Modifier.size(18.dp))
                    Text("  Next Action")
                }
            }
        }
    }
}

@Composable
private fun LocalDocumentPrivacyBanner() {
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
                Text("Diproses lokal", color = SignalBlack, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                Text(
                    "PDF dirender dan dibaca dengan OCR di perangkat. Maksimal $MAX_DOCUMENT_PAGES halaman per analisis.",
                    color = SignalMuted,
                    fontSize = 10.sp,
                    lineHeight = 14.sp
                )
            }
        }
    }
}

@Composable
private fun DocumentEmptyState(onPick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onPick),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, SignalBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 34.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .background(SignalRed.copy(alpha = .08f), RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.Description, null, tint = SignalRed, modifier = Modifier.size(34.dp))
            }
            Spacer(Modifier.height(15.dp))
            Text("Pilih dokumen PDF", color = SignalBlack, fontWeight = FontWeight.Bold, fontSize = 17.sp)
            Text(
                "Signal555 akan membaca halaman sebagai gambar, menjalankan OCR lokal, lalu membuat ringkasan ekstraktif.",
                color = SignalMuted,
                fontSize = 11.sp,
                lineHeight = 17.sp
            )
            Spacer(Modifier.height(18.dp))
            Button(
                onClick = onPick,
                colors = ButtonDefaults.buttonColors(containerColor = SignalRed),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Outlined.FolderOpen, null, modifier = Modifier.size(18.dp))
                Text("  Pilih PDF")
            }
        }
    }
}

@Composable
private fun DocumentProcessingState(state: DocumentState.Processing) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(color = SignalRed, strokeWidth = 3.dp)
            Spacer(Modifier.height(14.dp))
            Text("Membaca dokumen…", color = SignalBlack, fontWeight = FontWeight.Bold)
            Text(state.fileName, color = SignalMuted, fontSize = 11.sp)
            Spacer(Modifier.height(5.dp))
            val progressText = when {
                state.totalPages <= 0 -> "Menyiapkan PDF"
                state.currentPage <= 0 -> "Menyiapkan ${state.totalPages} halaman"
                else -> "OCR halaman ${state.currentPage} dari ${minOf(state.totalPages, MAX_DOCUMENT_PAGES)}"
            }
            Text(progressText, color = SignalMuted, fontSize = 10.sp)
        }
    }
}

@Composable
private fun DocumentErrorState(
    message: String,
    onRetry: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Outlined.ErrorOutline, null, tint = SignalRed, modifier = Modifier.size(34.dp))
            Spacer(Modifier.height(10.dp))
            Text("Dokumen belum bisa dibaca", color = SignalBlack, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(5.dp))
            Text(message, color = SignalMuted, fontSize = 11.sp, lineHeight = 16.sp)
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(containerColor = SignalBlack),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Pilih PDF lain")
            }
        }
    }
}

@Composable
private fun DocumentSuccessState(
    state: DocumentState.Success,
    mode: DocumentMode,
    searchQuery: String,
    onModeChange: (DocumentMode) -> Unit,
    onSearchChange: (String) -> Unit
) {
    val result = state.result

    DocumentFileCard(
        fileName = state.fileName,
        fileSize = state.fileSize,
        analyzedPages = result.analyzedPages,
        totalPages = result.totalPages
    )

    Spacer(Modifier.height(14.dp))
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        DocumentTool(
            icon = Icons.Outlined.Summarize,
            text = "Ringkas",
            active = mode == DocumentMode.SUMMARY,
            modifier = Modifier.weight(1f),
            onClick = { onModeChange(DocumentMode.SUMMARY) }
        )
        DocumentTool(
            icon = Icons.Outlined.Search,
            text = "Cari Info",
            active = mode == DocumentMode.SEARCH,
            modifier = Modifier.weight(1f),
            onClick = { onModeChange(DocumentMode.SEARCH) }
        )
        DocumentTool(
            icon = Icons.Outlined.CheckBox,
            text = "Jadikan Tugas",
            active = mode == DocumentMode.TASKS,
            modifier = Modifier.weight(1f),
            onClick = { onModeChange(DocumentMode.TASKS) }
        )
    }

    Spacer(Modifier.height(22.dp))

    when (mode) {
        DocumentMode.SUMMARY -> SummaryPanel(result)
        DocumentMode.SEARCH -> SearchPanel(
            result = result,
            searchQuery = searchQuery,
            onSearchChange = onSearchChange
        )
        DocumentMode.TASKS -> TasksPanel(result)
    }
}

@Composable
private fun DocumentFileCard(
    fileName: String,
    fileSize: Long?,
    analyzedPages: Int,
    totalPages: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(58.dp)
                    .background(SignalRed.copy(alpha = .09f), RoundedCornerShape(15.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.Description, null, tint = SignalRed, modifier = Modifier.size(30.dp))
            }
            Column(modifier = Modifier.padding(start = 14.dp).weight(1f)) {
                Text(fileName, fontWeight = FontWeight.Bold, color = SignalBlack, maxLines = 2)
                val sizeText = fileSize?.let(::formatBytes)?.plus(" · ").orEmpty()
                Text(
                    "$sizeText$analyzedPages/$totalPages halaman dianalisis",
                    color = SignalMuted,
                    fontSize = 11.sp
                )
            }
            Text("PDF", color = SignalRed, fontWeight = FontWeight.Bold, fontSize = 11.sp)
        }
    }
}

@Composable
private fun SummaryPanel(result: LocalDocumentResult) {
    Text("Ringkasan lokal", fontWeight = FontWeight.Bold, color = SignalBlack, fontSize = 16.sp)
    Spacer(Modifier.height(10.dp))
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Text(
            result.summary,
            modifier = Modifier.padding(18.dp),
            color = SignalBlack,
            fontSize = 13.sp,
            lineHeight = 20.sp
        )
    }

    Spacer(Modifier.height(16.dp))
    Text("Kata kunci", fontWeight = FontWeight.Bold, color = SignalBlack, fontSize = 14.sp)
    Spacer(Modifier.height(7.dp))
    Text(
        if (result.keywords.isEmpty()) "Belum ada kata kunci yang cukup kuat."
        else result.keywords.joinToString("  •  "),
        color = SignalMuted,
        fontSize = 11.sp,
        lineHeight = 17.sp
    )

    Spacer(Modifier.height(16.dp))
    Text(
        "${result.wordCount} kata · ${result.characterCount} karakter · ringkasan bersifat ekstraktif dan tidak menambah fakta baru.",
        color = SignalMuted,
        fontSize = 10.sp,
        lineHeight = 15.sp
    )
}

@Composable
private fun SearchPanel(
    result: LocalDocumentResult,
    searchQuery: String,
    onSearchChange: (String) -> Unit
) {
    Text("Cari di dokumen", fontWeight = FontWeight.Bold, color = SignalBlack, fontSize = 16.sp)
    Spacer(Modifier.height(10.dp))
    OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text("Contoh: pendapatan, target, risiko…") },
        singleLine = true,
        leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
        shape = RoundedCornerShape(15.dp)
    )

    val results = remember(result.fullText, searchQuery) {
        LocalDocumentAnalyzer.search(result.fullText, searchQuery)
    }

    Spacer(Modifier.height(12.dp))
    when {
        searchQuery.trim().length < 2 -> Text(
            "Ketik minimal 2 karakter. Pencarian dilakukan langsung pada teks hasil OCR.",
            color = SignalMuted,
            fontSize = 11.sp
        )
        results.isEmpty() -> Text(
            "Tidak ditemukan potongan teks yang memuat “${searchQuery.trim()}”.",
            color = SignalMuted,
            fontSize = 11.sp
        )
        else -> results.forEachIndexed { index, text ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 9.dp),
                shape = RoundedCornerShape(15.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.Top) {
                    Text(
                        "%02d".format(index + 1),
                        color = SignalRed,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                    Text(
                        text,
                        modifier = Modifier.padding(start = 10.dp),
                        color = SignalBlack,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun TasksPanel(result: LocalDocumentResult) {
    Text("Calon tindak lanjut", fontWeight = FontWeight.Bold, color = SignalBlack, fontSize = 16.sp)
    Spacer(Modifier.height(6.dp))
    Text(
        "Signal555 mencari kalimat yang mengandung pola target, kewajiban, rencana, atau tindak lanjut. Periksa kembali konteks aslinya.",
        color = SignalMuted,
        fontSize = 11.sp,
        lineHeight = 16.sp
    )
    Spacer(Modifier.height(12.dp))

    if (result.tasks.isEmpty()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Text(
                "Belum ada kalimat yang cukup jelas untuk dijadikan calon tugas.",
                modifier = Modifier.padding(16.dp),
                color = SignalMuted,
                fontSize = 11.sp
            )
        }
    } else {
        result.tasks.forEachIndexed { index, task ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 9.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.Top) {
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .background(SignalRed.copy(alpha = .09f), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("${index + 1}", color = SignalRed, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                    }
                    Text(
                        task,
                        modifier = Modifier.padding(start = 10.dp),
                        color = SignalBlack,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun DocumentTool(
    icon: ImageVector,
    text: String,
    active: Boolean,
    modifier: Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = if (active) SignalRed else Color.White),
        border = if (active) null else BorderStroke(1.dp, SignalBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, null, tint = if (active) Color.White else SignalBlack, modifier = Modifier.size(20.dp))
            Spacer(Modifier.height(5.dp))
            Text(
                text,
                fontSize = 10.sp,
                color = if (active) Color.White else SignalBlack,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

private suspend fun extractPdfText(
    context: Context,
    uri: Uri,
    recognizer: TextRecognizer,
    onProgress: (currentPage: Int, totalPages: Int) -> Unit
): PdfExtraction {
    val descriptor = withContext(Dispatchers.IO) {
        context.contentResolver.openFileDescriptor(uri, "r")
            ?: error("PDF tidak dapat dibuka.")
    }

    descriptor.use { fileDescriptor ->
        PdfRenderer(fileDescriptor).use { renderer ->
            val totalPages = renderer.pageCount
            if (totalPages <= 0) error("PDF tidak memiliki halaman.")

            val pageLimit = minOf(totalPages, MAX_DOCUMENT_PAGES)
            onProgress(0, totalPages)
            val pageTexts = ArrayList<String>(pageLimit)

            for (index in 0 until pageLimit) {
                val bitmap = withContext(Dispatchers.IO) {
                    renderer.openPage(index).use { page ->
                        val scale = minOf(
                            2f,
                            1800f / page.width.toFloat(),
                            2400f / page.height.toFloat()
                        ).coerceAtLeast(0.5f)

                        val width = (page.width * scale).roundToInt().coerceAtLeast(1)
                        val height = (page.height * scale).roundToInt().coerceAtLeast(1)
                        Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).also { output ->
                            Canvas(output).drawColor(AndroidColor.WHITE)
                            page.render(
                                output,
                                null,
                                null,
                                PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY
                            )
                        }
                    }
                }

                try {
                    val pageText = recognizer.processAwait(InputImage.fromBitmap(bitmap, 0)).trim()
                    if (pageText.isNotBlank()) {
                        pageTexts += "[Halaman ${index + 1}]\n$pageText"
                    }
                } finally {
                    bitmap.recycle()
                }

                onProgress(index + 1, totalPages)
            }

            return PdfExtraction(
                text = pageTexts.joinToString("\n\n"),
                analyzedPages = pageLimit,
                totalPages = totalPages
            )
        }
    }
}

private suspend fun TextRecognizer.processAwait(image: InputImage): String =
    suspendCancellableCoroutine { continuation ->
        process(image)
            .addOnSuccessListener { visionText ->
                if (continuation.isActive) continuation.resume(visionText.text)
            }
            .addOnFailureListener { error ->
                if (continuation.isActive) continuation.resumeWithException(error)
            }
    }

private fun readDocumentMetadata(context: Context, uri: Uri): Pair<String?, Long?> {
    return runCatching {
        context.contentResolver.query(
            uri,
            arrayOf(OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE),
            null,
            null,
            null
        )?.use { cursor ->
            if (!cursor.moveToFirst()) return@use null to null
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
            val name = if (nameIndex >= 0) cursor.getString(nameIndex) else null
            val size = if (sizeIndex >= 0 && !cursor.isNull(sizeIndex)) cursor.getLong(sizeIndex) else null
            name to size
        } ?: (null to null)
    }.getOrDefault(null to null)
}

private fun formatBytes(bytes: Long): String = when {
    bytes >= 1024L * 1024L -> String.format("%.1f MB", bytes / (1024f * 1024f))
    bytes >= 1024L -> String.format("%.0f KB", bytes / 1024f)
    else -> "$bytes B"
}
