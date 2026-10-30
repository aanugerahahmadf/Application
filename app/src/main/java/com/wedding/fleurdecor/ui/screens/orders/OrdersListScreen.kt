package com.wedding.fleurdecor.ui.screens.orders

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
fun OrdersListScreen(
    repository: AppRepository,
    currentLanguage: AppLanguage,
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToPayment: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val orders = repository.orders.value
    var selectedFilter by remember { mutableStateOf("Semua") }

    val filterOptions = listOf("Semua", "Menunggu Bayar", "Diproses", "Selesai")

    val filteredOrders = remember(orders, selectedFilter) {
        when (selectedFilter) {
            "Menunggu Bayar" -> orders.filter { it.paymentStatus == PaymentStatus.Pending }
            "Diproses" -> orders.filter { it.status == OrderStatus.Processing || it.status == OrderStatus.Confirmed }
            "Selesai" -> orders.filter { it.status == OrderStatus.Completed }
            else -> orders
        }
    }

    GlassScaffold(
        modifier = modifier.fillMaxSize().testTag("orders_list_screen"),
        topBar = {
            GlassTopBar(
                title = Localization.get("orders", currentLanguage),
                testTag = "top_bar_orders"
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Filter Pills
            LazyRow(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filterOptions) { filter ->
                    FilterCapsule(
                        label = filter,
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter }
                    )
                }
            }

            if (filteredOrders.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Belum ada pesanan pada kategori ini.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = WarmGray
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(top = 8.dp, bottom = 90.dp)
                ) {
                    items(filteredOrders) { order ->
                        GlassSurface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(22.dp))
                                .clickable { onNavigateToDetail(order.id) }
                                .testTag("order_item_${order.id}"),
                            shape = RoundedCornerShape(22.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = order.orderNumber,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )

                                    val statusColor = when (order.paymentStatus) {
                                        PaymentStatus.Paid -> EmeraldSuccess
                                        PaymentStatus.Pending -> AmberWarning
                                        else -> CrimsonError
                                    }

                                    StatusChip(
                                        label = if (order.paymentStatus == PaymentStatus.Paid) "Lunas" else "Menunggu Bayar",
                                        color = statusColor
                                    )
                                }

                                Text(
                                    text = "Dipesan: ${order.createdAt}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = WarmGray
                                )

                                Spacer(modifier = Modifier.height(10.dp))
                                HorizontalDivider(color = GlassBorderWhite)
                                Spacer(modifier = Modifier.height(10.dp))

                                // Item Preview
                                val firstItem = order.items.firstOrNull()
                                if (firstItem != null) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Image(
                                            painter = painterResource(id = firstItem.imageRes),
                                            contentDescription = firstItem.catalogTitle,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .size(60.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = firstItem.catalogTitle,
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.SemiBold,
                                                maxLines = 1
                                            )
                                            Text(
                                                text = firstItem.tierName,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = RoseGold
                                            )
                                            if (order.items.size > 1) {
                                                Text(
                                                    text = "+ ${order.items.size - 1} item dekorasi lainnya",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = WarmGray
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(text = "Total Transaksi", style = MaterialTheme.typography.bodySmall, color = WarmGray)
                                        Text(
                                            text = Localization.formatCurrency(order.grandTotal, currentLanguage),
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = CharcoalDark
                                        )
                                    }

                                    if (order.paymentStatus == PaymentStatus.Pending) {
                                        Button(
                                            onClick = { onNavigateToPayment(order.id) },
                                            colors = ButtonDefaults.buttonColors(containerColor = AmberWarning),
                                            shape = RoundedCornerShape(999.dp),
                                            modifier = Modifier.testTag("btn_pay_order_${order.id}")
                                        ) {
                                            Text(text = "Bayar Sekarang", fontWeight = FontWeight.SemiBold, color = Color.White)
                                        }
                                    } else {
                                        OutlinedButton(
                                            onClick = { onNavigateToDetail(order.id) },
                                            shape = RoundedCornerShape(999.dp)
                                        ) {
                                            Text(text = "Lihat Detail", fontWeight = FontWeight.SemiBold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
