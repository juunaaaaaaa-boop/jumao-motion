
package com.juumao.motion

enum class LayerType {
    TEXT,
    IMAGE,
    VIDEO,
    AUDIO,
    SHAPE
}

class MotionLayer(
    val id: String,
    var name: String,
    val type: LayerType
) {
    val positionX = AnimatedValue()
    val positionY = AnimatedValue()

    val scaleX = AnimatedValue()
    val scaleY = AnimatedValue()

    val rotation = AnimatedValue()
    val opacity = AnimatedValue()

    init {
        positionX.addKeyframe(0L, 0f)
        positionY.addKeyframe(0L, 0f)

        scaleX.addKeyframe(0L, 1f)
        scaleY.addKeyframe(0L, 1f)

        rotation.addKeyframe(0L, 0f)
        opacity.addKeyframe(0L, 1f)
    }

    fun getTransform(time: Long): LayerTransform {
        return LayerTransform(
            x = positionX.getValueAt(time),
            y = positionY.getValueAt(time),
            scaleX = scaleX.getValueAt(time),
            scaleY = scaleY.getValueAt(time),
            rotation = rotation.getValueAt(time),
            opacity = opacity.getValueAt(time)
        )
    }
}

data class LayerTransform(
    val x: Float,
    val y: Float,
    val scaleX: Float,
    val scaleY: Float,
    val rotation: Float,
    val opacity: Float
)
