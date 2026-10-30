package com.wedding.fleurdecor.ui.screens.chat

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wedding.fleurdecor.R
import com.wedding.fleurdecor.data.model.AppLanguage
import com.wedding.fleurdecor.data.model.ChatMessage
import com.wedding.fleurdecor.data.model.Localization
import com.wedding.fleurdecor.data.repository.AppRepository
import com.wedding.fleurdecor.ui.components.*
import com.wedding.fleurdecor.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun ChatScreen(
    repository: AppRepository,
    currentLanguage: AppLanguage,
    onBack: () -> Unit,
    onStartCall: () -> Unit,
    modifier: Modifier = Modifier
) {
    val messages = repository.chatMessages.value
    var textInput by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    GlassScaffold(
        modifier = modifier.fillMaxSize().testTag("chat_screen"),
        topBar = {
            GlassTopBar(
                title = Localization.get("chat_support", currentLanguage),
                onBack = onBack,
                actions = {
                    // Video Call Consultation Action
                    IconButton(
                        onClick = onStartCall,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(RoseGold.copy(alpha = 0.2f))
                            .testTag("btn_start_video_call")
                    ) {
                        Icon(imageVector = Icons.Default.Videocam, contentDescription = "Call", tint = RoseGold)
                    }
                },
                testTag = "top_bar_chat"
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Chat Messages List
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(top = 12.dp, bottom = 12.dp)
            ) {
                items(messages) { msg ->
                    ChatBubbleItem(message = msg)
                }
            }

            // Input Bar
            Surface(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                shadowElevation = 8.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorderWhite),
                modifier = Modifier.fillMaxWidth().imePadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            repository.sendMessage("Berikut referensi bunga impian saya 🌸", imageRes = R.drawable.bridal_bouquet_cbir)
                        },
                        modifier = Modifier.testTag("btn_chat_attach_photo")
                    ) {
                        Icon(imageVector = Icons.Default.AddPhotoAlternate, contentDescription = "Photo", tint = RoseGold)
                    }

                    OutlinedTextField(
                        value = textInput,
                        onValueChange = { textInput = it },
                        placeholder = { Text("Ketik pesan atau pertanyaan dekorasi...", style = MaterialTheme.typography.bodyMedium, color = WarmGray) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_chat_text"),
                        shape = RoundedCornerShape(20.dp),
                        singleLine = false,
                        maxLines = 3
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = {
                            if (textInput.isNotBlank()) {
                                repository.sendMessage(textInput.trim())
                                textInput = ""
                            }
                        },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(RoseGold)
                            .testTag("btn_chat_send")
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatBubbleItem(message: ChatMessage) {
    val isStaff = message.isStaff

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isStaff) Arrangement.Start else Arrangement.End
    ) {
        Column(
            horizontalAlignment = if (isStaff) Alignment.Start else Alignment.End,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(
                    topStart = 18.dp,
                    topEnd = 18.dp,
                    bottomStart = if (isStaff) 4.dp else 18.dp,
                    bottomEnd = if (isStaff) 18.dp else 4.dp
                ),
                color = if (isStaff) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f) else MaterialTheme.colorScheme.primary,
                border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorderWhite),
                shadowElevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    if (isStaff) {
                        Text(
                            text = message.senderName,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = RoseGold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                    }

                    if (message.imageRes != null) {
                        Image(
                            painter = painterResource(id = message.imageRes),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp)
                                .clip(RoundedCornerShape(12.dp))
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                    }

                    Text(
                        text = message.text,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isStaff) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = message.timestamp,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                color = WarmGray
            )
        }
    }
}
