
package com.juumao.motion

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.util.UUID

@Composable
fun MediaEditor() {
    val context = androidx.compose.ui.platform.LocalContext.current

    var layers by remember {
        mutableStateOf(
            MediaProjectManager.load(context)?.layers
                ?: emptyList()
        )
    }

    var selectedId by remember {
        mutableStateOf<String?>(null)
    }

    var projectName by remember {
        mutableStateOf("JUUMAO PROJECT")
    }

    val picker = rememberMediaPicker { uri, type ->
        val layer = MediaLayer(
            id = UUID.randomUUID().toString(),
            name = "Media ${layers.size + 1}",
            uri = uri.toString(),
            type = type.name
        )

        layers = layers + layer
        selectedId = layer.id
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF101010))
            .padding(16.dp)
    ) {
        Text(
            "JUUMAO MOTION",
            color = Color.White,
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(onClick = {
                picker(MediaType.IMAGE)
            }) {
                Text("Gambar")
            }

            Button(onClick = {
                picker(MediaType.VIDEO)
            }) {
                Text("Video")
            }

            Button(onClick = {
                picker(MediaType.AUDIO)
            }) {
                Text("Audio")
            }
        }

        Spacer(Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(
                    Color(0xFF202020),
                    RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            val selected = layers.find {
                it.id == selectedId
            }

            if (selected == null) {
                Text(
                    "Pilih media untuk memulai",
                    color = Color.LightGray
                )
            } else {
                Text(
                    "${selected.name}\n${selected.type}\n${selected.uri}",
                    color = Color.White
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        MediaTimeline(
            layers = layers,
            selectedId = selectedId,
            onSelect = {
                selectedId = it
            },
            onDelete = { id ->
                layers = layers.filterNot {
                    it.id == id
                }

                if (selectedId == id) {
                    selectedId = null
                }
            }
        )

        Spacer(Modifier.height(12.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                MediaProjectManager.save(
                    context,
                    MediaProject(
                        name = projectName,
                        layers = layers
                    )
                )
            }
        ) {
            Text("SIMPAN PROJECT")
        }
    }
}
