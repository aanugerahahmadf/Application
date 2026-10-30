package com.wedding.fleurdecor.ui.screens.chat

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wedding.fleurdecor.R
import com.wedding.fleurdecor.data.model.AppLanguage
import com.wedding.fleurdecor.ui.theme.*

@Composable
fun ConsultationCallScreen(
    currentLanguage: AppLanguage,
    onEndCall: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isMuted by remember { mutableStateOf(false) }
    var isVideoOff by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("consultation_call_screen")
    ) {
        // Full screen video background (Simulated florist studio)
        Image(
            painter = painterResource(id = R.drawable.wedding_hero_banner),
            contentDescription = "Florist Studio",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Scrim
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.6f),
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.8f)
                        )
                    )
                )
        )

        // Top Info
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Ratna Dewi, S.Ds.",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "Lead Wedding Floral Designer • Fleur Décor",
                style = MaterialTheme.typography.bodySmall,
                color = IvoryLight.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Surface(
                color = EmeraldSuccess.copy(alpha = 0.3f),
                shape = CircleShape
            ) {
                Text(
                    text = "● Terhubung (02:45)",
                    style = MaterialTheme.typography.labelSmall,
                    color = EmeraldSuccess,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Bottom Call Controls
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 36.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Mute Button
            IconButton(
                onClick = { isMuted = !isMuted },
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(if (isMuted) CrimsonError else Color.White.copy(alpha = 0.25f))
                    .testTag("btn_call_mute")
            ) {
                Icon(
                    imageVector = if (isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                    contentDescription = "Mute",
                    tint = Color.White
                )
            }

            // End Call Button
            IconButton(
                onClick = onEndCall,
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(CrimsonError)
                    .testTag("btn_call_end")
            ) {
                Icon(
                    imageVector = Icons.Default.CallEnd,
                    contentDescription = "End Call",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }

            // Video Toggle Button
            IconButton(
                onClick = { isVideoOff = !isVideoOff },
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(if (isVideoOff) CrimsonError else Color.White.copy(alpha = 0.25f))
                    .testTag("btn_call_video")
            ) {
                Icon(
                    imageVector = if (isVideoOff) Icons.Default.VideocamOff else Icons.Default.Videocam,
                    contentDescription = "Video",
                    tint = Color.White
                )
            }
        }
    }
}
