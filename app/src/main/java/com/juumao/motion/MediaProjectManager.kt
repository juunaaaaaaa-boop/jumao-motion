
package com.juumao.motion

import android.content.Context
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

object MediaProjectManager {

    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    fun save(
        context: Context,
        project: MediaProject
    ): Boolean {
        return try {
            val file = File(
                context.filesDir,
                "juumao_project.json"
            )

            file.writeText(json.encodeToString(project))
            true
        } catch (e: Exception) {
            false
        }
    }

    fun load(context: Context): MediaProject? {
        return try {
            val file = File(
                context.filesDir,
                "juumao_project.json"
            )

            if (!file.exists()) return null

            json.decodeFromString<MediaProject>(
                file.readText()
            )
        } catch (e: Exception) {
            null
        }
    }
}
