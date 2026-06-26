package com.hng14.energyiq.features.reports.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ReportRequest(
    val mode: String,
    val inverterId: String,
    val type: String,
    val name: String,
    val period: String,
    val referenceDate: String,
    val startDate: String? = null,
    val endDate: String? = null
)

@Serializable
data class ReportResponse(
    val success: Boolean,
    val message: String,
    val data: ReportDataDto,
    val meta: ReportMetaDto? = null
)

@Serializable
data class ReportDataDto(
    val id: String,
    val userId: String,
    val inverterId: String,
    val type: String,
    val name: String,
    val period: String,
    val referenceDate: String? = null,
    val status: String,
    val startDate: String? = null,
    val endDate: String? = null,
    val dateDelivered: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val deletedAt: String? = null
)

@Serializable
data class ReportMetaDto(
    val timestamp: String
)

@Serializable
data class ReportsListResponse(
    val success: Boolean,
    val message: String,
    val data: List<ReportDataDto>,
    val meta: ReportsListMetaDto? = null
)

@Serializable
data class ReportsListMetaDto(
    val timestamp: String,
    val pagination: PaginationDto? = null
)

@Serializable
data class PaginationDto(
    val total: Int,
    val limit: Int,
    val page: Int,
    val total_pages: Int,
    val has_next: Boolean,
    val has_previous: Boolean
)

@Serializable
data class ReportDeleteResponse(
    val success: Boolean,
    val message: String,
    val data: DeleteDataDto? = null,
    val meta: ReportMetaDto? = null
)

@Serializable
data class DeleteDataDto(
    val affected: Int
)

@Serializable
data class EmailReportResponse(
    val success: Boolean,
    val message: String,
    val meta: ReportMetaDto? = null
)

@Serializable
data class ReportCancelResponse(
    val success: Boolean,
    val message: String
)
