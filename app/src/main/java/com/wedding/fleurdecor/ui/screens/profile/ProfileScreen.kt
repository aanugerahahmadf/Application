package com.wedding.fleurdecor.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wedding.fleurdecor.data.model.*
import com.wedding.fleurdecor.data.repository.AppRepository
import com.wedding.fleurdecor.ui.components.*
import com.wedding.fleurdecor.ui.theme.*

@Composable
fun ProfileScreen(
    repository: AppRepository,
    currentLanguage: AppLanguage,
    onNavigateToKyc: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToLanguage: () -> Unit,
    onNavigateToAppearance: () -> Unit,
    onOpenAccountSheet: () -> Unit,
    onLockApp: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val user = repository.currentUser.value

    GlassScaffold(
        modifier = modifier.fillMaxSize().testTag("profile_screen"),
        topBar = {
            GlassTopBar(
                title = Localization.get("profile", currentLanguage),
                testTag = "top_bar_profile"
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 10.dp, bottom = 90.dp)
        ) {
            // User Header Card
            item {
                GlassSurface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(26.dp))
                        .testTag("profile_user_card"),
                    shape = RoundedCornerShape(26.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(68.dp)
                                    .clip(CircleShape)
                                    .background(RoseGold.copy(alpha = 0.2f))
                                    .border(2.dp, RoseGold, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = user?.firstName?.take(1)?.uppercase() ?: "G",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = RoseGold
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = user?.fullName ?: "Tamu / Guest",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = user?.email ?: "Belum masuk akun",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = WarmGray
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                StatusChip(
                                    label = if (user?.kycStatus == KycStatus.Approved) "KYC Terverifikasi" else "KYC Belum Lengkap",
                                    color = if (user?.kycStatus == KycStatus.Approved) EmeraldSuccess else AmberWarning
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = GlassBorderWhite)
                        Spacer(modifier = Modifier.height(12.dp))

                        // Profile Completion Bar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Kelengkapan Profil Pengantin",
                                style = MaterialTheme.typography.bodySmall,
                                color = WarmGray
                            )
                            Text(
                                text = "${user?.completionPercent ?: 50}%",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = RoseGold
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { (user?.completionPercent ?: 50) / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = RoseGold,
                            trackColor = Color.LightGray.copy(alpha = 0.3f)
                        )
                    }
                }
            }

            // KYC Banner
            item {
                GlassSurface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(22.dp))
                        .clickable(onClick = onNavigateToKyc)
                        .testTag("profile_kyc_banner"),
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
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(RoseGold.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = RoseGold)
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = Localization.get("kyc_verification", currentLanguage),
                                style = MaterialTheme.typography.titleMedium.copy(fontSize = 15.sp),
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = Localization.get("kyc_desc", currentLanguage),
                                style = MaterialTheme.typography.bodySmall,
                                color = WarmGray
                            )
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = WarmGray)
                    }
                }
            }

            // Account & Preferences Section
            item {
                GlassSurface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp)),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Pengaturan & Preferensi",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        ProfileMenuItem(
                            icon = Icons.Default.SwapHoriz,
                            title = Localization.get("switch_account", currentLanguage),
                            subtitle = "Ganti sesi atau tambah akun (Bisa via Long-Press tab Profil)",
                            onClick = onOpenAccountSheet,
                            testTag = "menu_switch_account"
                        )

                        ProfileMenuItem(
                            icon = Icons.Default.Palette,
                            title = Localization.get("appearance", currentLanguage),
                            subtitle = "Tema Terang, Gelap, atau Ikuti Sistem",
                            onClick = onNavigateToAppearance,
                            testTag = "menu_appearance"
                        )

                        ProfileMenuItem(
                            icon = Icons.Default.Translate,
                            title = Localization.get("language", currentLanguage),
                            subtitle = if (currentLanguage == AppLanguage.ID) "Bahasa Indonesia (Aktif)" else "English (Active)",
                            onClick = onNavigateToLanguage,
                            testTag = "menu_language"
                        )

                        ProfileMenuItem(
                            icon = Icons.Default.Lock,
                            title = Localization.get("app_lock", currentLanguage),
                            subtitle = "Kunci aplikasi dengan PIN keamanan",
                            onClick = onLockApp,
                            testTag = "menu_app_lock"
                        )
                    }
                }
            }

            // Sign Out Button
            item {
                OutlinedButton(
                    onClick = onLogout,
                    shape = RoundedCornerShape(999.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CrimsonError),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("btn_profile_logout")
                ) {
                    Icon(imageVector = Icons.Default.Logout, contentDescription = null, tint = CrimsonError)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = Localization.get("logout", currentLanguage), fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp, horizontal = 4.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = WarmGray)
        }
        Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = WarmGray)
    }
}
