package com.hng14.energyiq.features.home.data

import com.hng14.energyiq.features.home.data.remote.dto.InverterDashboardData
import com.hng14.energyiq.features.home.data.local.BatteryHealthLogDao
import com.hng14.energyiq.features.home.data.local.BatteryHealthLogEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

data class BatteryHealthLogEntry(
    val status: String,
    val reason: String,
    val recordedAt: String,
    val dataAgeSeconds: Int?,
    val systemOffline: Boolean,
)

/**
 * In-memory log of battery health snapshots captured from the dashboard poll (every ~30s).
 * Persisted; user+inverter scoped.
 */
class HealthLogRepository(
    private val dao: BatteryHealthLogDao,
) {
    fun observeBatteryHealthLogs(
        userId: String,
        inverterId: String,
    ): Flow<List<BatteryHealthLogEntry>> {
        return dao.observeLatest(userId = userId, inverterId = inverterId, limit = 500)
            .map { rows ->
                rows.map { row ->
                    BatteryHealthLogEntry(
                        status = row.status,
                        reason = row.reason,
                        recordedAt = row.recordedAt,
                        dataAgeSeconds = row.dataAgeSeconds,
                        systemOffline = row.systemOffline,
                    )
                }
            }
    }

    suspend fun recordFromDashboard(
        userId: String,
        inverterId: String,
        data: InverterDashboardData,
    ) {
        val health = data.health ?: return
        val recordedAt = data.currentReadings?.recordedAt?.takeIf { it.isNotBlank() } ?: return

        val entity = BatteryHealthLogEntity(
            userId = userId,
            inverterId = inverterId,
            recordedAt = recordedAt,
            status = health.status,
            reason = health.reason,
            dataAgeSeconds = data.dataAgeSeconds,
            systemOffline = data.systemOffline,
        )
        dao.insert(entity)
        dao.pruneToLatest(userId = userId, inverterId = inverterId, limit = 500)
    }

    suspend fun clear(userId: String, inverterId: String) =
        dao.clearForInverter(userId = userId, inverterId = inverterId)
}
