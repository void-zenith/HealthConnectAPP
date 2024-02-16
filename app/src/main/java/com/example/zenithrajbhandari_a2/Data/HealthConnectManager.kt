package com.example.zenithrajbhandari_a2.Data

import android.annotation.SuppressLint
import android.content.Context
import android.health.connect.datatypes.HeartRateRecord.HeartRateSample
import android.os.Build
import androidx.activity.result.contract.ActivityResultContract
import androidx.annotation.RequiresApi
import androidx.health.connect.client.HealthConnectClient
import java.time.ZonedDateTime
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.WeightRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import java.time.Instant
import kotlin.random.Random

// The minimum android level that can use Health Connect
const val MIN_SUPPORTED_SDK = Build.VERSION_CODES.O_MR1

class HealthConnectManager(private val context: Context) {
    private val healthConnectClient by lazy { HealthConnectClient.getOrCreate(context) }
    suspend fun hasAllPermissions(permissions: Set<String>): Boolean {
        return healthConnectClient.permissionController.getGrantedPermissions().containsAll(permissions)
    }

    fun requestPermissionsActivityContract(): ActivityResultContract<Set<String>, Set<String>> {
        return PermissionController.createRequestPermissionResultContract()
    }
    @SuppressLint("SuspiciousIndentation")
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun buildHeartRateSeries(heartInput: Long): HeartRateRecord {
        val samples = mutableListOf<HeartRateRecord.Sample>()
       val time = ZonedDateTime.now().withNano(0)
            samples.add(
                HeartRateRecord.Sample(
                    time = time.toInstant(),
                    beatsPerMinute = heartInput
                )
            )
        return HeartRateRecord(
            startTime = time.toInstant(),
            startZoneOffset = time.offset,
            endTime = time.toInstant(),
            endZoneOffset = time.offset,
            samples = samples
        )
    }
    suspend fun readHeartData(start: Instant, end: Instant): List<HeartRateRecord> {
        val request = ReadRecordsRequest(
            recordType = HeartRateRecord::class,
            timeRangeFilter = TimeRangeFilter.between(start, end)
        )
        val response = healthConnectClient.readRecords(request)
        return response.records
    }
}
