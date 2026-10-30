package com.wedding.fleurdecor.ui.screens.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wedding.fleurdecor.data.model.AppLanguage
import com.wedding.fleurdecor.data.model.NotificationItem
import com.wedding.fleurdecor.data.repository.AppRepository
import com.wedding.fleurdecor.ui.components.*
import com.wedding.fleurdecor.ui.theme.*

@Composable
fun NotificationsScreen(
    repository: AppRepository,
    currentLanguage: AppLanguage,
    onBack: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onNavigateToChat: () -> Unit,
    modifier: Modifier = Modifier
) {
    val notifications = repository.notifications.value

    GlassScaffold(
        modifier = modifier.fillMaxSize().testTag("notifications_screen"),
        topBar = {
            GlassTopBar(
                title = "Notifikasi",
                onBack = onBack,
                actions = {
                    IconButton(
                        onClick = { repository.markNotificationsRead() },
                        modifier = Modifier.testTag("btn_mark_all_read")
                    ) {
                        Icon(imageVector = Icons.Default.DoneAll, contentDescription = "Mark Read", tint = RoseGold)
                    }
                },
                testTag = "top_bar_notifications"
            )
        }
    ) { padding ->
        if (notifications.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Belum ada notifikasi baru", style = MaterialTheme.typography.bodyMedium, color = WarmGray)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(top = 10.dp, bottom = 30.dp)
            ) {
                items(notifications) { notif ->
                    GlassSurface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .clickable {
                                when (notif.type) {
                                    "order" -> onNavigateToOrders()
                                    "consultation" -> onNavigateToChat()
                                    else -> {}
                                }
                            }
                            .testTag("notification_item_${notif.id}"),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            val icon = when (notif.type) {
                                "order" -> Icons.Default.ShoppingBag
                                "promo" -> Icons.Default.LocalOffer
                                else -> Icons.Default.Chat
                            }

                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(RoseGold.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = icon, contentDescription = null, tint = RoseGold, modifier = Modifier.size(20.dp))
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = notif.title,
                                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 15.sp),
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = notif.timestamp,
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                        color = WarmGray
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = notif.body,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
