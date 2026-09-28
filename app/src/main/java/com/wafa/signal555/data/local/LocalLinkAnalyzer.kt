package com.wafa.signal555.data.local

import java.net.IDN
import java.net.URI
import java.util.Locale

data class LinkSignal(
    val title: String,
    val detail: String,
    val points: Int
)

data class LocalLinkResult(
    val normalizedUrl: String,
    val host: String,
    val score: Int,
    val level: RiskLevel,
    val summary: String,
    val signals: List<LinkSignal>
)

/**
 * Offline URL heuristics. This is a risk indicator, not a malware verdict.
 * It deliberately avoids network lookups so the check can run without INTERNET permission.
 */
object LocalLinkAnalyzer {

    private val shorteners = setOf(
        "bit.ly", "tinyurl.com", "t.co", "s.id", "cutt.ly", "rb.gy", "is.gd", "shorturl.at"
    )

    private val riskyTlds = setOf(
        "xyz", "top", "click", "site", "online", "live", "buzz", "work", "support"
    )

    private val lureWords = listOf(
        "login", "signin", "verify", "verification", "verifikasi", "secure", "security",
        "account", "akun", "claim", "klaim", "hadiah", "bonus", "reward", "prize",
        "update", "wallet", "payment", "bank"
    )

    fun analyze(rawUrl: String): LocalLinkResult {
        val input = rawUrl.trim()
        if (input.isBlank()) {
            return invalid(input, "Masukkan tautan yang ingin diperiksa.")
        }

        val normalized = normalize(input)
        val uri = runCatching { URI(normalized) }.getOrNull()
            ?: return invalid(normalized, "Format tautan tidak dapat dibaca.")

        val hostRaw = uri.host?.trim()?.lowercase(Locale.ROOT)
            ?: return invalid(normalized, "Domain tidak ditemukan pada tautan ini.")

        val host = runCatching { IDN.toUnicode(hostRaw) }.getOrDefault(hostRaw)
        val asciiHost = hostRaw
        val signals = mutableListOf<LinkSignal>()

        if (uri.scheme.equals("http", ignoreCase = true)) {
            signals += LinkSignal(
                "Tidak menggunakan HTTPS",
                "Koneksi HTTP tidak mengenkripsi trafik seperti HTTPS.",
                20
            )
        }

        if (uri.userInfo != null || normalized.substringBefore("//", "").contains("@")) {
            signals += LinkSignal(
                "URL memuat identitas sebelum domain",
                "Pola user@domain dapat membuat domain sebenarnya lebih sulit dibaca.",
                25
            )
        }

        if (isIpAddress(asciiHost)) {
            signals += LinkSignal(
                "Menggunakan alamat IP langsung",
                "Layanan publik biasanya lebih mudah diverifikasi melalui nama domain resmi.",
                25
            )
        }

        if (asciiHost.contains("xn--")) {
            signals += LinkSignal(
                "Domain memakai Punycode",
                "Karakter internasional pada domain dapat menyerupai huruf dari domain lain. Periksa ejaannya dengan teliti.",
                20
            )
        }

        if (shorteners.any { asciiHost == it || asciiHost.endsWith(".$it") }) {
            signals += LinkSignal(
                "Tautan dipendekkan",
                "Shortener menyembunyikan tujuan akhir sampai tautan dibuka.",
                20
            )
        }

        val tld = asciiHost.substringAfterLast('.', missingDelimiterValue = "")
        if (tld in riskyTlds) {
            signals += LinkSignal(
                "TLD perlu perhatian",
                ".$tld sering dipakai untuk situs sementara; ini bukan bukti penipuan, tetapi layak diverifikasi.",
                16
            )
        }

        val hostLabels = asciiHost.split('.').filter { it.isNotBlank() }
        if (hostLabels.size >= 5) {
            signals += LinkSignal(
                "Subdomain sangat panjang",
                "Banyak lapisan subdomain dapat menyulitkan pembacaan domain utama.",
                12
            )
        }

        if (asciiHost.count { it == '-' } >= 3) {
            signals += LinkSignal(
                "Banyak tanda hubung pada domain",
                "Domain dengan banyak pemisah dapat digunakan untuk meniru nama layanan yang familiar.",
                10
            )
        }

        val searchable = buildString {
            append(asciiHost)
            append(' ')
            append(uri.rawPath.orEmpty().lowercase(Locale.ROOT))
            append(' ')
            append(uri.rawQuery.orEmpty().lowercase(Locale.ROOT))
        }
        val matchedLures = lureWords.count { searchable.contains(it) }
        if (matchedLures >= 2) {
            signals += LinkSignal(
                "Banyak kata pemicu sensitif",
                "URL memuat beberapa kata seperti login, verifikasi, hadiah, akun, atau pembayaran.",
                if (matchedLures >= 4) 22 else 14
            )
        } else if (matchedLures == 1) {
            signals += LinkSignal(
                "Ada kata yang perlu diverifikasi",
                "URL memuat istilah sensitif seperti login, verifikasi, akun, hadiah, atau pembayaran.",
                7
            )
        }

        if (uri.port != -1 && uri.port !in setOf(80, 443)) {
            signals += LinkSignal(
                "Menggunakan port non-standar",
                "Port ${uri.port} bukan port web umum 80/443. Pastikan sumber tautan dapat dipercaya.",
                10
            )
        }

        if (normalized.length > 120) {
            signals += LinkSignal(
                "URL sangat panjang",
                "Tautan panjang dapat menyembunyikan parameter atau tujuan yang sulit diperiksa sekilas.",
                8
            )
        }

        val score = signals.sumOf { it.points }.coerceIn(0, 100)
        val level = when {
            score >= 60 -> RiskLevel.HIGH
            score >= 30 -> RiskLevel.MEDIUM
            else -> RiskLevel.LOW
        }

        val summary = when (level) {
            RiskLevel.HIGH -> "Banyak indikator perlu perhatian. Jangan login, transfer, atau memasukkan data sebelum memverifikasi domain melalui kanal resmi."
            RiskLevel.MEDIUM -> "Ada beberapa pola yang layak diperiksa sebelum membuka atau memasukkan data pada tautan ini."
            RiskLevel.LOW -> "Tidak banyak pola risiko yang terdeteksi secara lokal. Ini bukan jaminan situs aman; tetap cocokkan domain dengan sumber resmi."
        }

        return LocalLinkResult(
            normalizedUrl = normalized,
            host = host,
            score = score,
            level = level,
            summary = summary,
            signals = signals
        )
    }

    private fun normalize(value: String): String {
        val cleaned = value.replace("\\s".toRegex(), "")
        return if (cleaned.startsWith("http://", true) || cleaned.startsWith("https://", true)) {
            cleaned
        } else {
            "https://$cleaned"
        }
    }

    private fun invalid(url: String, message: String) = LocalLinkResult(
        normalizedUrl = url,
        host = "-",
        score = 0,
        level = RiskLevel.LOW,
        summary = message,
        signals = emptyList()
    )

    private fun isIpAddress(host: String): Boolean {
        val ipv4 = Regex("^(?:\\d{1,3}\\.){3}\\d{1,3}$")
        if (ipv4.matches(host)) {
            return host.split('.').all { it.toIntOrNull() in 0..255 }
        }
        return host.contains(':') // URI.host strips [] for IPv6.
    }
}
