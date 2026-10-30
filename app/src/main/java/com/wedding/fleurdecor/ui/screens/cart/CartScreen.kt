package com.wedding.fleurdecor.ui.screens.cart

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wedding.fleurdecor.data.model.*
import com.wedding.fleurdecor.data.repository.AppRepository
import com.wedding.fleurdecor.ui.components.*
import com.wedding.fleurdecor.ui.theme.*

@Composable
fun CartScreen(
    repository: AppRepository,
    currentLanguage: AppLanguage,
    onNavigateToCheckout: () -> Unit,
    onNavigateToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cartItems = repository.cartItems.value
    val appliedVoucher = repository.appliedVoucher.value

    val subtotal = cartItems.sumOf {
        (it.selectedTier.salePrice ?: it.selectedTier.price) * it.quantity
    }
    val discount = appliedVoucher?.discountAmount ?: 0L
    val serviceFee = if (cartItems.isNotEmpty()) 500000L else 0L
    val grandTotal = (subtotal - discount + serviceFee).coerceAtLeast(0L)

    GlassScaffold(
        modifier = modifier.fillMaxSize().testTag("cart_screen"),
        topBar = {
            GlassTopBar(
                title = Localization.get("cart", currentLanguage),
                testTag = "top_bar_cart"
            )
        }
    ) { padding ->
        if (cartItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(RoseGold.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingBag,
                            contentDescription = null,
                            tint = RoseGold,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Keranjang Anda Masih Kosong",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Jelajahi koleksi dekorasi bunga pernikahan elegan kami dan pilih paket yang sesuai tema hari bahagia Anda.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = WarmGray,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    PrimaryCapsuleButton(
                        text = "Mulai Jelajah Dekorasi",
                        onClick = onNavigateToHome,
                        testTag = "btn_browse_catalog"
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(top = 10.dp, bottom = 20.dp)
                ) {
                    items(cartItems) { item ->
                        val itemPrice = item.selectedTier.salePrice ?: item.selectedTier.price

                        GlassSurface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(22.dp))
                                .testTag("cart_item_${item.id}"),
                            shape = RoundedCornerShape(22.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Image(
                                        painter = painterResource(id = item.catalog.imageRes),
                                        contentDescription = item.catalog.title,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(76.dp)
                                            .clip(RoundedCornerShape(16.dp))
                                    )
                                    Spacer(modifier = Modifier.width(14.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = item.catalog.title,
                                            style = MaterialTheme.typography.titleMedium.copy(fontSize = 15.sp),
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1
                                        )
                                        Text(
                                            text = item.selectedTier.name,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = RoseGold,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = Localization.formatCurrency(itemPrice, currentLanguage),
                                            style = MaterialTheme.typography.labelLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = CharcoalDark
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                                HorizontalDivider(color = GlassBorderWhite)
                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    IconButton(
                                        onClick = { repository.removeFromCart(item.id) },
                                        modifier = Modifier.testTag("btn_remove_cart_${item.id}")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.DeleteOutline,
                                            contentDescription = "Remove",
                                            tint = CrimsonError
                                        )
                                    }

                                    // Quantity Controls
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        IconButton(
                                            onClick = { repository.updateCartQuantity(item.id, item.quantity - 1) },
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clip(CircleShape)
                                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                        ) {
                                            Icon(imageVector = Icons.Default.Remove, contentDescription = "Decrease", modifier = Modifier.size(16.dp))
                                        }

                                        Text(
                                            text = "${item.quantity}",
                                            style = MaterialTheme.typography.labelLarge,
                                            fontWeight = FontWeight.Bold
                                        )

                                        IconButton(
                                            onClick = { repository.updateCartQuantity(item.id, item.quantity + 1) },
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clip(CircleShape)
                                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                        ) {
                                            Icon(imageVector = Icons.Default.Add, contentDescription = "Increase", modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Voucher Card
                    item {
                        GlassSurface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(20.dp))
                                .testTag("cart_voucher_section"),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ConfirmationNumber,
                                    contentDescription = null,
                                    tint = RoseGold
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = appliedVoucher?.code ?: "Punya Kode Promo?",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = appliedVoucher?.title ?: "Gunakan voucher untuk hemat jutaan rupiah",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = WarmGray
                                    )
                                }
                                TextButton(
                                    onClick = {
                                        if (appliedVoucher == null) {
                                            repository.applyVoucher(repository.vouchers.first())
                                        } else {
                                            repository.applyVoucher(null)
                                        }
                                    }
                                ) {
                                    Text(
                                        text = if (appliedVoucher == null) "Pakai" else "Hapus",
                                        color = RoseGold,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                // Summary and Checkout Sheet Bottom
                Surface(
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                    shadowElevation = 16.dp,
                    border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorderWhite),
                    modifier = Modifier.fillMaxWidth().navigationBarsPadding()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = Localization.get("subtotal", currentLanguage), style = MaterialTheme.typography.bodyMedium, color = WarmGray)
                            Text(text = Localization.formatCurrency(subtotal, currentLanguage), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                        }
                        if (discount > 0) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = Localization.get("discount", currentLanguage), style = MaterialTheme.typography.bodyMedium, color = CrimsonError)
                                Text(text = "- ${Localization.formatCurrency(discount, currentLanguage)}", style = MaterialTheme.typography.bodyMedium, color = CrimsonError, fontWeight = FontWeight.SemiBold)
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = Localization.get("service_fee", currentLanguage), style = MaterialTheme.typography.bodyMedium, color = WarmGray)
                            Text(text = Localization.formatCurrency(serviceFee, currentLanguage), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(color = GlassBorderWhite)
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = Localization.get("total", currentLanguage), style = MaterialTheme.typography.bodySmall, color = WarmGray)
                                Text(
                                    text = Localization.formatCurrency(grandTotal, currentLanguage),
                                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp),
                                    fontWeight = FontWeight.Bold,
                                    color = CharcoalDark
                                )
                            }

                            PrimaryCapsuleButton(
                                text = Localization.get("continue_payment", currentLanguage),
                                onClick = onNavigateToCheckout,
                                modifier = Modifier.width(190.dp),
                                testTag = "btn_cart_checkout"
                            )
                        }
                    }
                }
            }
        }
    }
}
