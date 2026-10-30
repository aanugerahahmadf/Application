package com.wedding.fleurdecor.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wedding.fleurdecor.data.model.AppLanguage
import com.wedding.fleurdecor.data.model.Localization
import com.wedding.fleurdecor.data.model.SavedAccount
import com.wedding.fleurdecor.data.repository.AppRepository
import com.wedding.fleurdecor.ui.components.GlassSurface
import com.wedding.fleurdecor.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountSwitchModalSheet(
    repository: AppRepository,
    currentLanguage: AppLanguage,
    onDismiss: () -> Unit,
    onAccountSwitched: (String) -> Unit,
    onAddNewAccount: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accounts = repository.savedAccounts.value
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        dragHandle = { BottomSheetDefaults.DragHandle(color = WarmGray.copy(alpha = 0.5f)) },
        modifier = modifier.testTag("account_switch_modal_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 36.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(RoseGold.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SwapHoriz,
                        contentDescription = null,
                        tint = RoseGold
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = Localization.get("switch_account", currentLanguage),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Kelola dan beralih sesi akun tersimpan",
                        style = MaterialTheme.typography.bodySmall,
                        color = WarmGray
                    )
                }
            }

            HorizontalDivider(color = GlassBorderWhite)
            Spacer(modifier = Modifier.height(14.dp))

            // Stored Accounts List
            accounts.forEach { account ->
                val isActive = account.isActive

                GlassSurface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .border(
                            width = if (isActive) 1.5.dp else 1.dp,
                            color = if (isActive) RoseGold else GlassBorderWhite,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .clickable {
                            if (!isActive) {
                                val name = repository.switchToAccount(account.id)
                                onAccountSwitched(name)
                                onDismiss()
                            }
                        }
                        .testTag("account_item_${account.id}"),
                    backgroundColor = if (isActive) RoseGold.copy(alpha = 0.08f) else null,
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Avatar with Active Ring
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(RoseGold.copy(alpha = 0.2f))
                                .border(
                                    width = if (isActive) 2.dp else 0.dp,
                                    color = if (isActive) RoseGold else Color.Transparent,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = account.user.firstName.take(1).uppercase(),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = RoseGold
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = account.user.fullName,
                                style = MaterialTheme.typography.titleMedium.copy(fontSize = 15.sp),
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = account.user.email,
                                style = MaterialTheme.typography.bodySmall,
                                color = WarmGray
                            )
                        }

                        if (isActive) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(RoseGold),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Active",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = GlassBorderWhite)
            Spacer(modifier = Modifier.height(14.dp))

            // Add New Account Action
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .clickable {
                        onDismiss()
                        onAddNewAccount()
                    }
                    .padding(12.dp)
                    .testTag("btn_add_new_account"),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(EmeraldSuccess.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PersonAdd,
                        contentDescription = null,
                        tint = EmeraldSuccess,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = Localization.get("add_account", currentLanguage),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Masuk dengan email atau akun pengantin lain",
                        style = MaterialTheme.typography.bodySmall,
                        color = WarmGray
                    )
                }
            }
        }
    }
}
