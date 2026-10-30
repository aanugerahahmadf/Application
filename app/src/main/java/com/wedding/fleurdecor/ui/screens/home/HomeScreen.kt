package com.wedding.fleurdecor.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wedding.fleurdecor.R
import com.wedding.fleurdecor.data.model.*
import com.wedding.fleurdecor.data.repository.AppRepository
import com.wedding.fleurdecor.ui.components.*
import com.wedding.fleurdecor.ui.theme.*

@Composable
fun HomeScreen(
    repository: AppRepository,
    currentLanguage: AppLanguage,
    onNavigateToCatalogDetail: (Long) -> Unit,
    onNavigateToVisualSearch: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToChat: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToOrders: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentUser = repository.currentUser.value
    val wishlistIds = repository.wishlistIds.value
    val unreadNotifs = repository.notifications.value.count { !it.isRead }
    val activeOrder = repository.orders.value.firstOrNull()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Top Header: User Greeting & Icons
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (currentUser != null) "Bonjour, ${currentUser.firstName} ✨" else "Bonjour, Guest ✨",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = Localization.get("app_tagline", currentLanguage),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Notifications with Badge
                        Box {
                            IconButton(
                                onClick = onNavigateToNotifications,
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f))
                                    .border(1.dp, GlassBorderWhite, CircleShape)
                                    .testTag("btn_notifications")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = "Notifications",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            if (unreadNotifs > 0) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .size(12.dp)
                                        .clip(CircleShape)
                                        .background(RoseGold)
                                )
                            }
                        }

                        // Chat Consultation
                        IconButton(
                            onClick = onNavigateToChat,
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f))
                                .border(1.dp, GlassBorderWhite, CircleShape)
                                .testTag("btn_chat_consultation")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChatBubble,
                                contentDescription = "Chat",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }

        // Search Bar with CBIR Camera Shortcut
        item {
            GlassSurface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .clickable(onClick = onNavigateToSearch)
                    .testTag("home_search_bar"),
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = WarmGray
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = Localization.get("search_hint", currentLanguage),
                        style = MaterialTheme.typography.bodyMedium,
                        color = WarmGray,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    // CBIR Visual Search Camera Icon
                    IconButton(
                        onClick = onNavigateToVisualSearch,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(RoseGold.copy(alpha = 0.15f))
                            .testTag("btn_home_cbir_camera")
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "CBIR Visual Search",
                            tint = RoseGold,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // Categories Row
        item {
            Column(modifier = Modifier.padding(top = 16.dp)) {
                Text(
                    text = Localization.get("categories", currentLanguage),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(repository.categories) { category ->
                        GlassSurface(
                            modifier = Modifier
                                .width(130.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .clickable {
                                    val cat = repository.catalogs.firstOrNull { it.categoryId == category.id }
                                    if (cat != null) onNavigateToCatalogDetail(cat.id)
                                }
                                .testTag("category_card_${category.id}"),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Image(
                                    painter = painterResource(id = category.imageRes),
                                    contentDescription = category.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(80.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = category.name,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "${category.catalogCount} Collections",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = WarmGray
                                )
                            }
                        }
                    }
                }
            }
        }

        // Hero Editorial Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 18.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .clickable { onNavigateToCatalogDetail(1L) }
                    .testTag("home_hero_editorial")
            ) {
                Image(
                    painter = painterResource(id = R.drawable.wedding_hero_banner),
                    contentDescription = "The Royal Parisian Blossom Altar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(210.dp)
                )
                // Gradient Scrim
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color(0xCC1E1A1B))
                            )
                        )
                )
                // Banner Content
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(18.dp)
                ) {
                    StatusChip(
                        label = "Featured Masterpiece",
                        color = RoseGold,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    Text(
                        text = "The Royal Parisian Blossom Altar",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "3 Curated Tier Packages • European Elegance",
                        style = MaterialTheme.typography.bodySmall,
                        color = IvoryLight.copy(alpha = 0.85f)
                    )
                }
            }
        }

        // Active Order / Booking Reminder (if available)
        if (activeOrder != null) {
            item {
                GlassSurface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .clickable(onClick = onNavigateToOrders)
                        .testTag("home_active_order_banner"),
                    shape = RoundedCornerShape(22.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(RoseGold.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "ORD",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = RoseGold
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = activeOrder.orderNumber,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                StatusChip(
                                    label = if (activeOrder.paymentStatus == PaymentStatus.Paid) "Paid" else "Pending",
                                    color = if (activeOrder.paymentStatus == PaymentStatus.Paid) EmeraldSuccess else AmberWarning
                                )
                            }
                            Text(
                                text = "Acara: ${activeOrder.eventDate}",
                                style = MaterialTheme.typography.bodySmall,
                                color = WarmGray
                            )
                        }
                        Text(
                            text = "Detail >",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // Featured Collections Carousel
        item {
            Column(modifier = Modifier.padding(top = 16.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = Localization.get("featured_decor", currentLanguage),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = Localization.get("view_all", currentLanguage),
                        style = MaterialTheme.typography.labelMedium,
                        color = RoseGold,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable { onNavigateToCatalogDetail(1L) }
                    )
                }

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(repository.catalogs) { catalog ->
                        val isFavorited = wishlistIds.contains(catalog.id)

                        GlassSurface(
                            modifier = Modifier
                                .width(240.dp)
                                .clip(RoundedCornerShape(24.dp))
                                .clickable { onNavigateToCatalogDetail(catalog.id) }
                                .testTag("catalog_card_${catalog.id}"),
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(140.dp)
                                        .clip(RoundedCornerShape(18.dp))
                                ) {
                                    Image(
                                        painter = painterResource(id = catalog.imageRes),
                                        contentDescription = catalog.title,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )

                                    // Wishlist Toggle
                                    IconButton(
                                        onClick = { repository.toggleWishlist(catalog.id) },
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(6.dp)
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(Color.White.copy(alpha = 0.85f))
                                            .testTag("btn_wishlist_${catalog.id}")
                                    ) {
                                        Icon(
                                            imageVector = if (isFavorited) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                            contentDescription = "Wishlist",
                                            tint = if (isFavorited) RoseGold else CharcoalDark,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    if (catalog.isOnSale) {
                                        StatusChip(
                                            label = "Special Offer",
                                            color = CrimsonError,
                                            modifier = Modifier
                                                .align(Alignment.BottomStart)
                                                .padding(8.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = catalog.title,
                                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 16.sp),
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Text(
                                    text = catalog.theme,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = WarmGray,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = "Rating",
                                            tint = ChampagneGold,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = "${catalog.rating} (${catalog.reviewCount})",
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }

                                    // PRD RULE: Catalog Card does NOT show direct price, shows tier availability!
                                    Text(
                                        text = "${catalog.tiers.size} Tier Packages",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // CBIR Visual Search Callout Banner
        item {
            GlassSurface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 18.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .clickable(onClick = onNavigateToVisualSearch)
                    .testTag("home_cbir_callout_banner"),
                shape = RoundedCornerShape(24.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(RoseGold.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Camera",
                            tint = RoseGold,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = Localization.get("cbir_title", currentLanguage),
                            style = MaterialTheme.typography.titleMedium.copy(fontSize = 16.sp),
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = Localization.get("cbir_subtitle", currentLanguage),
                            style = MaterialTheme.typography.bodySmall,
                            color = WarmGray
                        )
                    }
                }
            }
        }
    }
}
