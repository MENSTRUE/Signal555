package com.wafa.signal555.data.local

import android.content.Context
import android.util.Base64

data class LocalChatMessage(
    val role: String,
    val text: String,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Small local conversation store for the offline assistant MVP.
 * The content never leaves the device.
 */
object LocalChatStore {
    private const val PREFS = "signal555_chat"
    private const val KEY_MESSAGES = "messages_v1"
    private const val MAX_MESSAGES = 60

    fun add(context: Context, message: LocalChatMessage) {
        val current = getAll(context).toMutableList()
        current += message
        save(context, current.takeLast(MAX_MESSAGES))
    }

    fun getAll(context: Context): List<LocalChatMessage> {
        val raw = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY_MESSAGES, "")
            .orEmpty()

        if (raw.isBlank()) return emptyList()

        return raw.lineSequence()
            .filter { it.isNotBlank() }
            .mapNotNull(::decode)
            .sortedBy { it.createdAt }
            .toList()
    }

    fun clear(context: Context) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .remove(KEY_MESSAGES)
            .apply()
    }

    private fun save(context: Context, messages: List<LocalChatMessage>) {
        val raw = messages.joinToString("\n", transform = ::encode)
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_MESSAGES, raw)
            .apply()
    }

    private fun encode(message: LocalChatMessage): String = listOf(
        encodeText(message.role),
        encodeText(message.text),
        message.createdAt.toString()
    ).joinToString("|")

    private fun decode(line: String): LocalChatMessage? {
        val parts = line.split('|')
        if (parts.size != 3) return null
        return runCatching {
            LocalChatMessage(
                role = decodeText(parts[0]),
                text = decodeText(parts[1]),
                createdAt = parts[2].toLong()
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
