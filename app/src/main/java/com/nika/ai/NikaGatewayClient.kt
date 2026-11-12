package com.nika.ai

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

/**
 * HTTPS client for the private NIKA gateway.
 *
 * The access token must be supplied at runtime by a real authentication flow.
 * Never place a long-lived gateway token in BuildConfig, source control, or the APK.
 */
internal object NikaGatewayClient {
    suspend fun sendMessage(
        baseUrl: String,
        accessToken: String,
        message: String
    ): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            require(baseUrl.startsWith("https://")) {
                "آدرس سرور باید با HTTPS شروع شود."
            }
            require(accessToken.isNotBlank()) {
                "نشست امن هنوز آماده نشده است."
            }
            require(message.isNotBlank() && message.length <= 5000) {
                "پیام باید بین ۱ تا ۵۰۰۰ نویسه باشد."
            }

            val endpoint = baseUrl.trimEnd('/') + "/"
            val connection = (URL(endpoint).openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                connectTimeout = 10000
                readTimeout = 25000
                doOutput = true
                setRequestProperty("Content-Type", "application/json; charset=utf-8")
                setRequestProperty("Accept", "application/json")
                setRequestProperty("Authorization", "Bearer $accessToken")
            }

            try {
                val payload = JSONObject().put("message", message).toString()
                connection.outputStream.use { stream ->
                    stream.write(payload.toByteArray(Charsets.UTF_8))
                }

                val status = connection.responseCode
                val responseStream = if (status in 200..299) {
                    connection.inputStream
                } else {
                    connection.errorStream
                }
                val responseText = responseStream?.bufferedReader(Charsets.UTF_8)?.use {
                    it.readText()
                }.orEmpty()

                if (status !in 200..299) {
                    throw IllegalStateException(
                        when (status) {
                            401 -> "نشست امن معتبر نیست."
                            503 -> "سرور هنوز راه‌اندازی نشده است."
                            502, 504 -> "سرویس هوش مصنوعی فعلاً پاسخ نمی‌دهد."
                            else -> "ارتباط با سرور ناموفق بود (کد $status)."
                        }
                    )
                }

                JSONObject(responseText).optString("reply").takeIf { it.isNotBlank() }
                    ?: throw IllegalStateException("پاسخ معتبری از نیکا دریافت نشد.")
            } finally {
                connection.disconnect()
            }
        }
    }
}
