package com.hng14.energyiq.features.profile.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
internal data class ProfileImageUploadResponse(
    val success: Boolean,
    val message: String,
    val data: UploadData
)

@Serializable
internal data class UploadData(
    val uploadUrl: String,
    val thumbnail: String? = null
)