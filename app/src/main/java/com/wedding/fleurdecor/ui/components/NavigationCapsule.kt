package com.wedding.fleurdecor.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.ListAlt
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wedding.fleurdecor.data.model.AppLanguage
import com.wedding.fleurdecor.data.model.Localization
import com.wedding.fleurdecor.ui.theme.*

enum class BottomTab(val route: String) {
    HOME("home"),
    ORDERS("orders"),
    CART("cart"),
    PROFILE("profile")
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CapsuleBottomBar(
    currentTab: BottomTab,
    onTabSelected: (BottomTab) -> Unit,
    cartItemCount: Int,
    currentLanguage: AppLanguage,
    onProfileLongClick: () -> Unit,
    onProfileDoubleClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = MaterialTheme.colorScheme.background == DarkBackground

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(32.dp),
            color = if (isDark) DarkSurface.copy(alpha = 0.92f) else Color.White.copy(alpha = 0.92f),
            shadowElevation = 8.dp,
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (isDark) DarkGlassBorder else GlassBorderWhite
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
                .testTag("capsule_bottom_bar")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Tab: Home
                CapsuleTabItem(
                    selected = currentTab == BottomTab.HOME,
                    label = Localization.get("home", currentLanguage),
                    selectedIcon = Icons.Filled.Home,
                    unselectedIcon = Icons.Outlined.Home,
                    onClick = { onTabSelected(BottomTab.HOME) },
                    testTag = "tab_home"
                )

                // Tab: Orders
                CapsuleTabItem(
                    selected = currentTab == BottomTab.ORDERS,
                    label = Localization.get("orders", currentLanguage),
                    selectedIcon = Icons.Filled.ListAlt,
                    unselectedIcon = Icons.Outlined.ListAlt,
                    onClick = { onTabSelected(BottomTab.ORDERS) },
                    testTag = "tab_orders"
                )

                // Tab: Cart
                CapsuleTabItem(
                    selected = currentTab == BottomTab.CART,
                    label = Localization.get("cart", currentLanguage),
                    selectedIcon = Icons.Filled.ShoppingCart,
                    unselectedIcon = Icons.Outlined.ShoppingCart,
                    badgeCount = cartItemCount,
                    onClick = { onTabSelected(BottomTab.CART) },
                    testTag = "tab_cart"
                )

                // Tab: Profile (with Special Gestures: Long-Press for Sheet, Double-Tap for Quick Switch)
                CapsuleTabItem(
                    selected = currentTab == BottomTab.PROFILE,
                    label = Localization.get("profile", currentLanguage),
                    selectedIcon = Icons.Filled.Person,
                    unselectedIcon = Icons.Outlined.Person,
                    onClick = { onTabSelected(BottomTab.PROFILE) },
                    onLongClick = onProfileLongClick,
                    onDoubleClick = onProfileDoubleClick,
                    testTag = "tab_profile"
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CapsuleTabItem(
    selected: Boolean,
    label: String,
    selectedIcon: ImageVector,
    unselectedIcon: ImageVector,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    onDoubleClick: (() -> Unit)? = null,
    badgeCount: Int = 0,
    testTag: String
) {
    val iconColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.primary else WarmGray,
        label = "iconColor"
    )

    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .combinedClickable(
                interactionSource = interactionSource,
                indication = ripple(),
                onClick = onClick,
                onLongClick = onLongClick,
                onDoubleClick = onDoubleClick
            )
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box {
                Icon(
                    imageVector = if (selected) selectedIcon else unselectedIcon,
                    contentDescription = label,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )

                if (badgeCount > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 6.dp, y = (-4).dp)
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(RoseGold),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (badgeCount > 9) "9+" else badgeCount.toString(),
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium.copy(fontSize = 11.sp),
                color = iconColor,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
            )
        }
    }
}
