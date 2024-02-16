package com.example.zenithrajbhandari_a2.ViewModel

import android.os.Build
import android.os.RemoteException
import androidx.annotation.RequiresApi
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.WeightRecord
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.zenithrajbhandari_a2.Data.HealthConnectManager
import kotlinx.coroutines.launch
import java.io.IOException
import java.time.Instant
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.UUID

class InputReadingsViewModel(private val healthConnectManager: HealthConnectManager): ViewModel() {
    val permissions = setOf(
        HealthPermission.getReadPermission(WeightRecord::class),
        HealthPermission.getWritePermission(WeightRecord::class),
    )
    var heartRateInput: MutableState<List<Pair<Instant, Long>>> = mutableStateOf(emptyList())
        private set
    var permissionsGranted = mutableStateOf(false)
        private set

    var uiState: UiState by mutableStateOf(UiState.Uninitialized)
        private set
    var readingsList: MutableState<List<HeartRateRecord>> = mutableStateOf(listOf())
        private set
    val permissionsLauncher = healthConnectManager.requestPermissionsActivityContract()

    @RequiresApi(Build.VERSION_CODES.O)
    fun initialLoad() {
        viewModelScope.launch {
            tryWithPermissionsCheck {
                readHeartBeatData()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun inputReadings(inputValue: Long) {
        viewModelScope.launch {
            tryWithPermissionsCheck {
                healthConnectManager.buildHeartRateSeries(inputValue)
                readHeartBeatData()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun readHeartBeatData() {
        val startOfDay = ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS)
        val now = Instant.now()
        readingsList.value = healthConnectManager.readHeartData(startOfDay.toInstant(), now)
    }

    private suspend fun tryWithPermissionsCheck(block: suspend () -> Unit) {
        permissionsGranted.value = healthConnectManager.hasAllPermissions(permissions)
        uiState = try {
            if (permissionsGranted.value) {
                block()
            }
            UiState.Done
        } catch (remoteException: RemoteException) {
            UiState.Error(remoteException)
        } catch (securityException: SecurityException) {
            UiState.Error(securityException)
        } catch (ioException: IOException) {
            UiState.Error(ioException)
        } catch (illegalStateException: IllegalStateException) {
            UiState.Error(illegalStateException)
        }
    }
    sealed class UiState {
        object Uninitialized : UiState()
        object Done : UiState()

        // A random UUID is used in each Error object to allow errors to be uniquely identified,
        // and recomposition won't result in multiple snackbars.
        data class Error(val exception: Throwable, val uuid: UUID = UUID.randomUUID()) : UiState()
    }
}

class InputReadingsViewModelFactory(
    private val healthConnectManager: HealthConnectManager,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InputReadingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return InputReadingsViewModel(
                healthConnectManager = healthConnectManager
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}