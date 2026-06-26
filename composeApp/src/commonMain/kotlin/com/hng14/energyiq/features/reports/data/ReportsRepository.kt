package com.hng14.energyiq.features.reports.data

import com.hng14.energyiq.features.reports.data.remote.ReportsApi
import com.hng14.energyiq.features.reports.data.remote.dto.ReportRequest
import com.hng14.energyiq.features.reports.domain.model.ReportIcon
import com.hng14.energyiq.features.reports.domain.model.ReportItem

class ReportsRepository(
    private val api: ReportsApi
) {
    suspend fun createReport(
        mode: String,
        inverterId: String,
        type: String,
        name: String,
        period: String,
        referenceDate: String,
        startDate: String? = null,
        endDate: String? = null
    ): ReportItem {
        val request = ReportRequest(
            mode = mode,
            inverterId = inverterId,
            type = type,
            name = name,
            period = period,
            referenceDate = referenceDate,
            startDate = startDate,
            endDate = endDate
        )
        val response = api.createReport(request)
        val data = response.data
        
        // Map icon based on report type
        val reportIcon = when (data.type.uppercase()) {
            "SOLAR" -> ReportIcon.SOLAR
            "ALERT" -> ReportIcon.ENERGY
            "COSTS_AND_SAVINGS" -> ReportIcon.BREAKDOWN
            else -> ReportIcon.SUMMARY
        }
        
        // Formulate date range display string
        val dateRangeStr = if (!data.startDate.isNullOrBlank() && !data.endDate.isNullOrBlank()) {
            "${data.startDate} - ${data.endDate}"
        } else {
            data.referenceDate?.substringBefore("T") ?: referenceDate
        }

        return ReportItem(
            id = data.id,
            status = data.status,
            icon = reportIcon,
            title = data.name,
            dateRange = dateRangeStr
        )
    }

    suspend fun getReports(): List<ReportItem> {
        val response = api.getReports()
        return response.data.map { data ->
            val reportIcon = when (data.type.uppercase()) {
                "SOLAR" -> ReportIcon.SOLAR
                "ALERT" -> ReportIcon.ENERGY
                "COSTS_AND_SAVINGS" -> ReportIcon.BREAKDOWN
                else -> ReportIcon.SUMMARY
            }
            val dateRangeStr = if (!data.startDate.isNullOrBlank() && !data.endDate.isNullOrBlank()) {
                "${data.startDate} - ${data.endDate}"
            } else {
                data.referenceDate?.substringBefore("T") ?: ""
            }
            ReportItem(
                id = data.id,
                status = data.status,
                icon = reportIcon,
                title = data.name,
                dateRange = dateRangeStr
            )
        }
    }

    suspend fun deleteReport(id: String) {
        api.deleteReport(id)
    }

    suspend fun emailReport(id: String) {
        api.emailReport(id)
    }

    suspend fun cancelReport(id: String) {
        api.cancelReport(id)
    }
}
