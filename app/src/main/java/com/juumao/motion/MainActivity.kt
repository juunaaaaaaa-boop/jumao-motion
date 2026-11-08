package com.juumao.motion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

private val Dark = Color(0xFF101114)
private val Panel = Color(0xFF1B1D22)
private val Accent = Color(0xFFB6FF4A)
private val Muted = Color(0xFF858994)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = android.graphics.Color.rgb(16, 17, 20)
        window.navigationBarColor = android.graphics.Color.rgb(16, 17, 20)

        setContent {
            MaterialTheme(
                colorScheme = darkColorScheme(
                    background = Dark,
                    surface = Panel,
                    primary = Accent,
                    onPrimary = Color.Black
                    MediaEditor
                    }
                )
            ) {
                JuumaoEditor()
            }
        }
    }
}

@Composable
fun JuumaoEditor() {
    val project = remember {
        MotionProject().apply {
            addTextLayer()
        }
    }

    var revision by remember { mutableIntStateOf(0) }
    var currentTime by remember { mutableLongStateOf(0L) }
    var playing by remember { mutableStateOf(false) }

    val layers = remember(revision) {
        project.getLayers()
    }

    val selectedLayer = remember(revision) {
        project.getSelectedLayer()
    }

    LaunchedEffect(playing) {
        while (playing) {
            delay(16)
            currentTime += 16

            if (currentTime >= project.duration) {
                currentTime = project.duration
                playing = false
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Dark)
    ) {

        // TOP BAR
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "JUUMAO",
                color = Accent,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black
            )

            Text(
                project.name,
                color = Color.White,
                fontSize = 13.sp
            )

            Text(
                "1.0",
                color = Muted,
                fontSize = 12.sp
            )
        }

        // PREVIEW
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 20.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFF08090B))
                .border(
                    1.dp,
                    Color(0xFF292C32),
                    RoundedCornerShape(14.dp)
                ),
            contentAlignment = Alignment.Center
        ) {

            layers.reversed().forEach { layer ->

                val transform = layer.getTransform(currentTime)

                when (layer.type) {

                    LayerType.TEXT -> {
                        Text(
                            text = layer.name,
                            color = Color.White,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .offset(
                                    x = transform.x.dp,
                                    y = transform.y.dp
                                )
                                .graphicsLayer {
                                    scaleX = transform.scaleX
                                    scaleY = transform.scaleY
                                    rotationZ = transform.rotation
                                    alpha = transform.opacity
                                }
                        )
                    }

                    LayerType.SHAPE -> {
                        Box(
                            modifier = Modifier
                                .size(75.dp)
                                .offset(
                                    x = transform.x.dp,
                                    y = transform.y.dp
                                )
                                .graphicsLayer {
                                    scaleX = transform.scaleX
                                    scaleY = transform.scaleY
                                    rotationZ = transform.rotation
                                    alpha = transform.opacity
                                }
                                .background(
                                    Accent,
                                    RoundedCornerShape(12.dp)
                                )
                        )
                    }

                    LayerType.IMAGE,
                    LayerType.VIDEO,
                    LayerType.AUDIO -> {
                        Text(
                            text = layer.name,
                            color = Muted,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Text(
                "PREVIEW",
                color = Color.White.copy(alpha = 0.12f),
                fontSize = 10.sp,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(10.dp)
            )
        }

        Spacer(Modifier.height(12.dp))

        // PLAYBACK
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Text(
                "${currentTime / 1000f}s",
                color = Color.White,
                fontSize = 12.sp
            )

            Row(verticalAlignment = Alignment.CenterVertically) {

                TextButton(
                    onClick = { currentTime = 0L }
                ) {
                    Text("RESET", color = Muted)
                }

                Button(
                    onClick = { playing = !playing },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Accent,
                        contentColor = Color.Black
                    )
                ) {
                    Text(if (playing) "PAUSE" else "PLAY")
                }
            }

            Text(
                "${project.duration / 1000f}s",
                color = Muted,
                fontSize = 12.sp
            )
        }

        Slider(
            value = currentTime.toFloat(),
            onValueChange = {
                currentTime = it.toLong()
            },
            valueRange = 0f..project.duration.toFloat(),
            colors = SliderDefaults.colors(
                thumbColor = Accent,
                activeTrackColor = Accent,
                inactiveTrackColor = Panel
            ),
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        // LAYER MANAGER
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .background(Panel)
                .padding(12.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "LAYERS (${layers.size})",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    "ADD LAYER",
                    color = Accent,
                    fontSize = 11.sp
                )
            }

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                AddLayerButton("TEXT") {
                    project.addTextLayer()
                    revision++
                }

                AddLayerButton("SHAPE") {
                    project.addShapeLayer()
                    revision++
                }

                AddLayerButton("IMAGE") {
                    project.addImageLayer()
                    revision++
                }

                AddLayerButton("VIDEO") {
                    project.addVideoLayer()
                    revision++
                }

                AddLayerButton("AUDIO") {
                    project.addAudioLayer()
                    revision++
                }
            }

            Spacer(Modifier.height(8.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                layers.forEach { layer ->

                    val selected =
                        project.selectedLayerId == layer.id

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (selected)
                                    Color(0xFF303A25)
                                else
                                    Color(0xFF24262B)
                            )
                            .clickable {
                                project.selectLayer(layer.id)
                                revision++
                            }
                            .padding(11.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        Column {
                            Text(
                                layer.name,
                                color = if (selected) Accent else Color.White,
                                fontSize = 13.sp
                            )

                            Text(
                                layer.type.name,
                                color = Muted,
                                fontSize = 9.sp
                            )
                        }

                        if (selected) {
                            Text(
                                "SELECTED",
                                color = Accent,
                                fontSize = 9.sp
                            )
                        }
                    }
                }
            }
        }

        // PROPERTY PANEL
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(245.dp)
                .background(Dark)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "TRANSFORM",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                TextButton(
                    onClick = {
                        selectedLayer?.let {
                            it.positionX.addKeyframe(
                                currentTime,
                                it.positionX.getValueAt(currentTime)
                            )
                            it.positionY.addKeyframe(
                                currentTime,
                                it.positionY.getValueAt(currentTime)
                            )
                            it.scaleX.addKeyframe(
                                currentTime,
                                it.scaleX.getValueAt(currentTime)
                            )
                            it.scaleY.addKeyframe(
                                currentTime,
                                it.scaleY.getValueAt(currentTime)
                            )
                            it.rotation.addKeyframe(
                                currentTime,
                                it.rotation.getValueAt(currentTime)
                            )
                            it.opacity.addKeyframe(
                                currentTime,
                                it.opacity.getValueAt(currentTime)
                            )
                        }

                        revision++
                    }
                ) {
                    Text("＋ KEYFRAME", color = Accent)
                }
            }

            if (selectedLayer != null) {

                PropertySlider(
                    "POSITION X",
                    selectedLayer.positionX.getValueAt(currentTime),
                    -300f,
                    300f
                ) {
                    selectedLayer.positionX.addKeyframe(currentTime, it)
                    revision++
                }

                PropertySlider(
                    "POSITION Y",
                    selectedLayer.positionY.getValueAt(currentTime),
                    -300f,
                    300f
                ) {
                    selectedLayer.positionY.addKeyframe(currentTime, it)
                    revision++
                }

                PropertySlider(
                    "SCALE",
                    selectedLayer.scaleX.getValueAt(currentTime),
                    0.1f,
                    3f
                ) {
                    selectedLayer.scaleX.addKeyframe(currentTime, it)
                    selectedLayer.scaleY.addKeyframe(currentTime, it)
                    revision++
                }

                PropertySlider(
                    "ROTATION",
                    selectedLayer.rotation.getValueAt(currentTime),
                    -360f,
                    360f
                ) {
                    selectedLayer.rotation.addKeyframe(currentTime, it)
                    revision++
                }

                PropertySlider(
                    "OPACITY",
                    selectedLayer.opacity.getValueAt(currentTime),
                    0f,
                    1f
                ) {
                    selectedLayer.opacity.addKeyframe(currentTime, it)
                    revision++
                }

                Button(
                    onClick = {
                        project.removeLayer(selectedLayer.id)
                        revision++
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF402326),
                        contentColor = Color(0xFFFF7777)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("DELETE SELECTED LAYER")
                }

            } else {
                Text(
                    "Pilih layer untuk mengatur transform.",
                    color = Muted
                )
            }
        }
    }
}

@Composable
fun AddLayerButton(
    title: String,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        contentPadding = PaddingValues(
            horizontal = 12.dp,
            vertical = 4.dp
        )
    ) {
        Text(
            title,
            fontSize = 10.sp,
            color = Accent
        )
    }
}

@Composable
fun PropertySlider(
    title: String,
    value: Float,
    min: Float,
    max: Float,
    onChange: (Float) -> Unit
) {
    Column(
        modifier = Modifier.padding(vertical = 5.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                title,
                color = Color.White,
                fontSize = 10.sp
            )

            Text(
                String.format("%.2f", value),
                color = Accent,
                fontSize = 10.sp
            )
        }

        Slider(
            value = value.coerceIn(min, max),
            onValueChange = onChange,
            valueRange = min..max,
            colors = SliderDefaults.colors(
                thumbColor = Accent,
                activeTrackColor = Accent
            )
        )
    }
}
