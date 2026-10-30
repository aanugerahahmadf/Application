package com.wedding.fleurdecor.ui.screens.settings

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.SettingsBrightness
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wedding.fleurdecor.data.model.AppLanguage
import com.wedding.fleurdecor.data.model.Localization
import com.wedding.fleurdecor.data.model.ThemeMode
import com.wedding.fleurdecor.data.repository.AppRepository
import com.wedding.fleurdecor.ui.components.*
import com.wedding.fleurdecor.ui.theme.*

@Composable
fun AppearanceScreen(
    repository: AppRepository,
    currentLanguage: AppLanguage,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentTheme = repository.themeMode.value

    GlassScaffold(
        modifier = modifier.fillMaxSize().testTag("appearance_screen"),
        topBar = {
            GlassTopBar(
                title = Localization.get("appearance", currentLanguage),
                onBack = onBack,
                testTag = "top_bar_appearance"
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
                text = "Pilih Tema Tampilan",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Pilih tema visual yang nyaman untuk melihat inspirasi bunga pernikahan Anda",
                style = MaterialTheme.typography.bodySmall,
                color = WarmGray
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Theme Options: Light, Dark, System
            ThemeOptionCard(
                mode = ThemeMode.Light,
                title = if (currentLanguage == AppLanguage.ID) "Terang (Light)" else "Light Mode",
                subtitle = "Nuansa Ivory, Blush, dan Champagne klasik",
                icon = Icons.Default.LightMode,
                isSelected = currentTheme == ThemeMode.Light,
                onClick = { repository.setThemeMode(ThemeMode.Light) },
                testTag = "theme_option_light"
            )

            Spacer(modifier = Modifier.height(10.dp))

            ThemeOptionCard(
                mode = ThemeMode.Dark,
                title = if (currentLanguage == AppLanguage.ID) "Gelap (Dark)" else "Dark Mode",
                subtitle = "Nuansa Charcoal dan Glassy Plum dramatis",
                icon = Icons.Default.DarkMode,
                isSelected = currentTheme == ThemeMode.Dark,
                onClick = { repository.setThemeMode(ThemeMode.Dark) },
                testTag = "theme_option_dark"
            )

            Spacer(modifier = Modifier.height(10.dp))

            ThemeOptionCard(
                mode = ThemeMode.System,
                title = if (currentLanguage == AppLanguage.ID) "Ikuti Sistem (System)" else "System Default",
                subtitle = "Menyesuaikan otomatis dengan pengaturan tema perangkat",
                icon = Icons.Default.SettingsBrightness,
                isSelected = currentTheme == ThemeMode.System,
                onClick = { repository.setThemeMode(ThemeMode.System) },
                testTag = "theme_option_system"
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Live Preview Card (Mandatory per PRD)
            Text(
                text = "Preview Elemen Glassy:",
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Fleur Décor Live Preview",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        StatusChip(label = "Active Glow", color = RoseGold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Permukaan frosted glass dengan perpaduan gradasi lembut dan kontras teks bersertifikasi WCAG AA.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun ThemeOptionCard(
    mode: ThemeMode,
    title: String,
    subtitle: String,
    icon: ImageVector,
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
            Spacer(modifier = Modifier.width(12.dp))
            Icon(imageVector = icon, contentDescription = null, tint = RoseGold)
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(text = title, style = MaterialTheme.typography.titleMedium.copy(fontSize = 15.sp), fontWeight = FontWeight.Bold)
                Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = WarmGray)
            }
        }
    }
}
