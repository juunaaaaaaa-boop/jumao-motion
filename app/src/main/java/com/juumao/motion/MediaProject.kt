
package com.juumao.motion

import kotlinx.serialization.Serializable

@Serializable
data class MediaProject(
    val name: String = "Untitled Project",
    val width: Int = 1080,
    val height: Int = 1920,
    val frameRate: Int = 30,
    val layers: List<MediaLayer> = emptyList()
)
