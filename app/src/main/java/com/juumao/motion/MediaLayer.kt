
package com.juumao.motion

import android.net.Uri
import kotlinx.serialization.Serializable

@Serializable
data class MediaLayer(
    val id: String,
    val name: String,
    val uri: String,
    val type: String,
    val startTime: Long = 0L,
    val duration: Long = 5000L,
    val visible: Boolean = true,
    val locked: Boolean = false
)
