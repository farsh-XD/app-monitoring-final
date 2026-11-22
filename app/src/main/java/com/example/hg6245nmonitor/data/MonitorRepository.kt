package com.example.hg6245nmonitor.data

import com.example.hg6245nmonitor.model.MonitorUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

class MonitorRepository {

    suspend fun fetchMetrics(current: MonitorUiState): MonitorUiState =
        withContext(Dispatchers.IO) {
            val baseUrl = current.routerHost.trim().trimEnd('/')
            val urls = listOf(
                "$baseUrl/html/status/deviceinfo.asp",
                "$baseUrl/html/status/status.asp",
                "$baseUrl/"
            )

            for (target in urls) {
                try {
                    val conn = (URL(target).openConnection() as HttpURLConnection).apply {
                        requestMethod = "GET"
                        connectTimeout = 2500
                        readTimeout = 2500
                    }
                    val code = conn.responseCode
                    val body = if (code in 200..299) {
                        conn.inputStream.bufferedReader().use { it.readText() }
                    } else {
                        ""
                    }
                    conn.disconnect()

                    if (body.isNotBlank()) {
                        val cpuVal = Regex("""CPU\s*Usage[^0-9]*([0-9]+(?:\.[0-9]+)?)""", RegexOption.IGNORE_CASE)
                            .find(body)?.groupValues?.getOrNull(1)?.toFloatOrNull()
                        val memVal = Regex("""Memory\s*Usage[^0-9]*([0-9]+(?:\.[0-9]+)?)""", RegexOption.IGNORE_CASE)
                            .find(body)?.groupValues?.getOrNull(1)?.toFloatOrNull()
                        val tempVal = Regex("""(?:CPU\s*)?(?:Temp|Temperature)[^0-9]*([0-9]+(?:\.[0-9]+)?)""", RegexOption.IGNORE_CASE)
                            .find(body)?.groupValues?.getOrNull(1)?.toFloatOrNull()
                        val uptimeVal = Regex("""System\s*UpTime[^<]*<[^>]*>([^<]+)""", RegexOption.IGNORE_CASE)
                            .find(body)?.groupValues?.getOrNull(1)?.trim()

                        return@withContext current.copy(
                            cpuUsagePercent = cpuVal ?: current.cpuUsagePercent,
                            memoryUsagePercent = memVal ?: current.memoryUsagePercent,
                            cpuTempCelsius = tempVal ?: current.cpuTempCelsius,
                            uptimeText = uptimeVal ?: current.uptimeText,
                            isConnected = true,
                            isLoading = false,
                            statusNote = "Terhubung ke $baseUrl (HTTP $code)"
                        )
                    }
                } catch (_: Exception) {
                    // Lanjut ke URL kandidat berikutnya
                }
            }

            current.copy(
                isConnected = false,
                isLoading = false,
                statusNote = "Menampilkan data terakhir ($baseUrl belum merespons langsung)"
            )
        }
}
