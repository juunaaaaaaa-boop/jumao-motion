
package com.juumao.motion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.layout.offset
import kotlinx.coroutines.delay

private val Background = Color(0xFF101114)
private val SurfaceDark = Color(0xFF191B20)
private val Accent = Color(0xFF00D9B5)
private val TextPrimary = Color(0xFFF5F5F5)
private val TextSecondary = Color(0xFF92959E)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = android.graphics.Color.rgb(16, 17, 20)
        window.navigationBarColor = android.graphics.Color.rgb(16, 17, 20)

        setContent {
            MaterialTheme(
                colorScheme = darkColorScheme(
                    background = Background,
                    surface = SurfaceDark,
                    primary = Accent
                )
            ) {
                JuumaoApp()
            }
        }
    }
}

@Composable
fun JuumaoApp() {
    var screen by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Background
    ) {
        if (screen) {
            EditorScreen {
                screen = false
            }
        } else {
            HomeScreen {
                screen = true
            }
        }
    }
}

@Composable
fun HomeScreen(onCreate: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(22.dp)
    ) {
        Spacer(Modifier.height(30.dp))

        Text(
            "JUUMAO",
            color = TextPrimary,
            fontSize = 30.sp,
            fontWeight = FontWeight.Black
        )

        Text(
            "MOTION STUDIO",
            color = Accent,
            fontSize = 12.sp,
            letterSpacing = 3.sp
        )

        Spacer(Modifier.height(55.dp))

        Text(
            "Create something.",
            color = TextPrimary,
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            "Your motion graphics workspace.",
            color = TextSecondary,
            fontSize = 13.sp
        )

        Spacer(Modifier.height(25.dp))

        Button(
            onClick = onCreate,
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Accent
            ),
            shape = RoundedCornerShape(14.dp)
        ) {
            Icon(Icons.Default.Add, null, tint = Color.Black)
            Spacer(Modifier.width(8.dp))
            Text(
                "NEW PROJECT",
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.height(35.dp))

        Text(
            "RECENT PROJECTS",
            color = TextSecondary,
            fontSize = 12.sp
        )

        Spacer(Modifier.height(15.dp))

        Card(
            onClick = onCreate,
            colors = CardDefaults.cardColors(
                containerColor = SurfaceDark
            ),
            shape = RoundedCornerShape(14.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Movie,
                    null,
                    tint = Accent,
                    modifier = Modifier.size(30.dp)
                )

                Spacer(Modifier.width(15.dp))

                Column {
                    Text(
                        "My First Project",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "1080 × 1920 · 30 FPS",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

@Composable
fun EditorScreen(onBack: () -> Unit) {

    val layer = remember {
        MotionLayer(
            id = "layer-1",
            name = "JUUMAO TEXT",
            type = LayerType.TEXT
        )
    }

    var currentTime by remember {
        mutableStateOf(0L)
    }

    var isPlaying by remember {
        mutableStateOf(false)
    }

    var refresh by remember {
        mutableIntStateOf(0)
    }

    val duration = 5000L

    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            delay(16)

            if (currentTime >= duration) {
                currentTime = 0L
                isPlaying = false
            } else {
                currentTime += 16L
            }
        }
    }

    val transform = remember(currentTime, refresh) {
        layer.getTransform(currentTime)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {

        // TOP BAR

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.Default.ArrowBack,
                    null,
                    tint = TextPrimary
                )
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    "JUUMAO MOTION",
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    "Untitled Project",
                    color = TextSecondary,
                    fontSize = 10.sp
                )
            }

            Text(
                "1080p",
                color = Accent,
                fontSize = 12.sp
            )

            Spacer(Modifier.width(12.dp))

            Icon(
                Icons.Default.Save,
                null,
                tint = TextPrimary
            )

            Spacer(Modifier.width(10.dp))
        }

        // PREVIEW

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(290.dp)
                .background(Color(0xFF08090B)),
            contentAlignment = Alignment.Center
        ) {

            Box(
                modifier = Modifier
                    .fillMaxHeight(0.92f)
                    .aspectRatio(9f / 16f)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color(0xFF25272D)),
                contentAlignment = Alignment.Center
            ) {

                Box(
                    modifier = Modifier
                        .offset(
                            x = transform.x.dp,
                            y = transform.y.dp
                        )
                        .graphicsLayer {
                            scaleX = transform.scaleX
                            scaleY = transform.scaleY
                            rotationZ = transform.rotation
                            alpha = transform.opacity.coerceIn(0f, 1f)
                        }
                ) {
                    Text(
                        "JUUMAO",
                        color = Accent,
                        fontSize = 27.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }

        // PLAYBACK CONTROLS

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceDark)
                .padding(12.dp)
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "00:${(currentTime / 1000).toString().padStart(2, '0')}:${((currentTime % 1000) / 10).toString().padStart(2, '0')}",
                    color = Accent,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            currentTime = 0L
                        }
                    ) {
                        Icon(
                            Icons.Default.SkipPrevious,
                            null,
                            tint = TextPrimary
                        )
                    }

                    IconButton(
                        onClick = {
                            isPlaying = !isPlaying
                        }
                    ) {
                        Icon(
                            if (isPlaying)
                                Icons.Default.Pause
                            else
                                Icons.Default.PlayArrow,
                            null,
                            tint = Accent,
                            modifier = Modifier.size(30.dp)
                        )
                    }

                    IconButton(
                        onClick = {
                            currentTime = duration
                        }
                    ) {
                        Icon(
                            Icons.Default.SkipNext,
                            null,
                            tint = TextPrimary
                        )
                    }
                }
            }

            Text(
                "TIMELINE",
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )

            Slider(
                value = currentTime.toFloat(),
                onValueChange = {
                    currentTime = it.toLong()
                },
                valueRange = 0f..duration.toFloat(),
                colors = SliderDefaults.colors(
                    thumbColor = Accent,
                    activeTrackColor = Accent
                )
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Layers,
                    null,
                    tint = Accent
                )

                Spacer(Modifier.width(8.dp))

                Text(
                    "JUUMAO TEXT",
                    color = TextPrimary,
                    fontSize = 12.sp,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    "◆",
                    color = Accent
                )
            }

            HorizontalDivider(
                color = Color(0xFF303239),
                modifier = Modifier.padding(vertical = 12.dp)
            )

            // TRANSFORM CONTROLS

            Text(
                "TRANSFORM",
                color = Accent,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(12.dp))

            PropertySlider(
                title = "POSITION X",
                value = transform.x,
                range = -150f..150f,
                onValueChange = {
                    layer.positionX.addKeyframe(
                        currentTime,
                        it
                    )
                    refresh++
                }
            )

            PropertySlider(
                title = "POSITION Y",
                value = transform.y,
                range = -150f..150f,
                onValueChange = {
                    layer.positionY.addKeyframe(
                        currentTime,
                        it
                    )
                    refresh++
                }
            )

            PropertySlider(
                title = "SCALE",
                value = transform.scaleX,
                range = 0.2f..3f,
                onValueChange = {
                    layer.scaleX.addKeyframe(
                        currentTime,
                        it
                    )
                    layer.scaleY.addKeyframe(
                        currentTime,
                        it
                    )
                    refresh++
                }
            )

            PropertySlider(
                title = "ROTATION",
                value = transform.rotation,
                range = -180f..180f,
                onValueChange = {
                    layer.rotation.addKeyframe(
                        currentTime,
                        it
                    )
                    refresh++
                }
            )

            Spacer(Modifier.height(15.dp))

            Button(
                onClick = {
                    layer.positionX.addKeyframe(
                        currentTime,
                        transform.x
                    )
                    layer.positionY.addKeyframe(
                        currentTime,
                        transform.y
                    )
                    layer.scaleX.addKeyframe(
                        currentTime,
                        transform.scaleX
                    )
                    layer.scaleY.addKeyframe(
                        currentTime,
                        transform.scaleY
                    )
                    layer.rotation.addKeyframe(
                        currentTime,
                        transform.rotation
                    )
                    layer.opacity.addKeyframe(
                        currentTime,
                        transform.opacity
                    )

                    refresh++
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Accent
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    "◆  ADD KEYFRAME",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(15.dp))

            Text(
                "KEYFRAME COUNT: " +
                    layer.rotation.getKeyframes().size,
                color = TextSecondary,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
fun PropertySlider(
    title: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit
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
                color = TextSecondary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                "%.2f".format(value),
                color = TextPrimary,
                fontSize = 10.sp
            )
        }

        Slider(
            value = value.coerceIn(range),
            onValueChange = onValueChange,
            valueRange = range,
            colors = SliderDefaults.colors(
                thumbColor = Accent,
                activeTrackColor = Accent
            )
        )
    }
}
