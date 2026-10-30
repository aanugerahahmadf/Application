package com.wedding.fleurdecor.ui.screens.settings

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wedding.fleurdecor.data.model.AppLanguage
import com.wedding.fleurdecor.data.model.Localization
import com.wedding.fleurdecor.data.repository.AppRepository
import com.wedding.fleurdecor.ui.components.*
import com.wedding.fleurdecor.ui.theme.*

@Composable
fun LanguageScreen(
    repository: AppRepository,
    currentLanguage: AppLanguage,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassScaffold(
        modifier = modifier.fillMaxSize().testTag("language_screen"),
        topBar = {
            GlassTopBar(
                title = Localization.get("language", currentLanguage),
                onBack = onBack,
                testTag = "top_bar_language"
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Pilih Bahasa Aplikasi",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Perubahan bahasa akan langsung diterapkan pada seluruh katalog, navigasi, dan rincian transaksi",
                style = MaterialTheme.typography.bodySmall,
                color = WarmGray
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Indonesian Language Option
            LanguageOptionCard(
                language = AppLanguage.ID,
                title = "Bahasa Indonesia",
                subtitle = "Indonesian (Default)",
                isSelected = currentLanguage == AppLanguage.ID,
                onClick = { repository.setLanguage(AppLanguage.ID) },
                testTag = "lang_option_id"
            )

            Spacer(modifier = Modifier.height(12.dp))

            // English Language Option
            LanguageOptionCard(
                language = AppLanguage.EN,
                title = "English",
                subtitle = "English (International)",
                isSelected = currentLanguage == AppLanguage.EN,
                onClick = { repository.setLanguage(AppLanguage.EN) },
                testTag = "lang_option_en"
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Sample Live Text Preview
            Text(
                text = "Contoh Tampilan Bahasa Terpilih:",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))

            GlassSurface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp)),
                shape = RoundedCornerShape(22.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = Localization.get("app_title", currentLanguage),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = Localization.get("app_tagline", currentLanguage),
                        style = MaterialTheme.typography.bodyMedium,
                        color = WarmGray
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        StatusChip(label = Localization.get("home", currentLanguage), color = RoseGold)
                        StatusChip(label = Localization.get("orders", currentLanguage), color = MaterialTheme.colorScheme.primary)
                        StatusChip(label = Localization.get("cart", currentLanguage), color = EmeraldSuccess)
                    }
                }
            }
        }
    }
}

@Composable
private fun LanguageOptionCard(
    language: AppLanguage,
    title: String,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    GlassSurface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) RoseGold else GlassBorderWhite,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick)
            .testTag(testTag),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(selectedColor = RoseGold)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.titleMedium.copy(fontSize = 15.sp), fontWeight = FontWeight.Bold)
                Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = WarmGray)
            }
            if (isSelected) {
                Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = RoseGold)
            }
        }
    }
}
