package com.example.hg6245nmonitor.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.hg6245nmonitor.model.MonitorUiState

@Composable
fun DashboardScreen(
    uiState: MonitorUiState,
    onRefresh: () -> Unit,
    onUpdateHost: (String) -> Unit,
    onUpdateInterval: (Int) -> Unit,
    onToggleAutoRefresh: (Boolean) -> Unit
) {
    var hostText by remember(uiState.routerHost) { mutableStateOf(uiState.routerHost) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header Dashboard
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${uiState.deviceModel} Dashboard",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = uiState.statusNote,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                FilledTonalIconButton(onClick = onRefresh) {
                    Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                }
            }
        }

        // Baris 1: CPU Usage & Memory Usage
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricCard(
                    modifier = Modifier.weight(1f),
                    title = "CPU Usage",
                    valueText = "${"%.0f".format(uiState.cpuUsagePercent)}%",
                    progress = (uiState.cpuUsagePercent / 100f).coerceIn(0f, 1f),
                    icon = Icons.Default.Speed
                )
                MetricCard(
                    modifier = Modifier.weight(1f),
                    title = "Memory Usage",
                    valueText = "${"%.2f".format(uiState.memoryUsagePercent)}%",
                    progress = (uiState.memoryUsagePercent / 100f).coerceIn(0f, 1f),
                    icon = Icons.Default.Memory
                )
            }
        }

        // Baris 2: Suhu CPU & Jumlah Pengguna Tersambung
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val tempStatus = when {
                    uiState.cpuTempCelsius >= 75f -> "Panas"
                    uiState.cpuTempCelsius >= 58f -> "Hangat"
                    else -> "Normal"
                }
                SummaryCard(
                    modifier = Modifier.weight(1f),
                    title = "Suhu CPU",
                    valueText = "${"%.1f".format(uiState.cpuTempCelsius)} °C",
                    subtitle = "Status: $tempStatus",
                    icon = Icons.Default.Thermostat
                )
                SummaryCard(
                    modifier = Modifier.weight(1f),
                    title = "Pengguna Tersambung",
                    valueText = "${uiState.connectedUsers.size} Perangkat",
                    subtitle = "Uptime: ${uiState.uptimeText}",
                    icon = Icons.Default.Devices
                )
            }
        }

        // Daftar Pengguna yang Tersambung
        item {
            Text(
                text = "Pengguna yang Tersambung (${uiState.connectedUsers.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        items(uiState.connectedUsers) { user ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(user.deviceName, fontWeight = FontWeight.SemiBold)
                        Text(
                            text = "IP: ${user.ipAddress} • ${user.interfaceInfo}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "MAC: ${user.macAddress}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    SuggestionChip(
                        onClick = {},
                        label = { Text("Aktif") }
                    )
                }
            }
        }

        // Pengaturan Koneksi & Refresh Singkat
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("Pengaturan Koneksi", style = MaterialTheme.typography.titleMedium)

                    OutlinedTextField(
                        value = hostText,
                        onValueChange = { hostText = it },
                        label = { Text("Alamat IP / URL Router") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            onUpdateHost(hostText)
                            onRefresh()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Terapkan & Refresh")
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Auto-Refresh (${uiState.pollIntervalSec}s)")
                        Switch(
                            checked = uiState.autoRefresh,
                            onCheckedChange = onToggleAutoRefresh
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(3, 5, 10).forEach { sec ->
                            FilterChip(
                                selected = uiState.pollIntervalSec == sec,
                                onClick = { onUpdateInterval(sec) },
                                label = { Text("${sec}s") }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricCard(
    modifier: Modifier = Modifier,
    title: String,
    valueText: String,
    progress: Float,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(icon, contentDescription = title, modifier = Modifier.size(18.dp))
                Text(title, style = MaterialTheme.typography.labelLarge)
            }
            Text(
                text = valueText,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun SummaryCard(
    modifier: Modifier = Modifier,
    title: String,
    valueText: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(icon, contentDescription = title, modifier = Modifier.size(18.dp))
                Text(title, style = MaterialTheme.typography.labelLarge)
            }
            Text(
                text = valueText,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
