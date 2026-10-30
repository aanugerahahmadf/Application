package com.wedding.fleurdecor.data.model

enum class ThemeMode(val labelId: String, val labelEn: String) {
    Light("Terang", "Light"),
    Dark("Gelap", "Dark"),
    System("Ikuti Sistem", "System")
}

enum class AppLanguage(val code: String, val displayName: String, val nativeName: String) {
    ID("id", "Indonesian", "Bahasa Indonesia"),
    EN("en", "English", "English")
}

enum class KycStatus(val labelId: String, val labelEn: String) {
    NotStarted("Belum Dimulai", "Not Started"),
    Uploading("Mengunggah", "Uploading"),
    Processing("Sedang Diproses", "Processing"),
    Pending("Menunggu Verifikasi", "Pending Review"),
    Approved("Terverifikasi", "Approved"),
    Rejected("Ditolak", "Rejected"),
    NeedsRetake("Perlu Foto Ulang", "Needs Retake")
}

enum class DocumentType(val title: String, val minDigits: Int, val maxDigits: Int) {
    KTP("KTP (Kartu Tanda Penduduk)", 16, 16),
    SIM("SIM (Surat Izin Mengemudi)", 6, 14),
    NPWP("NPWP (Nomor Pokok Wajib Pajak)", 15, 16),
    Passport("Paspor", 6, 9)
}

enum class OrderStatus(val labelId: String, val labelEn: String) {
    Pending("Menunggu Konfirmasi", "Pending"),
    Confirmed("Terkonfirmasi", "Confirmed"),
    Processing("Dalam Persiapan", "In Preparation"),
    Completed("Selesai", "Completed"),
    Cancelled("Dibatalkan", "Cancelled")
}

enum class PaymentStatus(val labelId: String, val labelEn: String) {
    Pending("Menunggu Pembayaran", "Awaiting Payment"),
    Paid("Lunas", "Paid"),
    Expired("Kedaluwarsa", "Expired"),
    Failed("Gagal", "Failed")
}

enum class PaymentMethodType {
    VirtualAccount,
    Qris,
    ManualProof
}
