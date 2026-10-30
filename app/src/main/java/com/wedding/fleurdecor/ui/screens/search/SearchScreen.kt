package com.wedding.fleurdecor.ui.screens.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wedding.fleurdecor.data.model.AppLanguage
import com.wedding.fleurdecor.data.model.Localization
import com.wedding.fleurdecor.data.repository.AppRepository
import com.wedding.fleurdecor.ui.components.*
import com.wedding.fleurdecor.ui.theme.*

@Composable
fun SearchScreen(
    repository: AppRepository,
    currentLanguage: AppLanguage,
    onBack: () -> Unit,
    onNavigateToCatalogDetail: (Long) -> Unit,
    onNavigateToVisualSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf<String?>(null) }

    val filteredCatalogs = remember(searchQuery, selectedCategoryFilter) {
        repository.catalogs.filter { catalog ->
            val matchText = searchQuery.isBlank() ||
                    catalog.title.contains(searchQuery, ignoreCase = true) ||
                    catalog.theme.contains(searchQuery, ignoreCase = true) ||
                    catalog.categoryName.contains(searchQuery, ignoreCase = true)
            val matchCat = selectedCategoryFilter == null || catalog.categoryName == selectedCategoryFilter
            matchText && matchCat
        }
    }

    GlassScaffold(
        modifier = modifier.fillMaxSize().testTag("search_screen"),
        topBar = {
            GlassTopBar(
                title = Localization.get("catalog", currentLanguage),
                onBack = onBack,
                testTag = "top_bar_search"
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
        ) {
            // Search Text Field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_search_query"),
                shape = RoundedCornerShape(20.dp),
                placeholder = { Text(Localization.get("search_hint", currentLanguage), color = WarmGray) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = WarmGray) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear", tint = WarmGray)
                        }
                    }
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = RoseGold,
                    unfocusedBorderColor = GlassBorderWhite,
                    focusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Quick Category Filters
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterCapsule(
                        label = "Semua",
                        selected = selectedCategoryFilter == null,
                        onClick = { selectedCategoryFilter = null }
                    )
                }
                items(repository.categories) { cat ->
                    FilterCapsule(
                        label = cat.name,
                        selected = selectedCategoryFilter == cat.name,
                        onClick = {
                            selectedCategoryFilter = if (selectedCategoryFilter == cat.name) null else cat.name
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Results List
            if (filteredCatalogs.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Tidak ada dekorasi yang cocok",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Coba kata kunci lain atau gunakan Pencarian Visual",
                            style = MaterialTheme.typography.bodySmall,
                            color = WarmGray
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        PrimaryCapsuleButton(
                            text = Localization.get("visual_search", currentLanguage),
                            onClick = onNavigateToVisualSearch
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(bottom = 30.dp)
                ) {
                    items(filteredCatalogs) { catalog ->
                        GlassSurface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(22.dp))
                                .clickable { onNavigateToCatalogDetail(catalog.id) }
                                .testTag("search_result_${catalog.id}"),
                            shape = RoundedCornerShape(22.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Image(
                                    painter = painterResource(id = catalog.imageRes),
                                    contentDescription = catalog.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(80.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                )
                                Spacer(modifier = Modifier.width(14.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    StatusChip(label = catalog.categoryName, color = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = catalog.title,
                                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 15.sp),
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = catalog.theme,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = WarmGray
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "${catalog.tiers.size} Tier Packages Pilihan",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = RoseGold,
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
