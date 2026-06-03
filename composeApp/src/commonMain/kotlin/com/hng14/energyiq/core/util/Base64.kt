package com.hng14.energyiq.core.util

/**
 * Base64 encoder for platform-specific implementations.
 */
expect fun base64Encode(input: ByteArray): String

