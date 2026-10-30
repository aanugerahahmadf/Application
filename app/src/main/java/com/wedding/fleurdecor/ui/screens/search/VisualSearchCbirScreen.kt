package com.wedding.fleurdecor.ui.screens.search

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Refresh
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wedding.fleurdecor.R
import com.wedding.fleurdecor.data.model.*
import com.wedding.fleurdecor.data.repository.AppRepository
import com.wedding.fleurdecor.ui.components.*
import com.wedding.fleurdecor.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun VisualSearchCbirScreen(
    repository: AppRepository,
    currentLanguage: AppLanguage,
    onBack: () -> Unit,
    onNavigateToCatalogDetail: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchStep by remember { mutableStateOf(0) } // 0: Select Source, 1: Scanning/AI Analysis, 2: Results
    var selectedImageRes by remember { mutableStateOf(R.drawable.bridal_bouquet_cbir) }
    var results by remember { mutableStateOf<List<CbirSearchResult>>(emptyList()) }

    // Scanner laser animation
    val infiniteTransition = rememberInfiniteTransition(label = "scanner")
    val laserOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laser"
    )

    // Trigger scanning delay
    LaunchedEffect(searchStep) {
        if (searchStep == 1) {
            delay(1500)
            results = repository.searchByImage()
            searchStep = 2
        }
    }

    GlassScaffold(
        modifier = modifier.fillMaxSize().testTag("visual_search_cbir_screen"),
        topBar = {
            GlassTopBar(
                title = Localization.get("cbir_title", currentLanguage),
                onBack = onBack,
                testTag = "top_bar_cbir"
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (searchStep) {
                // Step 0: Choose Camera / Gallery source
                0 -> {
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = "Temukan Dekorasi Bunga Impian",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Unggah foto buket bunga atau dekorasi pernikahan yang Anda sukai, sistem AI Content-Based Image Retrieval kami akan mencocokkan tema dan palet warnanya.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = WarmGray,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    // Sample Image Preview
                    Box(
                        modifier = Modifier
                            .size(240.dp)
                            .clip(RoundedCornerShape(32.dp))
                            .border(2.dp, RoseGold, RoundedCornerShape(32.dp))
                    ) {
                        Image(
                            painter = painterResource(id = selectedImageRes),
                            contentDescription = "Sample floral photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .background(Color.Black.copy(alpha = 0.5f))
                                .padding(vertical = 6.dp)
                        ) {
                            Text(
                                text = "Contoh Foto Bunga Pengantin",
                                color = Color.White,
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Buttons: Camera or Gallery
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                selectedImageRes = R.drawable.wedding_table_centerpiece
                                searchStep = 1
                            },
                            shape = RoundedCornerShape(999.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp)
                                .testTag("btn_cbir_gallery")
                        ) {
                            Icon(imageVector = Icons.Default.Collections, contentDescription = null, tint = RoseGold)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = Localization.get("gallery", currentLanguage), fontWeight = FontWeight.SemiBold)
                        }

                        PrimaryCapsuleButton(
                            text = Localization.get("camera", currentLanguage),
                            onClick = {
                                selectedImageRes = R.drawable.bridal_bouquet_cbir
                                searchStep = 1
                            },
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.CameraAlt, contentDescription = null, tint = Color.White)
                            },
                            modifier = Modifier.weight(1f),
                            testTag = "btn_cbir_camera"
                        )
                    }
                }

                // Step 1: Scanning / Feature Extraction Animation
                1 -> {
                    Spacer(modifier = Modifier.height(40.dp))
                    Text(
                        text = "Menganalisis Komposisi Flora...",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Mengekstraksi palet warna, kepadatan bunga, dan kemiripan tema",
                        style = MaterialTheme.typography.bodySmall,
                        color = WarmGray
                    )

                    Spacer(modifier = Modifier.height(30.dp))

                    Box(
                        modifier = Modifier
                            .size(260.dp)
                            .clip(RoundedCornerShape(32.dp))
                            .border(2.dp, RoseGold, RoundedCornerShape(32.dp))
                    ) {
                        Image(
                            painter = painterResource(id = selectedImageRes),
                            contentDescription = "Scanning",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        // Animated scanning laser line
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(3.dp)
                                .offset(y = (laserOffset * 250).dp)
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(Color.Transparent, RoseGold, Color.White, RoseGold, Color.Transparent)
                                    )
                                )
                        )
                    }

                    Spacer(modifier = Modifier.height(30.dp))
                    CircularProgressIndicator(color = RoseGold)
                }

                // Step 2: Similarity Ranking Results List
                2 -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Hasil Pencarian Visual",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Ditemukan ${results.size} dekorasi dengan palet serupa",
                                style = MaterialTheme.typography.bodySmall,
                                color = WarmGray
                            )
                        }

                        IconButton(
                            onClick = { searchStep = 0 },
                            modifier = Modifier.testTag("btn_cbir_retry")
                        ) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = "Retry", tint = RoseGold)
                        }
                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        contentPadding = PaddingValues(bottom = 40.dp)
                    ) {
                        items(results) { item ->
                            GlassSurface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(22.dp))
                                    .clickable { onNavigateToCatalogDetail(item.catalog.id) }
                                    .testTag("cbir_result_${item.id}"),
                                shape = RoundedCornerShape(22.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Image(
                                        painter = painterResource(id = item.catalog.imageRes),
                                        contentDescription = item.catalog.title,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(80.dp)
                                            .clip(RoundedCornerShape(16.dp))
                                    )

                                    Spacer(modifier = Modifier.width(14.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = item.catalog.title,
                                                style = MaterialTheme.typography.titleMedium.copy(fontSize = 15.sp),
                                                fontWeight = FontWeight.Bold,
                                                maxLines = 1,
                                                modifier = Modifier.weight(1f)
                                            )
                                            // Similarity Percentage Badge
                                            StatusChip(
                                                label = "${item.similarityPercent}% Mirip",
                                                color = if (item.similarityPercent >= 90) EmeraldSuccess else RoseGold
                                            )
                                        }

                                        Text(
                                            text = item.catalog.theme,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = WarmGray,
                                            maxLines = 1
                                        )

                                        Spacer(modifier = Modifier.height(4.dp))

                                        // Similarity Progress Bar Indicator
                                        LinearProgressIndicator(
                                            progress = { item.similarityPercent / 100f },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(6.dp)
                                                .clip(RoundedCornerShape(3.dp)),
                                            color = if (item.similarityPercent >= 90) EmeraldSuccess else RoseGold,
                                            trackColor = Color.LightGray.copy(alpha = 0.3f)
                                        )

                                        Spacer(modifier = Modifier.height(4.dp))

                                        Text(
                                            text = "${item.catalog.tiers.size} Tier Packages Tersedia",
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
        }
    }
}
