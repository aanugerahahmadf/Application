package com.wedding.fleurdecor.ui.screens.profile

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wedding.fleurdecor.data.model.*
import com.wedding.fleurdecor.data.repository.AppRepository
import com.wedding.fleurdecor.ui.components.*
import com.wedding.fleurdecor.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun KycScannerScreen(
    repository: AppRepository,
    currentLanguage: AppLanguage,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var kycStep by remember { mutableStateOf(0) } // 0: Select Document, 1: Scan Document Card, 2: Selfie Liveness, 3: Success
    var selectedDocType by remember { mutableStateOf(DocumentType.KTP) }
    var docNumber by remember { mutableStateOf("3171025508970003") }
    var livenessScore by remember { mutableStateOf(98) }

    // Pulse animation for scanner
    val infiniteTransition = rememberInfiniteTransition(label = "kyc_pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    GlassScaffold(
        modifier = modifier.fillMaxSize().testTag("kyc_scanner_screen"),
        topBar = {
            GlassTopBar(
                title = Localization.get("kyc_verification", currentLanguage),
                onBack = onBack,
                testTag = "top_bar_kyc"
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (kycStep) {
                // Step 0: Choose Document Type
                0 -> {
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = "Pilih Dokumen Identitas",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Pilih jenis kartu identitas resmi yang masih berlaku untuk verifikasi booking acara pernikahan",
                        style = MaterialTheme.typography.bodyMedium,
                        color = WarmGray,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    DocumentType.values().forEach { doc ->
                        val isSelected = selectedDocType == doc

                        GlassSurface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) RoseGold else GlassBorderWhite,
                                    shape = RoundedCornerShape(20.dp)
                                )
                                .clickable { selectedDocType = doc }
                                .testTag("doc_type_${doc.name}"),
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
                                    onClick = { selectedDocType = doc },
                                    colors = RadioButtonDefaults.colors(selectedColor = RoseGold)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = doc.title,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    PrimaryCapsuleButton(
                        text = "Lanjut Pindai Dokumen",
                        onClick = { kycStep = 1 },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp),
                        testTag = "btn_kyc_next_step1"
                    )
                }

                // Step 1: Scan Document Card Viewfinder
                1 -> {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Posisikan ${selectedDocType.name} pada Bingkai",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Pastikan seluruh sudut dokumen terlihat jelas, tidak silau, dan tulisan terbaca",
                        style = MaterialTheme.typography.bodySmall,
                        color = WarmGray,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Simulated Camera Document Frame
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(CharcoalDark)
                            .border(2.dp, RoseGold.copy(alpha = pulseAlpha), RoundedCornerShape(24.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.8f),
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Bingkai ${selectedDocType.name} Terdeteksi",
                                color = Color.White,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    OutlinedTextField(
                        value = docNumber,
                        onValueChange = { docNumber = it },
                        label = { Text("Nomor Identitas (${selectedDocType.name})") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_kyc_doc_number"),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    PrimaryCapsuleButton(
                        text = "Ambil Foto & Lanjut Selfie",
                        onClick = { kycStep = 2 },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp),
                        testTag = "btn_kyc_next_step2"
                    )
                }

                // Step 2: Selfie Liveness & Face Scan
                2 -> {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Foto Selfie & Verifikasi Wajah",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Posisikan wajah Anda tepat di dalam lingkaran untuk memverifikasi kecocokan dengan dokumen",
                        style = MaterialTheme.typography.bodySmall,
                        color = WarmGray,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    // Circular Face Guide
                    Box(
                        modifier = Modifier
                            .size(240.dp)
                            .clip(CircleShape)
                            .background(CharcoalDark)
                            .border(3.dp, EmeraldSuccess.copy(alpha = pulseAlpha), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Face,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.9f),
                                modifier = Modifier.size(72.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Liveness: Terdeteksi",
                                color = EmeraldSuccess,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Face Score Panel
                    GlassSurface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp)),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = Localization.get("face_match_score", currentLanguage),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Tingkat kesesuaian biometrik sangat tinggi",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = WarmGray
                                )
                            }
                            Text(
                                text = "$livenessScore%",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldSuccess
                            )
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    PrimaryCapsuleButton(
                        text = "Kirim Verifikasi",
                        onClick = {
                            repository.submitKyc(selectedDocType, docNumber)
                            kycStep = 3
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp),
                        testTag = "btn_kyc_submit"
                    )
                }

                // Step 3: Success Completed
                3 -> {
                    Spacer(modifier = Modifier.height(60.dp))
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape)
                            .background(EmeraldSuccess.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = EmeraldSuccess,
                            modifier = Modifier.size(54.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Verifikasi Identitas Selesai!",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldSuccess
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Data identitas Anda (${selectedDocType.name}: $docNumber) telah berhasil diverifikasi oleh sistem. Anda sekarang dapat melakukan transaksi pemesanan dekorasi pernikahan dengan aman.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = WarmGray,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    PrimaryCapsuleButton(
                        text = "Kembali ke Profil",
                        onClick = onBack,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp),
                        testTag = "btn_kyc_done"
                    )
                }
            }
        }
    }
}
