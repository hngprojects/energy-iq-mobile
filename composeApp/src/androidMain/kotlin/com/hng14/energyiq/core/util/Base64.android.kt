package com.hng14.energyiq.core.util

import android.util.Base64

actual fun base64Encode(input: ByteArray): String =
    Base64.encodeToString(input, Base64.NO_WRAP)

