package com.wafa.signal555.data.local

import android.content.Context
import android.util.Base64

data class LocalHistoryItem(
    val type: String,
    val title: String,
    val status: String,
    val score: Int?,
    val createdAt: Long = System.currentTimeMillis()
)

/** Small persistent history store for the local MVP. No network and no database dependency. */
object LocalHistoryStore {
    private const val PREFS = "signal555_history"
    private const val KEY_ITEMS = "items_v1"
    private const val MAX_ITEMS = 30

    fun add(context: Context, item: LocalHistoryItem) {
        val current = getAll(context).toMutableList()
        current.add(0, item)
        save(context, current.take(MAX_ITEMS))
    }

    fun getAll(context: Context): List<LocalHistoryItem> {
        val raw = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY_ITEMS, "")
            .orEmpty()
        if (raw.isBlank()) return emptyList()

        return raw.lineSequence()
            .filter { it.isNotBlank() }
            .mapNotNull(::decode)
            .sortedByDescending { it.createdAt }
            .toList()
    }

    fun clear(context: Context) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .remove(KEY_ITEMS)
            .apply()
    }

    private fun save(context: Context, items: List<LocalHistoryItem>) {
        val raw = items.joinToString("\n", transform = ::encode)
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_ITEMS, raw)
            .apply()
    }

    private fun encode(item: LocalHistoryItem): String = listOf(
        encodeText(item.type),
        encodeText(item.title),
        encodeText(item.status),
        item.score?.toString().orEmpty(),
        item.createdAt.toString()
    ).joinToString("|")

    private fun decode(line: String): LocalHistoryItem? {
        val parts = line.split('|')
        if (parts.size != 5) return null
        return runCatching {
            LocalHistoryItem(
                type = decodeText(parts[0]),
                title = decodeText(parts[1]),
                status = decodeText(parts[2]),
                score = parts[3].toIntOrNull(),
                createdAt = parts[4].toLong()
            )
        }.getOrNull()
    }

    private fun encodeText(value: String): String = Base64.encodeToString(
        value.toByteArray(Charsets.UTF_8),
        Base64.NO_WRAP or Base64.URL_SAFE
    )

    private fun decodeText(value: String): String = String(
        Base64.decode(value, Base64.NO_WRAP or Base64.URL_SAFE),
        Charsets.UTF_8
    )
}
