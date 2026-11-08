
package com.juumao.motion

import java.util.UUID

class MotionProject(
    var name: String = "Untitled Project",
    var width: Int = 1080,
    var height: Int = 1920,
    var frameRate: Int = 30,
    var duration: Long = 5000L
) {

    private val layerList = mutableListOf<MotionLayer>()

    var selectedLayerId: String? = null
        private set

    fun getLayers(): List<MotionLayer> {
        return layerList.toList()
    }

    fun addLayer(
        name: String,
        type: LayerType
    ): MotionLayer {

        val layer = MotionLayer(
            id = UUID.randomUUID().toString(),
            name = name,
            type = type
        )

        layerList.add(0, layer)
        selectedLayerId = layer.id

        return layer
    }

    fun addTextLayer(): MotionLayer {
        return addLayer(
            "Text ${layerList.size + 1}",
            LayerType.TEXT
        )
    }

    fun addShapeLayer(): MotionLayer {
        return addLayer(
            "Shape ${layerList.size + 1}",
            LayerType.SHAPE
        )
    }

    fun addImageLayer(): MotionLayer {
        return addLayer(
            "Image ${layerList.size + 1}",
            LayerType.IMAGE
        )
    }

    fun addVideoLayer(): MotionLayer {
        return addLayer(
            "Video ${layerList.size + 1}",
            LayerType.VIDEO
        )
    }

    fun addAudioLayer(): MotionLayer {
        return addLayer(
            "Audio ${layerList.size + 1}",
            LayerType.AUDIO
        )
    }

    fun selectLayer(id: String) {
        if (layerList.any { it.id == id }) {
            selectedLayerId = id
        }
    }

    fun getSelectedLayer(): MotionLayer? {
        return layerList.find {
            it.id == selectedLayerId
        }
    }

    fun removeLayer(id: String) {
        layerList.removeAll {
            it.id == id
        }

        if (selectedLayerId == id) {
            selectedLayerId = layerList.firstOrNull()?.id
        }
    }

    fun renameLayer(
        id: String,
        newName: String
    ) {
        layerList.find {
            it.id == id
        }?.name = newName
    }

    fun moveLayer(
        fromIndex: Int,
        toIndex: Int
    ) {
        if (
            fromIndex !in layerList.indices ||
            toIndex !in layerList.indices
        ) return

        val layer = layerList.removeAt(fromIndex)
        layerList.add(toIndex, layer)
    }

    fun clearLayers() {
        layerList.clear()
        selectedLayerId = null
    }

    fun layerCount(): Int {
        return layerList.size
    }
}
