package com.wedding.fleurdecor.ui.screens.orders

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Download
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wedding.fleurdecor.data.model.*
import com.wedding.fleurdecor.data.repository.AppRepository
import com.wedding.fleurdecor.ui.components.*
import com.wedding.fleurdecor.ui.theme.*

@Composable
fun OrderDetailScreen(
    orderId: Long,
    repository: AppRepository,
    currentLanguage: AppLanguage,
    onBack: () -> Unit,
    onNavigateToChat: () -> Unit,
    modifier: Modifier = Modifier
) {
    val order = repository.orders.value.find { it.id == orderId } ?: repository.orders.value.first()

    val timelineSteps = listOf(
        Pair("Pesanan Dibuat", true),
        Pair("Pembayaran Dikonfirmasi", order.paymentStatus == PaymentStatus.Paid),
        Pair("Persiapan Bunga & Moodboard", order.status == OrderStatus.Processing || order.status == OrderStatus.Completed),
        Pair("Pemasangan di Venue Gedung", order.status == OrderStatus.Completed),
        Pair("Acara Selesai", order.status == OrderStatus.Completed)
    )

    GlassScaffold(
        modifier = modifier.fillMaxSize().testTag("order_detail_screen"),
        topBar = {
            GlassTopBar(
                title = "Detail ${order.orderNumber}",
                onBack = onBack,
                testTag = "top_bar_order_detail"
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 10.dp, bottom = 40.dp)
        ) {
            // Status Summary
            item {
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
                                text = "Status Pesanan",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            StatusChip(
                                label = if (order.paymentStatus == PaymentStatus.Paid) "Lunas" else "Menunggu Bayar",
                                color = if (order.paymentStatus == PaymentStatus.Paid) EmeraldSuccess else AmberWarning
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(text = "Nomor Order: ${order.orderNumber}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                        Text(text = "Waktu Booking: ${order.createdAt}", style = MaterialTheme.typography.bodySmall, color = WarmGray)
                        Text(text = "Tanggal Acara: ${order.eventDate}", style = MaterialTheme.typography.bodySmall, color = WarmGray)
                        Text(text = "Lokasi: ${order.venueAddress}", style = MaterialTheme.typography.bodySmall, color = WarmGray)
                    }
                }
            }

            // Timeline
            item {
                GlassSurface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(22.dp)),
                    shape = RoundedCornerShape(22.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Tracking Timeline",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        timelineSteps.forEachIndexed { index, step ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(if (step.second) EmeraldSuccess else Color.LightGray.copy(alpha = 0.5f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (step.second) {
                                        Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = step.first,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = if (step.second) FontWeight.SemiBold else FontWeight.Normal,
                                    color = if (step.second) MaterialTheme.colorScheme.onSurface else WarmGray
                                )
                            }
                            if (index < timelineSteps.size - 1) {
                                Box(
                                    modifier = Modifier
                                        .padding(start = 11.dp)
                                        .width(2.dp)
                                        .height(16.dp)
                                        .background(if (step.second) EmeraldSuccess else Color.LightGray.copy(alpha = 0.5f))
                                )
                            }
                        }
                    }
                }
            }

            // Items Ordered
            item {
                GlassSurface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(22.dp)),
                    shape = RoundedCornerShape(22.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Rincian Paket Dekorasi",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        order.items.forEach { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Image(
                                    painter = painterResource(id = item.imageRes),
                                    contentDescription = item.catalogTitle,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.catalogTitle,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${item.tierName} (${item.quantity}x)",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = RoseGold
                                    )
                                }
                                Text(
                                    text = Localization.formatCurrency(item.price * item.quantity, currentLanguage),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = GlassBorderWhite)
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "Subtotal", style = MaterialTheme.typography.bodySmall, color = WarmGray)
                            Text(text = Localization.formatCurrency(order.subtotal, currentLanguage), style = MaterialTheme.typography.bodySmall)
                        }
                        if (order.discount > 0) {
                            Row(modifier = Modifier.fillMaxWidth().padding(top = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = "Diskon Voucher", style = MaterialTheme.typography.bodySmall, color = CrimsonError)
                                Text(text = "- ${Localization.formatCurrency(order.discount, currentLanguage)}", style = MaterialTheme.typography.bodySmall, color = CrimsonError)
                            }
                        }
                        Row(modifier = Modifier.fillMaxWidth().padding(top = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "Biaya Layanan", style = MaterialTheme.typography.bodySmall, color = WarmGray)
                            Text(text = Localization.formatCurrency(order.serviceFee, currentLanguage), style = MaterialTheme.typography.bodySmall)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "Total", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(text = Localization.formatCurrency(order.grandTotal, currentLanguage), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = CharcoalDark)
                        }
                    }
                }
            }

            // Contact Florist CTA
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onNavigateToChat,
                        shape = RoundedCornerShape(999.dp),
                        modifier = Modifier.weight(1f).height(50.dp)
                    ) {
                        Icon(imageVector = Icons.Default.ChatBubbleOutline, contentDescription = null, tint = RoseGold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Chat Florist", fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = { /* simulated invoice download */ },
                        shape = RoundedCornerShape(999.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.weight(1f).height(50.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Download, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Unduh Invoice", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}
