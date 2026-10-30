package com.wedding.fleurdecor.ui.screens.orders

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wedding.fleurdecor.data.model.*
import com.wedding.fleurdecor.data.repository.AppRepository
import com.wedding.fleurdecor.ui.components.*
import com.wedding.fleurdecor.ui.theme.*

@Composable
fun PaymentInstructionScreen(
    orderId: Long,
    repository: AppRepository,
    currentLanguage: AppLanguage,
    onBack: () -> Unit,
    onPaymentCompleted: () -> Unit,
    modifier: Modifier = Modifier
) {
    val clipboardManager = LocalClipboardManager.current
    val order = repository.orders.value.find { it.id == orderId } ?: repository.orders.value.first()

    var copiedTextFeedback by remember { mutableStateOf(false) }
    var proofUploaded by remember { mutableStateOf(order.paymentProofUploaded) }

    GlassScaffold(
        modifier = modifier.fillMaxSize().testTag("payment_instruction_screen"),
        topBar = {
            GlassTopBar(
                title = Localization.get("payment_instruction", currentLanguage),
                onBack = onBack,
                testTag = "top_bar_payment"
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
            // Status Header & Countdown
            item {
                GlassSurface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp)),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (proofUploaded || order.paymentStatus == PaymentStatus.Paid) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = EmeraldSuccess,
                                modifier = Modifier.size(56.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = Localization.get("payment_success", currentLanguage),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldSuccess
                            )
                            Text(
                                text = "Bukti transfer telah diverifikasi otomatis.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = WarmGray
                            )
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = null,
                                    tint = AmberWarning,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = Localization.get("deadline_hours", currentLanguage),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = AmberWarning,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = Localization.formatCurrency(order.grandTotal, currentLanguage),
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = CharcoalDark
                            )
                            Text(
                                text = "Nomor Pesanan: ${order.orderNumber}",
                                style = MaterialTheme.typography.bodySmall,
                                color = WarmGray
                            )
                        }
                    }
                }
            }

            // Virtual Account Details
            item {
                GlassSurface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(22.dp)),
                    shape = RoundedCornerShape(22.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = order.vaBank ?: "BCA Virtual Account",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = Localization.get("va_number", currentLanguage),
                            style = MaterialTheme.typography.bodySmall,
                            color = WarmGray
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = order.vaNumber ?: "8801 2345 6789 0012",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )

                            TextButton(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(order.vaNumber ?: "8801234567890012"))
                                    copiedTextFeedback = true
                                },
                                modifier = Modifier.testTag("btn_copy_va")
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp), tint = RoseGold)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (copiedTextFeedback) Localization.get("copied", currentLanguage) else Localization.get("copy", currentLanguage),
                                    color = RoseGold,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // QRIS Code Visual
            item {
                GlassSurface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(22.dp)),
                    shape = RoundedCornerShape(22.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = Localization.get("qris_scan", currentLanguage),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Pindai menggunakan GoPay, OVO, Dana, BCA Mobile, atau aplikasi bank lain",
                            style = MaterialTheme.typography.bodySmall,
                            color = WarmGray,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        // QRIS Pattern Container
                        Box(
                            modifier = Modifier
                                .size(180.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.White)
                                .border(2.dp, CharcoalDark, RoundedCornerShape(16.dp))
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "QRIS STANDAR INDONESIA",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Box(
                                    modifier = Modifier
                                        .size(110.dp)
                                        .background(CharcoalDark)
                                        .padding(4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .background(Color.White),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "FLEUR",
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                                            fontWeight = FontWeight.Bold,
                                            color = CharcoalDark
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Upload Proof or Done Button
            item {
                if (!proofUploaded && order.paymentStatus != PaymentStatus.Paid) {
                    PrimaryCapsuleButton(
                        text = Localization.get("upload_proof", currentLanguage),
                        onClick = {
                            repository.markOrderPaid(order.id)
                            proofUploaded = true
                        },
                        leadingIcon = {
                            Icon(Icons.Default.UploadFile, contentDescription = null, tint = Color.White)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "btn_upload_payment_proof"
                    )
                } else {
                    PrimaryCapsuleButton(
                        text = "Lihat Riwayat Pesanan",
                        onClick = onPaymentCompleted,
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "btn_view_order_history"
                    )
                }
            }
        }
    }
}
