
package com.juumao.motion

import kotlin.math.pow

enum class MotionEasing {
    LINEAR,
    EASE_IN,
    EASE_OUT,
    EASE_IN_OUT
}

data class Keyframe(
    val time: Long,
    val value: Float,
    val easing: MotionEasing = MotionEasing.LINEAR
)

class AnimatedValue {

    private val keyframes = mutableListOf<Keyframe>()

    fun addKeyframe(
        time: Long,
        value: Float,
        easing: MotionEasing = MotionEasing.LINEAR
    ) {
        keyframes.removeAll { it.time == time }

        keyframes.add(
            Keyframe(time, value, easing)
        )

        keyframes.sortBy { it.time }
    }

    fun removeKeyframe(time: Long) {
        keyframes.removeAll { it.time == time }
    }

    fun getKeyframes(): List<Keyframe> {
        return keyframes.toList()
    }

    fun getValueAt(time: Long): Float {

        if (keyframes.isEmpty()) return 0f

        if (keyframes.size == 1) {
            return keyframes.first().value
        }

        if (time <= keyframes.first().time) {
            return keyframes.first().value
        }

        if (time >= keyframes.last().time) {
            return keyframes.last().value
        }

        val start = keyframes.last {
            it.time <= time
        }

        val end = keyframes.first {
            it.time > time
        }

        val duration = end.time - start.time

        if (duration <= 0L) return start.value

        val progress =
            (time - start.time).toFloat() / duration

        val eased = applyEasing(
            progress,
            start.easing
        )

        return start.value +
            (end.value - start.value) * eased
    }

    private fun applyEasing(
        progress: Float,
        easing: MotionEasing
    ): Float {

        return when (easing) {

            MotionEasing.LINEAR -> progress

            MotionEasing.EASE_IN ->
                progress.pow(2)

            MotionEasing.EASE_OUT ->
                1f - (1f - progress).pow(2)

            MotionEasing.EASE_IN_OUT ->
                if (progress < 0.5f) {
                    2f * progress.pow(2)
                } else {
                    1f - (-2f * progress + 2f).pow(2) / 2f
                }
        }
    }
}
