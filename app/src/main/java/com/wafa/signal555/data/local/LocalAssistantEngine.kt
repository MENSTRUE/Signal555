package com.wafa.signal555.data.local

import java.util.Locale

enum class AssistantIntent {
    GREETING,
    HELP,
    LATEST_CHECK,
    HISTORY,
    EXPLAIN_SCORE,
    LINK,
    SCREENSHOT,
    CAMERA,
    DOCUMENT,
    SAFE_ACTION,
    UNKNOWN
}

data class LocalAssistantReply(
    val intent: AssistantIntent,
    val text: String,
    val usedHistory: Boolean
)

/**
 * Offline contextual assistant for the local MVP.
 *
 * This is not an LLM. It uses deterministic intent detection + templates +
 * local check history so the assistant can answer useful follow-up questions
 * without an API or network connection.
 */
object LocalAssistantEngine {

    fun reply(
        prompt: String,
        history: List<LocalHistoryItem>
    ): LocalAssistantReply {
        val clean = prompt.trim()
        val normalized = clean.lowercase(Locale.ROOT)
        val intent = detectIntent(normalized)
        val latest = history.maxByOrNull { it.createdAt }

        val text = when (intent) {
            AssistantIntent.GREETING -> greeting(latest)
            AssistantIntent.HELP -> helpText()
            AssistantIntent.LATEST_CHECK -> latestCheck(latest)
            AssistantIntent.HISTORY -> historySummary(history)
            AssistantIntent.EXPLAIN_SCORE -> explainScore(latest)
            AssistantIntent.LINK -> linkAdvice(latest)
            AssistantIntent.SCREENSHOT -> screenshotAdvice(latest)
            AssistantIntent.CAMERA -> cameraAdvice(latest)
            AssistantIntent.DOCUMENT -> documentAdvice(latest)
            AssistantIntent.SAFE_ACTION -> safeAction(latest)
            AssistantIntent.UNKNOWN -> fallback(clean, latest)
        }

        return LocalAssistantReply(
            intent = intent,
            text = text,
            usedHistory = latest != null && intent !in setOf(
                AssistantIntent.HELP,
                AssistantIntent.GREETING
            )
        )
    }

    private fun detectIntent(text: String): AssistantIntent {
        if (text.isBlank()) return AssistantIntent.HELP

        if (hasAny(text, "halo", "hai", "hi", "hello", "pagi", "siang", "malam")) {
            return AssistantIntent.GREETING
        }
        if (hasAny(text, "bisa apa", "fitur", "bantu apa", "help", "panduan")) {
            return AssistantIntent.HELP
        }
        if (hasAny(text, "terakhir", "check terakhir", "cek terakhir", "hasil terakhir")) {
            return AssistantIntent.LATEST_CHECK
        }
        if (hasAny(text, "riwayat", "history", "semua check", "semua cek")) {
            return AssistantIntent.HISTORY
        }
        if (hasAny(text, "skor", "score", "kenapa tinggi", "kenapa rendah", "kenapa risiko")) {
            return AssistantIntent.EXPLAIN_SCORE
        }
        if (hasAny(text, "link", "url", "domain", "tautan", "website")) {
            return AssistantIntent.LINK
        }
        if (hasAny(text, "screenshot", "chat", "pesan", "whatsapp", "sms")) {
            return AssistantIntent.SCREENSHOT
        }
        if (hasAny(text, "kamera", "camera", "foto langsung", "scan kamera")) {
            return AssistantIntent.CAMERA
        }
        if (hasAny(text, "pdf", "dokumen", "document", "ringkasan", "summary")) {
            return AssistantIntent.DOCUMENT
        }
        if (hasAny(
                text,
                "harus bagaimana", "harus gimana", "sebaiknya", "aman nggak", "aman gak",
                "boleh klik", "boleh transfer", "sudah terlanjur", "apa yang harus"
            )
        ) {
            return AssistantIntent.SAFE_ACTION
        }
        return AssistantIntent.UNKNOWN
    }

    private fun greeting(latest: LocalHistoryItem?): String {
        return if (latest == null) {
            "Halo. Saya berjalan sepenuhnya lokal di perangkat. Kamu bisa tanya tentang hasil Screenshot, Link, Camera, atau Document Check."
        } else {
            "Halo. Saya siap membantu membaca konteks lokalmu. Check terakhir: ${latest.title} (${latest.status}${scoreSuffix(latest)})."
        }
    }

    private fun helpText(): String = buildString {
        append("Saya bisa membantu dari data yang sudah ada di perangkat. Coba tanya:\n\n")
        append("• \"Hasil check terakhir bagaimana?\"\n")
        append("• \"Kenapa skornya tinggi?\"\n")
        append("• \"Apa yang harus saya lakukan?\"\n")
        append("• \"Ringkas riwayat check saya\"\n")
        append("• \"Apa yang perlu dicek dari sebuah link?\"\n\n")
        append("Catatan: assistant ini offline dan belum memakai LLM generatif.")
    }

    private fun latestCheck(item: LocalHistoryItem?): String {
        if (item == null) return noHistory()
        return buildString {
            append("Check terakhirmu adalah ${typeLabel(item.type)}.\n\n")
            append("${item.title}\n")
            append("Status: ${item.status}")
            item.score?.let { append("\nRisk indicator: $it/100") }
            append("\n\nGunakan hasil ini sebagai indikator, bukan kepastian. Verifikasi identitas, domain, dan permintaan sensitif sebelum bertindak.")
        }
    }

    private fun historySummary(history: List<LocalHistoryItem>): String {
        if (history.isEmpty()) return noHistory()

        val ordered = history.sortedByDescending { it.createdAt }
        val scored = ordered.filter { it.score != null }
        val high = scored.count { (it.score ?: 0) >= 60 }
        val medium = scored.count { (it.score ?: 0) in 30..59 }
        val low = scored.count { (it.score ?: 0) < 30 }

        val types = ordered.groupingBy { typeLabel(it.type) }.eachCount()
            .entries
            .sortedByDescending { it.value }
            .joinToString(", ") { "${it.key}: ${it.value}" }

        return buildString {
            append("Ada ${ordered.size} hasil check tersimpan lokal.\n\n")
            append("Jenis: $types.\n")
            if (scored.isNotEmpty()) {
                append("Risk indicator: $high tinggi, $medium sedang, $low rendah.\n")
            }
            append("\nTerbaru: ${ordered.first().title} (${ordered.first().status}${scoreSuffix(ordered.first())}).")
        }
    }

    private fun explainScore(item: LocalHistoryItem?): String {
        if (item == null) return noHistory()
        val score = item.score ?: return "Check terakhir tidak memiliki risk score. Untuk Document Check, fokusnya adalah ekstraksi/ringkasan konten, bukan penilaian risiko."

        val meaning = when (score) {
            in 0..29 -> "sedikit pola risiko lokal yang terdeteksi"
            in 30..59 -> "ada beberapa pola yang perlu diverifikasi"
            else -> "banyak pola yang layak dianggap peringatan"
        }

        return "Skor $score/100 berarti $meaning. Skor ini berasal dari pola lokal seperti urgensi, permintaan data sensitif, pembayaran, tautan, atau pola URL. Ini bukan vonis bahwa konten pasti aman atau pasti penipuan."
    }

    private fun linkAdvice(latest: LocalHistoryItem?): String {
        val context = latest?.takeIf { it.type == "link" }
        return buildString {
            if (context != null) {
                append("Link terakhir: ${context.title}\nStatus: ${context.status}${scoreSuffix(context)}.\n\n")
            }
            append("Sebelum membuka link, cek domain utama, HTTPS, typo/punycode, shortener, subdomain berlebihan, kata seperti login/verify, dan apakah pesan mendorongmu bertindak terburu-buru. Jika mengatasnamakan layanan tertentu, buka situs/app resminya secara manual.")
        }
    }

    private fun screenshotAdvice(latest: LocalHistoryItem?): String {
        val context = latest?.takeIf { it.type == "screenshot" }
        return buildString {
            if (context != null) {
                append("Screenshot check terakhir: ${context.status}${scoreSuffix(context)}.\n\n")
            }
            append("Untuk screenshot pesan, fokus pada identitas pengirim, urgensi, permintaan OTP/PIN, pembayaran, hadiah, dan link eksternal. OCR lokal bisa salah baca, jadi lihat juga gambar aslinya sebelum mengambil keputusan.")
        }
    }

    private fun cameraAdvice(latest: LocalHistoryItem?): String {
        val context = latest?.takeIf { it.type == "camera" }
        return buildString {
            if (context != null) {
                append("Camera check terakhir: ${context.status}${scoreSuffix(context)}.\n\n")
            }
            append("Camera Assist membaca teks dari foto secara lokal. Pastikan teks tajam, cukup terang, dan tidak miring. Setelah OCR, risk engine memeriksa pola teks yang terdeteksi.")
        }
    }

    private fun documentAdvice(latest: LocalHistoryItem?): String {
        val context = latest?.takeIf { it.type == "document" }
        return buildString {
            if (context != null) {
                append("Document terakhir: ${context.title}.\nStatus: ${context.status}.\n\n")
            }
            append("Document AI saat ini melakukan OCR dan ringkasan ekstraktif secara lokal. Untuk PDF panjang, hasil terbaik datang dari dokumen dengan teks yang jelas dan tidak terlalu banyak halaman scan buram.")
        }
    }

    private fun safeAction(latest: LocalHistoryItem?): String {
        if (latest == null) {
            return "Kalau belum ada check, masukkan screenshot/link/foto/dokumen dulu. Untuk situasi mencurigakan: jangan klik link, jangan kirim OTP/PIN, jangan transfer, lalu verifikasi lewat kanal resmi yang kamu buka sendiri."
        }

        val score = latest.score
        val action = when {
            score == null -> "Periksa sumber dokumen dan konfirmasi informasi penting dari sumber resmi."
            score >= 60 -> "Jangan lanjut klik, transfer, atau memberikan data. Verifikasi lewat aplikasi/situs/nomor resmi yang kamu cari sendiri."
            score >= 30 -> "Tunda tindakan sampai identitas pengirim dan tujuan permintaan terverifikasi."
            else -> "Tidak banyak pola risiko terdeteksi, tetapi tetap verifikasi identitas dan domain sebelum tindakan sensitif."
        }

        return "Berdasarkan check terakhir (${latest.status}${scoreSuffix(latest)}): $action"
    }

    private fun fallback(prompt: String, latest: LocalHistoryItem?): String {
        val context = latest?.let {
            "\n\nKonteks lokal terakhir: ${it.title} — ${it.status}${scoreSuffix(it)}."
        }.orEmpty()

        return "Saya belum bisa menghasilkan jawaban bebas seperti LLM. Saya tetap bisa membantu secara offline untuk konteks keamanan digital, hasil check, skor risiko, link, screenshot, kamera, dokumen, dan tindakan aman.$context\n\nCoba ubah pertanyaan \"$prompt\" menjadi lebih spesifik, misalnya: \"kenapa skor terakhir tinggi?\" atau \"apa yang harus saya lakukan?\""
    }

    private fun typeLabel(type: String): String = when (type) {
        "link" -> "Link Check"
        "screenshot" -> "Screenshot Check"
        "camera" -> "Camera Assist"
        "document" -> "Document AI"
        else -> "Check"
    }

    private fun scoreSuffix(item: LocalHistoryItem): String = item.score?.let { " • $it/100" }.orEmpty()

    private fun noHistory(): String =
        "Belum ada hasil check lokal. Jalankan Screenshot Check, Link Check, Camera Assist, atau Document AI dulu supaya saya punya konteks untuk dijelaskan."

    private fun hasAny(text: String, vararg phrases: String): Boolean =
        phrases.any { text.contains(it) }
}
