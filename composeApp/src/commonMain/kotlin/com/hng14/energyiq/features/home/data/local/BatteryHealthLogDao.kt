package com.hng14.energyiq.features.home.data.local

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BatteryHealthLogDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entity: BatteryHealthLogEntity)

    @Query(
        """
        SELECT * FROM battery_health_logs
        WHERE userId = :userId AND inverterId = :inverterId
        ORDER BY recordedAt DESC
        LIMIT :limit
        """
    )
    fun observeLatest(
        userId: String,
        inverterId: String,
        limit: Int = 500,
    ): Flow<List<BatteryHealthLogEntity>>

    @Query(
        """
        DELETE FROM battery_health_logs
        WHERE userId = :userId AND inverterId = :inverterId
          AND recordedAt NOT IN (
            SELECT recordedAt FROM battery_health_logs
            WHERE userId = :userId AND inverterId = :inverterId
            ORDER BY recordedAt DESC
            LIMIT :limit
          )
        """
    )
    suspend fun pruneToLatest(
        userId: String,
        inverterId: String,
        limit: Int = 500,
    )

    @Query(
        """
        DELETE FROM battery_health_logs
        WHERE userId = :userId AND inverterId = :inverterId
        """
    )
    suspend fun clearForInverter(userId: String, inverterId: String)
}

