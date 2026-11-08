
package com.juumao.motion

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.VideoView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

private val Dark = Color(0xFF101114)
private val Panel = Color(0xFF1B1D22)
private val Accent = Color(0xFFB6FF4A)
private val Muted = Color(0xFF858994)
private val Card = Color(0xFF24262B)

private data class ImportedMedia(
    val uri: Uri,
    val type: MediaType
)

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor =
            android.graphics.Color.rgb(16, 17, 20)

        window.navigationBarColor =
            android.graphics.Color.rgb(16, 17, 20)

        setContent {
            MaterialTheme(
                colorScheme = darkColorScheme(
                    background = Dark,
                    surface = Panel,
                    primary = Accent,
                    secondary = Color(0xFF8BC34A),
                    onPrimary = Color.Black,
                    onBackground = Color.White,
                    onSurface = Color.White
                )
            ) {
                JuumaoEditor()
            }
        }
    }
}

@Composable
fun JuumaoEditor() {

    val context = LocalContext.current

    val project = remember {
        MotionProject().apply {
            addTextLayer()
        }
    }

    var revision by remember {
        mutableIntStateOf(0)
    }

    var currentTime by remember {
        mutableLongStateOf(0L)
    }

    var playing by remember {
        mutableStateOf(false)
    }

    var mediaMap by remember {
        mutableStateOf<Map<String, ImportedMedia>>(emptyMap())
    }

    var pendingMediaType by remember {
        mutableStateOf(MediaType.IMAGE)
    }

    val layers = remember(revision) {
        project.getLayers()
    }

    val selectedLayer = remember(revision) {
        project.getSelectedLayer()
    }

    val selectedMedia = selectedLayer?.let {
        mediaMap[it.id]
    }

    // MEDIA PICKER

    val mediaPicker =
        rememberLauncherForActivityResult(
            ActivityResultContracts.OpenDocument()
        ) { uri ->

            if (uri != null) {

                try {
                    context.contentResolver
                        .takePersistableUriPermission(
                            uri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION
                        )
                } catch (_: Exception) {
                    // Some providers do not support
                    // persistable URI permission.
                }

                when (pendingMediaType) {

                    MediaType.IMAGE -> {
                        project.addImageLayer()
                    }

                    MediaType.VIDEO -> {
                        project.addVideoLayer()
                    }

                    MediaType.AUDIO -> {
                        project.addAudioLayer()
                    }
                }

                val newLayer =
                    project.getSelectedLayer()

                if (newLayer != null) {
                    mediaMap = mediaMap +
                        (newLayer.id to ImportedMedia(
                            uri = uri,
                            type = pendingMediaType
                        ))
                }

                revision++
            }
        }

    fun importMedia(type: MediaType) {
        pendingMediaType = type

        val mimeTypes = when (type) {
            MediaType.IMAGE -> arrayOf("image/*")
            MediaType.VIDEO -> arrayOf("video/*")
            MediaType.AUDIO -> arrayOf("audio/*")
        }

        mediaPicker.launch(mimeTypes)
    }

    // PLAYBACK ENGINE

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

    // MAIN EDITOR

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

            horizontalArrangement =
                Arrangement.SpaceBetween
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
                "2.0",
                color = Muted,
                fontSize = 12.sp
            )
        }

        // PREVIEW CANVAS

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

                val transform =
                    layer.getTransform(currentTime)

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

                        val media = mediaMap[layer.id]

                        if (
                            media != null &&
                            layer.id == project.selectedLayerId
                        ) {
                            MediaPreview(
                                media = media
                            )
                        } else if (media == null) {

                            Text(
                                layer.name,
                                color = Muted,
                                fontSize = 12.sp
                            )
                        }
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

        // PLAYBACK CONTROL

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),

            verticalAlignment = Alignment.CenterVertically,

            horizontalArrangement =
                Arrangement.SpaceBetween
        ) {

            Text(
                String.format(
                    "%.2fs",
                    currentTime / 1000f
                ),
                color = Color.White,
                fontSize = 12.sp
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                TextButton(
                    onClick = {
                        currentTime = 0L
                    }
                ) {
                    Text(
                        "RESET",
                        color = Muted
                    )
                }

                Button(
                    onClick = {
                        if (
                            currentTime >= project.duration
                        ) {
                            currentTime = 0L
                        }

                        playing = !playing
                    },

                    colors = ButtonDefaults.buttonColors(
                        containerColor = Accent,
                        contentColor = Color.Black
                    )
                ) {

                    Text(
                        if (playing) "PAUSE" else "PLAY"
                    )
                }
            }

            Text(
                String.format(
                    "%.2fs",
                    project.duration / 1000f
                ),
                color = Muted,
                fontSize = 12.sp
            )
        }

        // TIME SCRUBBER

        Slider(
            value = currentTime.toFloat(),

            onValueChange = {
                currentTime = it.toLong()
            },

            valueRange =
                0f..project.duration.toFloat(),

            colors = SliderDefaults.colors(
                thumbColor = Accent,
                activeTrackColor = Accent,
                inactiveTrackColor = Panel
            ),

            modifier = Modifier.padding(
                horizontal = 12.dp
            )
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

                horizontalArrangement =
                    Arrangement.SpaceBetween,

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

            // ADD LAYER BUTTONS

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(
                        rememberScrollState()
                    ),

                horizontalArrangement =
                    Arrangement.spacedBy(6.dp)
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
                    importMedia(MediaType.IMAGE)
                }

                AddLayerButton("VIDEO") {
                    importMedia(MediaType.VIDEO)
                }

                AddLayerButton("AUDIO") {
                    importMedia(MediaType.AUDIO)
                }
            }

            Spacer(Modifier.height(8.dp))

            // LAYER LIST

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(
                        rememberScrollState()
                    )
            ) {

                layers.forEach { layer ->

                    val selected =
                        project.selectedLayerId == layer.id

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                            .clip(
                                RoundedCornerShape(8.dp)
                            )
                            .background(
                                if (selected) {
                                    Color(0xFF303A25)
                                } else {
                                    Card
                                }
                            )
                            .clickable {

                                project.selectLayer(layer.id)

                                revision++
                            }
                            .padding(11.dp),

                        verticalAlignment =
                            Alignment.CenterVertically,

                        horizontalArrangement =
                            Arrangement.SpaceBetween
                    ) {

                        Column {

                            Text(
                                layer.name,

                                color = if (selected) {
                                    Accent
                                } else {
                                    Color.White
                                },

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
                .verticalScroll(
                    rememberScrollState()
                )
                .padding(16.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.SpaceBetween,

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
                                it.positionX.getValueAt(
                                    currentTime
                                )
                            )

                            it.positionY.addKeyframe(
                                currentTime,
                                it.positionY.getValueAt(
                                    currentTime
                                )
                            )

                            it.scaleX.addKeyframe(
                                currentTime,
                                it.scaleX.getValueAt(
                                    currentTime
                                )
                            )

                            it.scaleY.addKeyframe(
                                currentTime,
                                it.scaleY.getValueAt(
                                    currentTime
                                )
                            )

                            it.rotation.addKeyframe(
                                currentTime,
                                it.rotation.getValueAt(
                                    currentTime
                                )
                            )

                            it.opacity.addKeyframe(
                                currentTime,
                                it.opacity.getValueAt(
                                    currentTime
                                )
                            )
                        }

                        revision++
                    }
                ) {

                    Text(
                        "＋ KEYFRAME",
                        color = Accent
                    )
                }
            }

            if (selectedLayer != null) {

                PropertySlider(
                    "POSITION X",
                    selectedLayer.positionX.getValueAt(
                        currentTime
                    ),
                    -300f,
                    300f
                ) {

                    selectedLayer.positionX.addKeyframe(
                        currentTime,
                        it
                    )

                    revision++
                }

                PropertySlider(
                    "POSITION Y",
                    selectedLayer.positionY.getValueAt(
                        currentTime
                    ),
                    -300f,
                    300f
              
