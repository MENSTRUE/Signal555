package com.wafa.signal555.data.local

import java.util.Locale

data class LocalDocumentResult(
    val summary: String,
    val keywords: List<String>,
    val tasks: List<String>,
    val fullText: String,
    val wordCount: Int,
    val characterCount: Int,
    val analyzedPages: Int,
    val totalPages: Int
)

/**
 * Lightweight, fully local document helper for the MVP.
 *
 * This is deliberately deterministic and explainable. It performs extractive
 * summarization, keyword frequency ranking, simple action-item extraction and
 * local text search. It is not a generative LLM and does not invent content.
 */
object LocalDocumentAnalyzer {

    private val stopWords = setOf(
        // Indonesian
        "yang", "dan", "atau", "dari", "untuk", "pada", "dengan", "dalam", "ini", "itu",
        "adalah", "sebagai", "oleh", "ke", "di", "akan", "telah", "juga", "tidak", "dapat",
        "serta", "karena", "agar", "lebih", "antara", "terhadap", "para", "suatu", "setiap",
        "tersebut", "menjadi", "merupakan", "sudah", "masih", "namun", "sehingga", "bahwa",
        "maka", "tentang", "hingga", "bagi", "saat", "tahun", "bulan", "hari", "kami", "kita",
        "mereka", "anda", "saya", "dia", "ia", "nya",
        // English
        "the", "and", "or", "of", "to", "in", "for", "on", "with", "this", "that", "is",
        "are", "was", "were", "be", "been", "as", "by", "from", "at", "an", "a", "it", "its",
        "will", "has", "have", "had", "can", "could", "should", "would", "not", "but", "than",
        "into", "about", "over", "under", "between", "during", "through", "we", "you", "they"
    )

    private val taskCues = listOf(
        "harus", "perlu", "wajib", "sebaiknya", "direkomendasikan", "ditargetkan", "target",
        "rencana", "direncanakan", "akan dilakukan", "tindak lanjut", "deadline", "batas waktu",
        "aksi", "action", "must", "need to", "needs to", "should", "recommended", "plan",
        "planned", "target", "follow up", "follow-up", "deadline", "required"
    )

    fun analyze(
        rawText: String,
        analyzedPages: Int,
        totalPages: Int
    ): LocalDocumentResult {
        val cleanText = normalize(rawText)
        val sentences = splitSentences(cleanText)
        val words = tokenize(cleanText)
        val frequencies = words
            .filterNot(stopWords::contains)
            .filter { it.length >= 3 }
            .groupingBy { it }
            .eachCount()

        val summary = buildSummary(sentences, frequencies)
        val keywords = frequencies.entries
            .sortedWith(compareByDescending<Map.Entry<String, Int>> { it.value }.thenBy { it.key })
            .map { it.key }
            .distinct()
            .take(10)

        val tasks = sentences
            .filter { sentence ->
                val lower = sentence.lowercase(Locale.ROOT)
                taskCues.any(lower::contains)
            }
            .map(::trimSentence)
            .filter { it.length >= 18 }
            .distinct()
            .take(6)

        return LocalDocumentResult(
            summary = summary,
            keywords = keywords,
            tasks = tasks,
            fullText = cleanText,
            wordCount = words.size,
            characterCount = cleanText.length,
            analyzedPages = analyzedPages,
            totalPages = totalPages
        )
    }

    fun search(fullText: String, query: String, maxResults: Int = 8): List<String> {
        val needle = query.trim().lowercase(Locale.ROOT)
        if (needle.length < 2) return emptyList()

        return splitSentences(fullText)
            .filter { it.lowercase(Locale.ROOT).contains(needle) }
            .map(::trimSentence)
            .distinct()
            .take(maxResults)
    }

    private fun buildSummary(
        sentences: List<String>,
        frequencies: Map<String, Int>
    ): String {
        if (sentences.isEmpty()) {
            return "Tidak ada teks yang cukup untuk diringkas dari dokumen ini."
        }

        if (sentences.size <= 3) {
            return sentences.joinToString(" ") { trimSentence(it) }
        }

        val scored = sentences.mapIndexed { index, sentence ->
            val tokens = tokenize(sentence).filterNot(stopWords::contains)
            val lexicalScore = tokens.sumOf { frequencies[it] ?: 0 }.toDouble()
            val normalizedScore = if (tokens.isEmpty()) 0.0 else lexicalScore / tokens.size
            val lengthBonus = when (tokens.size) {
                in 8..32 -> 1.25
                in 5..45 -> 1.0
                else -> 0.7
            }
            Triple(index, sentence, normalizedScore * lengthBonus)
        }

        val chosenIndexes = scored
            .sortedByDescending { it.third }
            .take(4)
            .map { it.first }
            .sorted()
            .toSet()

        val selected = sentences
            .mapIndexedNotNull { index, sentence ->
                if (index in chosenIndexes) trimSentence(sentence) else null
            }
            .filter { it.isNotBlank() }

        return selected.joinToString(" ").ifBlank {
            sentences.take(3).joinToString(" ") { trimSentence(it) }
        }
    }

    private fun splitSentences(text: String): List<String> {
        if (text.isBlank()) return emptyList()

        return text
            .replace(Regex("[\\t ]+"), " ")
            .replace(Regex("\\n{2,}"), ". ")
            .split(Regex("(?<=[.!?])\\s+|\\n+"))
            .map(::trimSentence)
            .filter { sentence -> sentence.length >= 20 && sentence.any(Char::isLetter) }
    }

    private fun tokenize(text: String): List<String> = Regex("[\\p{L}\\p{N}][\\p{L}\\p{N}-]{1,}")
        .findAll(text.lowercase(Locale.ROOT))
        .map { it.value.trim('-') }
        .filter { it.isNotBlank() }
        .toList()

    private fun normalize(text: String): String = text
        .replace('\u0000', ' ')
        .replace(Regex("[ \\t]+"), " ")
        .replace(Regex(" *\\n *"), "\n")
        .replace(Regex("\\n{3,}"), "\n\n")
        .trim()

    private fun trimSentence(sentence: String): String = sentence
        .replace(Regex("\\s+"), " ")
        .trim()
        .trimStart('-', '•', '·', ':')
        .trim()
}
