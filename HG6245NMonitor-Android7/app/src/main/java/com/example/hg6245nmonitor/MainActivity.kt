package com.example.hg6245nmonitor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import com.example.hg6245nmonitor.data.MonitorRepository
import com.example.hg6245nmonitor.model.MonitorUiState
import com.example.hg6245nmonitor.ui.DashboardScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = MonitorRepository()

        setContent {
            MaterialTheme {
                var uiState by remember { mutableStateOf(MonitorUiState()) }
                val scope = rememberCoroutineScope()

                fun refreshMetrics() {
                    scope.launch {
                        uiState = uiState.copy(isLoading = true)
                        uiState = repository.fetchMetrics(uiState)
                    }
                }

                LaunchedEffect(uiState.autoRefresh, uiState.pollIntervalSec, uiState.routerHost) {
                    refreshMetrics()
                    while (uiState.autoRefresh) {
                        delay(uiState.pollIntervalSec * 1000L)
                        uiState = repository.fetchMetrics(uiState)
                    }
                }

                DashboardScreen(
                    uiState = uiState,
                    onRefresh = { refreshMetrics() },
                    onUpdateHost = { newHost ->
                        uiState = uiState.copy(routerHost = newHost.trim())
                    },
                    onUpdateInterval = { sec ->
                        uiState = uiState.copy(pollIntervalSec = sec)
                    },
                    onToggleAutoRefresh = { enabled ->
                        uiState = uiState.copy(autoRefresh = enabled)
                    }
                )
            }
        }
    }
}
