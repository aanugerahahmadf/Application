package com.wedding.fleurdecor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.wedding.fleurdecor.data.model.AppLanguage
import com.wedding.fleurdecor.data.repository.AppRepository
import com.wedding.fleurdecor.ui.components.BottomTab
import com.wedding.fleurdecor.ui.components.CapsuleBottomBar
import com.wedding.fleurdecor.ui.components.GlassScaffold
import com.wedding.fleurdecor.ui.screens.auth.AppLockScreen
import com.wedding.fleurdecor.ui.screens.auth.AuthSheetHost
import com.wedding.fleurdecor.ui.screens.cart.CartScreen
import com.wedding.fleurdecor.ui.screens.cart.CheckoutScreen
import com.wedding.fleurdecor.ui.screens.catalog.CatalogDetailScreen
import com.wedding.fleurdecor.ui.screens.chat.ChatScreen
import com.wedding.fleurdecor.ui.screens.chat.ConsultationCallScreen
import com.wedding.fleurdecor.ui.screens.home.HomeScreen
import com.wedding.fleurdecor.ui.screens.notifications.NotificationsScreen
import com.wedding.fleurdecor.ui.screens.orders.OrderDetailScreen
import com.wedding.fleurdecor.ui.screens.orders.OrdersListScreen
import com.wedding.fleurdecor.ui.screens.orders.PaymentInstructionScreen
import com.wedding.fleurdecor.ui.screens.profile.AccountSwitchModalSheet
import com.wedding.fleurdecor.ui.screens.profile.KycScannerScreen
import com.wedding.fleurdecor.ui.screens.profile.ProfileScreen
import com.wedding.fleurdecor.ui.screens.search.SearchScreen
import com.wedding.fleurdecor.ui.screens.search.VisualSearchCbirScreen
import com.wedding.fleurdecor.ui.screens.settings.AppearanceScreen
import com.wedding.fleurdecor.ui.screens.settings.LanguageScreen
import com.wedding.fleurdecor.ui.theme.FleurDecorTheme
import kotlinx.coroutines.launch

sealed class Screen {
    object Home : Screen()
    object Orders : Screen()
    object Cart : Screen()
    object Profile : Screen()
    data class CatalogDetail(val catalogId: Long) : Screen()
    object VisualSearch : Screen()
    object Search : Screen()
    object Checkout : Screen()
    data class PaymentInstruction(val orderId: Long) : Screen()
    data class OrderDetail(val orderId: Long) : Screen()
    object KycScanner : Screen()
    object Appearance : Screen()
    object Language : Screen()
    object Chat : Screen()
    object ConsultationCall : Screen()
    object Notifications : Screen()
}

class MainActivity : ComponentActivity() {

    private val repository = AppRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val themeMode by repository.themeMode.collectAsState()
            val currentLanguage by repository.currentLanguage.collectAsState()
            val isAppLocked by repository.isAppLocked.collectAsState()
            val cartItems by repository.cartItems.collectAsState()

            var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }
            var currentTab by remember { mutableStateOf(BottomTab.HOME) }

            var showAccountSheet by remember { mutableStateOf(false) }
            var showAuthSheet by remember { mutableStateOf(false) }

            val snackbarHostState = remember { SnackbarHostState() }
            val coroutineScope = rememberCoroutineScope()

            FleurDecorTheme(themeMode = themeMode) {
                if (isAppLocked) {
                    AppLockScreen(
                        repository = repository,
                        currentLanguage = currentLanguage,
                        onUnlocked = { /* unlocked */ }
                    )
                } else {
                    // Back handler: returns to Home or previous screen
                    BackHandler(enabled = currentScreen !is Screen.Home) {
                        when (currentScreen) {
                            is Screen.CatalogDetail, is Screen.VisualSearch, is Screen.Search,
                            is Screen.Checkout, is Screen.PaymentInstruction, is Screen.OrderDetail,
                            is Screen.KycScanner, is Screen.Appearance, is Screen.Language,
                            is Screen.Chat, is Screen.ConsultationCall, is Screen.Notifications -> {
                                currentScreen = Screen.Home
                                currentTab = BottomTab.HOME
                            }
                            is Screen.Orders, is Screen.Cart, is Screen.Profile -> {
                                currentScreen = Screen.Home
                                currentTab = BottomTab.HOME
                            }
                            else -> {}
                        }
                    }

                    val isRootTab = currentScreen is Screen.Home ||
                            currentScreen is Screen.Orders ||
                            currentScreen is Screen.Cart ||
                            currentScreen is Screen.Profile

                    GlassScaffold(
                        modifier = Modifier.fillMaxSize().testTag("main_activity_root"),
                        bottomBar = {
                            if (isRootTab) {
                                CapsuleBottomBar(
                                    currentTab = currentTab,
                                    onTabSelected = { tab ->
                                        currentTab = tab
                                        currentScreen = when (tab) {
                                            BottomTab.HOME -> Screen.Home
                                            BottomTab.ORDERS -> Screen.Orders
                                            BottomTab.CART -> Screen.Cart
                                            BottomTab.PROFILE -> Screen.Profile
                                        }
                                    },
                                    cartItemCount = cartItems.size,
                                    currentLanguage = currentLanguage,
                                    onProfileLongClick = {
                                        // Long-press opens Account Switcher Sheet Modal!
                                        showAccountSheet = true
                                    },
                                    onProfileDoubleClick = {
                                        // Double-tap switches directly to next account!
                                        val switchedName = repository.switchToNextAccount()
                                        if (switchedName.isNotEmpty()) {
                                            coroutineScope.launch {
                                                snackbarHostState.showSnackbar("Beralih ke akun: $switchedName ✨")
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    ) { padding ->
                        Box(modifier = Modifier.fillMaxSize()) {
                            when (val screen = currentScreen) {
                                is Screen.Home -> {
                                    HomeScreen(
                                        repository = repository,
                                        currentLanguage = currentLanguage,
                                        onNavigateToCatalogDetail = { id -> currentScreen = Screen.CatalogDetail(id) },
                                        onNavigateToVisualSearch = { currentScreen = Screen.VisualSearch },
                                        onNavigateToSearch = { currentScreen = Screen.Search },
                                        onNavigateToChat = { currentScreen = Screen.Chat },
                                        onNavigateToNotifications = { currentScreen = Screen.Notifications },
                                        onNavigateToOrders = {
                                            currentTab = BottomTab.ORDERS
                                            currentScreen = Screen.Orders
                                        }
                                    )
                                }
                                is Screen.Orders -> {
                                    OrdersListScreen(
                                        repository = repository,
                                        currentLanguage = currentLanguage,
                                        onNavigateToDetail = { id -> currentScreen = Screen.OrderDetail(id) },
                                        onNavigateToPayment = { id -> currentScreen = Screen.PaymentInstruction(id) }
                                    )
                                }
                                is Screen.Cart -> {
                                    CartScreen(
                                        repository = repository,
                                        currentLanguage = currentLanguage,
                                        onNavigateToCheckout = { currentScreen = Screen.Checkout },
                                        onNavigateToHome = {
                                            currentTab = BottomTab.HOME
                                            currentScreen = Screen.Home
                                        }
                                    )
                                }
                                is Screen.Profile -> {
                                    ProfileScreen(
                                        repository = repository,
                                        currentLanguage = currentLanguage,
                                        onNavigateToKyc = { currentScreen = Screen.KycScanner },
                                        onNavigateToSettings = { currentScreen = Screen.Appearance },
                                        onNavigateToLanguage = { currentScreen = Screen.Language },
                                        onNavigateToAppearance = { currentScreen = Screen.Appearance },
                                        onOpenAccountSheet = { showAccountSheet = true },
                                        onLockApp = { repository.lockApp() },
                                        onLogout = {
                                            repository.logout()
                                            showAuthSheet = true
                                        }
                                    )
                                }
                                is Screen.CatalogDetail -> {
                                    CatalogDetailScreen(
                                        catalogId = screen.catalogId,
                                        repository = repository,
                                        currentLanguage = currentLanguage,
                                        onBack = { currentScreen = Screen.Home },
                                        onNavigateToCheckout = { currentScreen = Screen.Checkout },
                                        onNavigateToChat = { currentScreen = Screen.Chat }
                                    )
                                }
                                is Screen.VisualSearch -> {
                                    VisualSearchCbirScreen(
                                        repository = repository,
                                        currentLanguage = currentLanguage,
                                        onBack = { currentScreen = Screen.Home },
                                        onNavigateToCatalogDetail = { id -> currentScreen = Screen.CatalogDetail(id) }
                                    )
                                }
                                is Screen.Search -> {
                                    SearchScreen(
                                        repository = repository,
                                        currentLanguage = currentLanguage,
                                        onBack = { currentScreen = Screen.Home },
                                        onNavigateToCatalogDetail = { id -> currentScreen = Screen.CatalogDetail(id) },
                                        onNavigateToVisualSearch = { currentScreen = Screen.VisualSearch }
                                    )
                                }
                                is Screen.Checkout -> {
                                    CheckoutScreen(
                                        repository = repository,
                                        currentLanguage = currentLanguage,
                                        onBack = { currentScreen = Screen.Cart },
                                        onOrderPlaced = { id -> currentScreen = Screen.PaymentInstruction(id) }
                                    )
                                }
                                is Screen.PaymentInstruction -> {
                                    PaymentInstructionScreen(
                                        orderId = screen.orderId,
                                        repository = repository,
                                        currentLanguage = currentLanguage,
                                        onBack = {
                                            currentTab = BottomTab.ORDERS
                                            currentScreen = Screen.Orders
                                        },
                                        onPaymentCompleted = {
                                            currentTab = BottomTab.ORDERS
                                            currentScreen = Screen.Orders
                                        }
                                    )
                                }
                                is Screen.OrderDetail -> {
                                    OrderDetailScreen(
                                        orderId = screen.orderId,
                                        repository = repository,
                                        currentLanguage = currentLanguage,
                                        onBack = { currentScreen = Screen.Orders },
                                        onNavigateToChat = { currentScreen = Screen.Chat }
                                    )
                                }
                                is Screen.KycScanner -> {
                                    KycScannerScreen(
                                        repository = repository,
                                        currentLanguage = currentLanguage,
                                        onBack = { currentScreen = Screen.Profile }
                                    )
                                }
                                is Screen.Appearance -> {
                                    AppearanceScreen(
                                        repository = repository,
                                        currentLanguage = currentLanguage,
                                        onBack = { currentScreen = Screen.Profile }
                                    )
                                }
                                is Screen.Language -> {
                                    LanguageScreen(
                                        repository = repository,
                                        currentLanguage = currentLanguage,
                                        onBack = { currentScreen = Screen.Profile }
                                    )
                                }
                                is Screen.Chat -> {
                                    ChatScreen(
                                        repository = repository,
                                        currentLanguage = currentLanguage,
                                        onBack = { currentScreen = Screen.Home },
                                        onStartCall = { currentScreen = Screen.ConsultationCall }
                                    )
                                }
                                is Screen.ConsultationCall -> {
                                    ConsultationCallScreen(
                                        currentLanguage = currentLanguage,
                                        onEndCall = { currentScreen = Screen.Chat }
                                    )
                                }
                                is Screen.Notifications -> {
                                    NotificationsScreen(
                                        repository = repository,
                                        currentLanguage = currentLanguage,
                                        onBack = { currentScreen = Screen.Home },
                                        onNavigateToOrders = {
                                            currentTab = BottomTab.ORDERS
                                            currentScreen = Screen.Orders
                                        },
                                        onNavigateToChat = { currentScreen = Screen.Chat }
                                    )
                                }
                            }

                            // Snackbar host overlay
                            SnackbarHost(
                                hostState = snackbarHostState,
                                modifier = Modifier
                                    .align(androidx.compose.ui.Alignment.BottomCenter)
                                    .padding(bottom = if (isRootTab) 88.dp else 16.dp)
                            )
                        }
                    }

                    // Account Switcher Bottom Sheet
                    if (showAccountSheet) {
                        AccountSwitchModalSheet(
                            repository = repository,
                            currentLanguage = currentLanguage,
                            onDismiss = { showAccountSheet = false },
                            onAccountSwitched = { name ->
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Aktif: $name")
                                }
                                currentTab = BottomTab.HOME
                                currentScreen = Screen.Home
                            },
                            onAddNewAccount = {
                                showAuthSheet = true
                            }
                        )
                    }

                    // Auth Sheet Modal (Sign In / Sign Up)
                    if (showAuthSheet) {
                        AuthSheetHost(
                            repository = repository,
                            currentLanguage = currentLanguage,
                            onDismiss = { showAuthSheet = false },
                            onAuthSuccess = { user ->
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Selamat datang, ${user.fullName}!")
                                }
                                currentTab = BottomTab.HOME
                                currentScreen = Screen.Home
                            }
                        )
                    }
                }
            }
        }
    }
}
