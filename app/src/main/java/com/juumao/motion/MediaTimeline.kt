
package com.juumao.motion

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun MediaTimeline(
    layers: List<MediaLayer>,
    selectedId: String?,
    onSelect: (String) -> Unit,
    onDelete: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF151515))
            .padding(12.dp)
    ) {
        Text(
            "TIMELINE",
            color = Color.White
        )

        Spacer(Modifier.height(10.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(layers, key = { it.id }) { layer ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            if (selectedId == layer.id)
                                Color(0xFF343434)
                            else Color(0xFF222222),
                            RoundedCornerShape(10.dp)
                        )
                        .clickable {
                            onSelect(layer.id)
                        }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            layer.name,
                            color = Color.White
                        )

                        Text(
                            "${layer.type} • ${layer.duration} ms",
                            color = Color.LightGray,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }

                    TextButton(
                        onClick = {
                            onDelete(layer.id)
                        }
                    ) {
                        Text(
                            "Hapus",
                            color = Color(0xFFFF7777)
                        )
                    }
                }
            }
        }
    }
}
