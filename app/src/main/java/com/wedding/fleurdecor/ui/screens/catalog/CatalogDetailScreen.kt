package com.wedding.fleurdecor.ui.screens.catalog

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wedding.fleurdecor.R
import com.wedding.fleurdecor.data.model.*
import com.wedding.fleurdecor.data.repository.AppRepository
import com.wedding.fleurdecor.ui.components.*
import com.wedding.fleurdecor.ui.theme.*

@Composable
fun CatalogDetailScreen(
    catalogId: Long,
    repository: AppRepository,
    currentLanguage: AppLanguage,
    onBack: () -> Unit,
    onNavigateToCheckout: () -> Unit,
    onNavigateToChat: () -> Unit,
    modifier: Modifier = Modifier
) {
    val catalog = repository.catalogs.find { it.id == catalogId } ?: repository.catalogs[0]
    val wishlistIds = repository.wishlistIds.value
    val isFavorited = wishlistIds.contains(catalog.id)

    // User must choose a tier
    var selectedTierIndex by remember { mutableStateOf(0) }
    val selectedTier = catalog.tiers.getOrNull(selectedTierIndex) ?: catalog.tiers.first()

    var activeMediaIndex by remember { mutableStateOf(0) }
    var snackbarMessage by remember { mutableStateOf<String?>(null) }

    Box(modifier = modifier.fillMaxSize().testTag("catalog_detail_screen")) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
            // Media Gallery Carousel / Viewer
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(340.dp)
                ) {
                    Image(
                        painter = painterResource(id = catalog.mediaResList.getOrElse(activeMediaIndex) { catalog.imageRes }),
                        contentDescription = catalog.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Top Bar Overlay
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.85f))
                                .testTag("btn_detail_back")
                        ) {
                            Icon(
                                painter = painterResource(android.R.drawable.ic_menu_revert),
                                contentDescription = "Back",
                                tint = CharcoalDark
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Wishlist Button
                            IconButton(
                                onClick = { repository.toggleWishlist(catalog.id) },
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.85f))
                                    .testTag("btn_detail_wishlist")
                            ) {
                                Icon(
                                    imageVector = if (isFavorited) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                    contentDescription = "Wishlist",
                                    tint = if (isFavorited) RoseGold else CharcoalDark
                                )
                            }
                        }
                    }

                    // Media Thumbnail Selector Indicator
                    if (catalog.mediaResList.size > 1) {
                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 16.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color.Black.copy(alpha = 0.45f))
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            catalog.mediaResList.indices.forEach { index ->
                                Box(
                                    modifier = Modifier
                                        .size(if (index == activeMediaIndex) 10.dp else 8.dp)
                                        .clip(CircleShape)
                                        .background(if (index == activeMediaIndex) RoseGold else Color.White.copy(alpha = 0.5f))
                                        .clickable { activeMediaIndex = index }
                                )
                            }
                        }
                    }
                }
            }

            // Title, Category, Rating & Description
            item {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        StatusChip(
                            label = catalog.categoryName,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Rating",
                                tint = ChampagneGold,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${catalog.rating} (${catalog.reviewCount} ulasan)",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = catalog.title,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = catalog.theme,
                        style = MaterialTheme.typography.bodyMedium,
                        color = RoseGold,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = catalog.description,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // TIER / PACKAGE SELECTOR SECTION (Mandatory per PRD)
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = Localization.get("select_package", currentLanguage),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${catalog.tiers.size} Pilihan Tersedia",
                            style = MaterialTheme.typography.labelSmall,
                            color = WarmGray
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    catalog.tiers.forEachIndexed { index, tier ->
                        val isSelected = index == selectedTierIndex

                        GlassSurface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .clip(RoundedCornerShape(22.dp))
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) RoseGold else GlassBorderWhite,
                                    shape = RoundedCornerShape(22.dp)
                                )
                                .clickable { selectedTierIndex = index }
                                .testTag("tier_selector_${tier.id}"),
                            shape = RoundedCornerShape(22.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = tier.name,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = tier.capacity,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = WarmGray
                                        )
                                    }

                                    // Radio / Check Indicator
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(if (isSelected) RoseGold else Color.Transparent)
                                            .border(
                                                2.dp,
                                                if (isSelected) RoseGold else WarmGray.copy(alpha = 0.5f),
                                                CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isSelected) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Selected",
                                                tint = Color.White,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Price display from Tier
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    val finalPrice = tier.salePrice ?: tier.price
                                    Text(
                                        text = Localization.formatCurrency(finalPrice, currentLanguage),
                                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp),
                                        fontWeight = FontWeight.Bold,
                                        color = CharcoalDark
                                    )

                                    if (tier.salePrice != null) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = Localization.formatCurrency(tier.price, currentLanguage),
                                            style = MaterialTheme.typography.bodySmall,
                                            textDecoration = TextDecoration.LineThrough,
                                            color = WarmGray
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        StatusChip(label = "Sale", color = CrimsonError)
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = tier.description,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                // Features Included
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    tier.features.forEach { feature ->
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                tint = EmeraldSuccess,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = feature,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Floating Bottom Action Bar (Add to Cart & Book Now)
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding(),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
            shadowElevation = 16.dp,
            border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorderWhite)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Consult Florist Button
                IconButton(
                    onClick = onNavigateToChat,
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(RoseGold.copy(alpha = 0.15f))
                        .testTag("btn_detail_consult_florist")
                ) {
                    Icon(
                        imageVector = Icons.Default.ChatBubbleOutline,
                        contentDescription = "Consult Florist",
                        tint = RoseGold
                    )
                }

                // Add To Cart Button
                OutlinedButton(
                    onClick = {
                        repository.addToCart(catalog, selectedTier)
                        snackbarMessage = "Ditambahkan ke keranjang!"
                    },
                    shape = RoundedCornerShape(999.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("btn_detail_add_to_cart")
                ) {
                    Text(
                        text = Localization.get("add_to_cart", currentLanguage),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Book Now Button
                PrimaryCapsuleButton(
                    text = Localization.get("book_now", currentLanguage),
                    onClick = {
                        repository.addToCart(catalog, selectedTier)
                        onNavigateToCheckout()
                    },
                    modifier = Modifier.weight(1.2f),
                    testTag = "btn_detail_book_now"
                )
            }
        }

        // Snackbar feedback
        AnimatedVisibility(
            visible = snackbarMessage != null,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 60.dp)
        ) {
            Surface(
                color = CharcoalDark,
                shape = RoundedCornerShape(20.dp),
                shadowElevation = 8.dp,
                modifier = Modifier.padding(horizontal = 24.dp)
            ) {
                Text(
                    text = snackbarMessage ?: "",
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
