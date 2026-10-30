package com.wedding.fleurdecor.ui.screens.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wedding.fleurdecor.data.model.*
import com.wedding.fleurdecor.data.repository.AppRepository
import com.wedding.fleurdecor.ui.components.*
import com.wedding.fleurdecor.ui.theme.*

enum class AuthMode {
    SIGN_IN,
    SIGN_UP,
    FORGOT_PASSWORD,
    OTP_VERIFY,
    TWO_FACTOR
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthSheetHost(
    repository: AppRepository,
    currentLanguage: AppLanguage,
    onDismiss: () -> Unit,
    onAuthSuccess: (User) -> Unit,
    modifier: Modifier = Modifier
) {
    var mode by remember { mutableStateOf(AuthMode.SIGN_IN) }

    // Form States
    var emailOrUsername by remember { mutableStateOf("amanda.kartika@example.com") }
    var password by remember { mutableStateOf("password123") }
    var passwordVisible by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(true) }
    var termsAccepted by remember { mutableStateOf(true) }

    // Sign Up States
    var fullName by remember { mutableStateOf("") }
    var whatsapp by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // OTP / 2FA States
    var otpCode by remember { mutableStateOf("881290") }
    var twoFactorCode by remember { mutableStateOf("123456") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.98f),
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        dragHandle = { BottomSheetDefaults.DragHandle(color = WarmGray.copy(alpha = 0.5f)) },
        modifier = modifier.testTag("auth_sheet_host")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 36.dp)
                .verticalScroll(rememberScrollState())
        ) {
            when (mode) {
                // SIGN IN
                AuthMode.SIGN_IN -> {
                    Text(
                        text = "Selamat Datang Kembali",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Masuk ke akun Anda untuk melanjutkan pesanan dekorasi pernikahan",
                        style = MaterialTheme.typography.bodySmall,
                        color = WarmGray
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    OutlinedTextField(
                        value = emailOrUsername,
                        onValueChange = { emailOrUsername = it },
                        label = { Text("Email / Username") },
                        modifier = Modifier.fillMaxWidth().testTag("input_signin_email"),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Kata Sandi") },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth().testTag("input_signin_password"),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = rememberMe,
                                onCheckedChange = { rememberMe = it },
                                colors = CheckboxDefaults.colors(checkedColor = RoseGold)
                            )
                            Text(text = "Ingat Saya", style = MaterialTheme.typography.bodySmall)
                        }

                        Text(
                            text = "Lupa Kata Sandi?",
                            style = MaterialTheme.typography.bodySmall,
                            color = RoseGold,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.clickable { mode = AuthMode.FORGOT_PASSWORD }
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Checkbox(
                            checked = termsAccepted,
                            onCheckedChange = { termsAccepted = it },
                            colors = CheckboxDefaults.colors(checkedColor = RoseGold)
                        )
                        Text(
                            text = "Saya menyetujui Syarat & Kebijakan Dekorasi Pernikahan",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = WarmGray
                        )
                    }

                    if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = errorMessage ?: "", color = CrimsonError, style = MaterialTheme.typography.bodySmall)
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    PrimaryCapsuleButton(
                        text = "Masuk ke Akun",
                        onClick = {
                            if (!termsAccepted) {
                                errorMessage = "Harap setujui syarat & kebijakan terlebih dahulu"
                            } else {
                                val user = User(
                                    id = System.currentTimeMillis(),
                                    fullName = "Amanda Kartika",
                                    firstName = "Amanda",
                                    lastName = "Kartika",
                                    username = emailOrUsername.substringBefore("@"),
                                    email = emailOrUsername,
                                    phone = "+62 812 3456 7890",
                                    kycStatus = KycStatus.Approved
                                )
                                repository.login(user)
                                onAuthSuccess(user)
                                onDismiss()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "btn_submit_signin"
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(text = "Belum memiliki akun? ", style = MaterialTheme.typography.bodySmall, color = WarmGray)
                        Text(
                            text = "Daftar Akun Baru",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = RoseGold,
                            modifier = Modifier.clickable { mode = AuthMode.SIGN_UP }
                        )
                    }
                }

                // SIGN UP
                AuthMode.SIGN_UP -> {
                    Text(
                        text = "Daftar Akun Baru",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Mulai rencanakan pernikahan impian dengan desainer bunga profesional",
                        style = MaterialTheme.typography.bodySmall,
                        color = WarmGray
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = { Text("Nama Lengkap Pengantin") },
                        modifier = Modifier.fillMaxWidth().testTag("input_signup_name"),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = emailOrUsername,
                        onValueChange = { emailOrUsername = it },
                        label = { Text("Email Aktif") },
                        modifier = Modifier.fillMaxWidth().testTag("input_signup_email"),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = whatsapp,
                        onValueChange = { whatsapp = it },
                        label = { Text("Nomor WhatsApp (+62)") },
                        modifier = Modifier.fillMaxWidth().testTag("input_signup_wa"),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Kata Sandi") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth().testTag("input_signup_password"),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    PrimaryCapsuleButton(
                        text = "Lanjut Verifikasi OTP",
                        onClick = { mode = AuthMode.OTP_VERIFY },
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "btn_submit_signup"
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(text = "Sudah memiliki akun? ", style = MaterialTheme.typography.bodySmall, color = WarmGray)
                        Text(
                            text = "Masuk di Sini",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = RoseGold,
                            modifier = Modifier.clickable { mode = AuthMode.SIGN_IN }
                        )
                    }
                }

                // OTP VERIFY
                AuthMode.OTP_VERIFY -> {
                    Text(
                        text = "Verifikasi Kode OTP",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Masukkan 6 digit kode yang telah kami kirimkan ke email / WhatsApp Anda",
                        style = MaterialTheme.typography.bodySmall,
                        color = WarmGray
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    OutlinedTextField(
                        value = otpCode,
                        onValueChange = { if (it.length <= 6) otpCode = it },
                        label = { Text("Kode OTP 6 Digit") },
                        modifier = Modifier.fillMaxWidth().testTag("input_otp_code"),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    PrimaryCapsuleButton(
                        text = "Verifikasi & Aktifkan",
                        onClick = {
                            val user = User(
                                id = System.currentTimeMillis(),
                                fullName = fullName.ifBlank { "Amanda Kartika" },
                                firstName = fullName.split(" ").firstOrNull() ?: "Amanda",
                                lastName = fullName.split(" ").getOrNull(1) ?: "Kartika",
                                username = emailOrUsername.substringBefore("@"),
                                email = emailOrUsername,
                                phone = whatsapp.ifBlank { "+62 812 3456 7890" },
                                kycStatus = KycStatus.Approved
                            )
                            repository.login(user)
                            onAuthSuccess(user)
                            onDismiss()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "btn_submit_otp"
                    )
                }

                // FORGOT PASSWORD
                AuthMode.FORGOT_PASSWORD -> {
                    Text(
                        text = "Atur Ulang Kata Sandi",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Masukkan email akun Anda untuk menerima tautan dan kode reset",
                        style = MaterialTheme.typography.bodySmall,
                        color = WarmGray
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    OutlinedTextField(
                        value = emailOrUsername,
                        onValueChange = { emailOrUsername = it },
                        label = { Text("Email Akun") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    PrimaryCapsuleButton(
                        text = "Kirim Kode Reset",
                        onClick = { mode = AuthMode.OTP_VERIFY },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Kembali ke Masuk",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = RoseGold,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .clickable { mode = AuthMode.SIGN_IN }
                    )
                }

                else -> {}
            }
        }
    }
}
