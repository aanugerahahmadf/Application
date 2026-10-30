package com.wedding.fleurdecor.ui.screens.cart

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wedding.fleurdecor.data.model.*
import com.wedding.fleurdecor.data.repository.AppRepository
import com.wedding.fleurdecor.ui.components.*
import com.wedding.fleurdecor.ui.theme.*

@Composable
fun CheckoutScreen(
    repository: AppRepository,
    currentLanguage: AppLanguage,
    onBack: () -> Unit,
    onOrderPlaced: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val cartItems = repository.cartItems.value
    val appliedVoucher = repository.appliedVoucher.value

    var eventDate by remember { mutableStateOf("18 Desember 2026") }
    var venueAddress by remember { mutableStateOf("Grand Ballroom Hotel Mulia Senayan, Jakarta Pusat") }
    var specialNotes by remember { mutableStateOf("Nuansa mawar blush pastel dan karpet champagne bersih.") }
    var selectedPaymentMethod by remember { mutableStateOf("BCA Virtual Account") }

    val subtotal = cartItems.sumOf { (it.selectedTier.salePrice ?: it.selectedTier.price) * it.quantity }
    val discount = appliedVoucher?.discountAmount ?: 0L
    val fee = 500000L
    val grandTotal = (subtotal - discount + fee).coerceAtLeast(0L)

    GlassScaffold(
        modifier = modifier.fillMaxSize().testTag("checkout_screen"),
        topBar = {
            GlassTopBar(
                title = Localization.get("checkout", currentLanguage),
                onBack = onBack,
                testTag = "top_bar_checkout"
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(top = 10.dp, bottom = 20.dp)
            ) {
                // Event Date Section
                item {
                    GlassSurface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(22.dp)),
                        shape = RoundedCornerShape(22.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = null, tint = RoseGold)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = Localization.get("event_date", currentLanguage),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(
                                value = eventDate,
                                onValueChange = { eventDate = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_event_date"),
                                shape = RoundedCornerShape(16.dp),
                                singleLine = true
                            )
                        }
                    }
                }

                // Venue Address Section
                item {
                    GlassSurface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(22.dp)),
                        shape = RoundedCornerShape(22.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = RoseGold)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = Localization.get("venue_address", currentLanguage),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(
                                value = venueAddress,
                                onValueChange = { venueAddress = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_venue_address"),
                                shape = RoundedCornerShape(16.dp),
                                minLines = 2
                            )
                        }
                    }
                }

                // Special Requests / Notes
                item {
                    GlassSurface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(22.dp)),
                        shape = RoundedCornerShape(22.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = Localization.get("special_notes", currentLanguage),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = specialNotes,
                                onValueChange = { specialNotes = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_special_notes"),
                                shape = RoundedCornerShape(16.dp),
                                minLines = 2
                            )
                        }
                    }
                }

                // Payment Method Selector
                item {
                    GlassSurface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(22.dp)),
                        shape = RoundedCornerShape(22.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Metode Pembayaran",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            listOf("BCA Virtual Account", "Mandiri Virtual Account", "QRIS Instant Scan").forEach { method ->
                                val isSelected = selectedPaymentMethod == method
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .clickable { selectedPaymentMethod = method }
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = { selectedPaymentMethod = method },
                                        colors = RadioButtonDefaults.colors(selectedColor = RoseGold)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(
                                        imageVector = if (method.contains("QRIS")) Icons.Default.QrCode else Icons.Default.AccountBalance,
                                        contentDescription = null,
                                        tint = RoseGold
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = method,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Bottom Confirm Button
            Surface(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                shadowElevation = 16.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorderWhite),
                modifier = Modifier.fillMaxWidth().navigationBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
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
                        text = Localization.get("pay_now", currentLanguage),
                        onClick = {
                            val newOrder = repository.createOrder(eventDate, venueAddress, specialNotes)
                            onOrderPlaced(newOrder.id)
                        },
                        modifier = Modifier.width(180.dp),
                        testTag = "btn_submit_order"
                    )
                }
            }
        }
    }
}
