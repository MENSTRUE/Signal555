package com.wafa.signal555.data.local

import java.util.Locale

enum class RiskLevel {
    LOW,
    MEDIUM,
    HIGH
}

data class RiskSignal(
    val title: String,
    val detail: String,
    val points: Int
)

data class LocalRiskResult(
    val score: Int,
    val level: RiskLevel,
    val summary: String,
    val signals: List<RiskSignal>,
    val detectedLinks: List<String>
)

/**
 * Explainable local risk engine.
 *
 * This is intentionally deterministic and offline. It does NOT claim that a
 * message is certainly fraudulent. It highlights patterns that deserve user
 * verification before clicking, paying, or sharing sensitive information.
 */
object LocalRiskAnalyzer {

    private val urlRegex = Regex(
        pattern = "(?i)(https?://[^\\s]+|www\\.[^\\s]+|(?:[a-z0-9-]+\\.)+(?:com|net|org|id|co\\.id|xyz|site|online|top|click|shop|info)(?:/[^\\s]*)?)"
    )

    private val urgencyWords = listOf(
        "segera", "sekarang", "hari ini", "batas waktu", "kedaluwarsa",
        "kadaluarsa", "terakhir", "10 menit", "5 menit", "1 jam",
        "akun diblokir", "akun akan diblokir", "urgent", "immediately",
        "right now", "expires", "last chance"
    )

    private val sensitiveWords = listOf(
        "otp", "kode verifikasi", "verification code", "pin", "password",
        "kata sandi", "cvv", "nomor kartu", "card number", "kode rahasia",
        "data pribadi", "nik", "nomor ktp"
    )

    private val moneyWords = listOf(
        "transfer", "bayar", "pembayaran", "rekening", "biaya admin",
        "biaya administrasi", "deposit", "uang muka", "dana", "wallet",
        "payment", "bank account"
    )

    private val rewardWords = listOf(
        "hadiah", "pemenang", "menang", "bonus", "gratis", "undian",
        "giveaway", "reward", "prize", "winner", "free gift"
    )

    private val authorityWords = listOf(
        "bank", "bca", "bri", "bni", "mandiri", "dana", "ovo", "gopay",
        "shopee", "tokopedia", "polisi", "pajak", "komdigi", "ojk",
        "customer service", "admin resmi", "official"
    )

    private val pressureWords = listOf(
        "jangan beri tahu", "rahasia", "jangan hubungi", "jangan telepon",
        "hanya melalui chat", "only contact", "do not tell", "keep this secret"
    )

    fun analyze(rawText: String): LocalRiskResult {
        val text = rawText.trim()
        val normalized = text.lowercase(Locale.ROOT)
        val signals = mutableListOf<RiskSignal>()
        val links = urlRegex.findAll(text)
            .map { it.value.trimEnd('.', ',', ')', ']', '}', '!', '?', ';', ':') }
            .distinct()
            .toList()

        if (text.isBlank()) {
            return LocalRiskResult(
                score = 0,
                level = RiskLevel.LOW,
                summary = "Tidak ada teks yang cukup untuk dianalisis.",
                signals = emptyList(),
                detectedLinks = emptyList()
            )
        }

        if (containsAny(normalized, urgencyWords)) {
            signals += RiskSignal(
                title = "Ada unsur mendesak",
                detail = "Pesan mendorong Anda bertindak cepat sebelum sempat memverifikasi.",
                points = 20
            )
        }

        if (containsAny(normalized, sensitiveWords)) {
            signals += RiskSignal(
                title = "Meminta data sensitif",
                detail = "Terdeteksi permintaan OTP, PIN, kata sandi, atau data identitas sensitif.",
                points = 30
            )
        }

        if (containsAny(normalized, moneyWords)) {
            signals += RiskSignal(
                title = "Berkaitan dengan uang atau pembayaran",
                detail = "Pesan menyebut transfer, rekening, biaya, atau pembayaran.",
                points = 18
            )
        }

        if (containsAny(normalized, rewardWords)) {
            signals += RiskSignal(
                title = "Menawarkan hadiah atau keuntungan",
                detail = "Imbalan, hadiah, atau status pemenang dapat digunakan untuk memancing tindakan.",
                points = 18
            )
        }

        if (links.isNotEmpty()) {
            val suspiciousLink = links.any(::looksSuspicious)
            signals += RiskSignal(
                title = if (suspiciousLink) "Terdapat tautan mencurigakan" else "Terdapat tautan eksternal",
                detail = if (suspiciousLink) {
                    "Pola domain atau URL perlu diverifikasi sebelum dibuka."
                } else {
                    "Pastikan domain benar-benar milik layanan yang dimaksud sebelum membukanya."
                },
                points = if (suspiciousLink) 25 else 10
            )
        }

        if (containsAny(normalized, pressureWords)) {
            signals += RiskSignal(
                title = "Mendorong Anda untuk tidak memverifikasi",
                detail = "Pesan meminta kerahasiaan atau membatasi cara Anda menghubungi pihak lain.",
                points = 25
            )
        }

        if (containsAny(normalized, authorityWords) &&
            (containsAny(normalized, urgencyWords) || links.isNotEmpty() || containsAny(normalized, sensitiveWords))
        ) {
            signals += RiskSignal(
                title = "Mengatasnamakan layanan atau otoritas",
                detail = "Identitas pengirim belum dapat dibuktikan hanya dari isi screenshot.",
                points = 12
            )
        }

        if (text.count { it == '!' } >= 3 || text.uppercase(Locale.ROOT) == text && text.length > 30) {
            signals += RiskSignal(
                title = "Bahasa sangat menekan",
                detail = "Penggunaan huruf kapital atau tanda seru berulang dapat menjadi pola tekanan sosial.",
                points = 8
            )
        }

        val score = signals.sumOf { it.points }.coerceIn(0, 100)
        val level = when {
            score >= 60 -> RiskLevel.HIGH
            score >= 30 -> RiskLevel.MEDIUM
            else -> RiskLevel.LOW
        }

        val summary = when (level) {
            RiskLevel.HIGH -> "Banyak pola berisiko ditemukan. Jangan klik, transfer, atau membagikan data sebelum verifikasi melalui kanal resmi."
            RiskLevel.MEDIUM -> "Ada beberapa pola yang perlu diverifikasi sebelum Anda bertindak."
            RiskLevel.LOW -> "Tidak banyak pola risiko yang terdeteksi, tetapi tetap verifikasi identitas pengirim dan tujuan tautan."
        }

        return LocalRiskResult(
            score = score,
            level = level,
            summary = summary,
            signals = signals,
            detectedLinks = links
        )
    }

    private fun containsAny(text: String, words: List<String>): Boolean =
        words.any { text.contains(it) }

    private fun looksSuspicious(url: String): Boolean {
        val value = url.lowercase(Locale.ROOT)
        val riskyTlds = listOf(".xyz", ".top", ".click", ".site", ".online")
        val shorteners = listOf("bit.ly", "tinyurl.com", "t.co", "s.id", "cutt.ly")
        val lureWords = listOf("login", "verify", "verifikasi", "claim", "klaim", "hadiah", "bonus", "secure", "account")

        return riskyTlds.any { value.contains(it) } ||
            shorteners.any { value.contains(it) } ||
            lureWords.count { value.contains(it) } >= 2 ||
            value.count { it == '-' } >= 3
    }
}
