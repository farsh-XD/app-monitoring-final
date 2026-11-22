package com.example.hg6245nmonitor.model

data class ConnectedUser(
    val deviceName: String,
    val ipAddress: String,
    val macAddress: String,
    val interfaceInfo: String
)

data class MonitorUiState(
    val routerHost: String = "http://192.168.1.100",
    val deviceModel: String = "HG6245N",
    val uptimeText: String = "11 d 19 h 26 m 10 s",
    val cpuUsagePercent: Float = 25f,
    val memoryUsagePercent: Float = 45.87f,
    val cpuTempCelsius: Float = 48.5f,
    val pollIntervalSec: Int = 5,
    val autoRefresh: Boolean = true,
    val isLoading: Boolean = false,
    val isConnected: Boolean = false,
    val statusNote: String = "Siap memantau 192.168.1.100",
    val connectedUsers: List<ConnectedUser> = listOf(
        ConnectedUser("Android-Phone", "192.168.1.105", "AA:BB:CC:11:22:33", "WLAN 5G"),
        ConnectedUser("Laptop-Utama", "192.168.1.102", "AA:BB:CC:44:55:66", "LAN 1"),
        ConnectedUser("Smart-TV", "192.168.1.108", "AA:BB:CC:77:88:99", "WLAN 2.4G")
    )
)
