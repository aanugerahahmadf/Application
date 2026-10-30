package com.wedding.fleurdecor.data.model

import java.text.NumberFormat
import java.util.Locale

object Localization {
    fun formatCurrency(amount: Long, locale: AppLanguage = AppLanguage.ID): String {
        val formatter = NumberFormat.getNumberInstance(Locale("id", "ID"))
        return "Rp ${formatter.format(amount)}"
    }

    // String dictionary for bilingual support (ID & EN)
    private val strings = mapOf(
        "app_title" to Pair("Fleur Décor", "Fleur Décor"),
        "app_tagline" to Pair("Dekorasi Pernikahan Eksklusif & Visual Search", "Exclusive Wedding Floral Decor & Visual Search"),
        "home" to Pair("Beranda", "Home"),
        "catalog" to Pair("Katalog", "Catalog"),
        "orders" to Pair("Pesanan", "Orders"),
        "cart" to Pair("Keranjang", "Cart"),
        "profile" to Pair("Profil", "Profile"),
        "visual_search" to Pair("Pencarian Visual", "Visual Search"),
        "search_hint" to Pair("Cari tema dekorasi, pelaminan, atau buket...", "Search decor themes, altar, or bouquets..."),
        "categories" to Pair("Kategori Dekorasi", "Decor Categories"),
        "featured_decor" to Pair("Koleksi Pilihan", "Featured Collections"),
        "on_sale" to Pair("Penawaran Spesial", "Special Offers"),
        "view_all" to Pair("Lihat Semua", "View All"),
        "select_package" to Pair("Pilih Paket / Tier", "Select Tier / Package"),
        "book_now" to Pair("Pesan Sekarang", "Book Now"),
        "add_to_cart" to Pair("Tambah ke Keranjang", "Add to Cart"),
        "wishlist" to Pair("Favorit", "Wishlist"),
        "consult_cs" to Pair("Konsultasi Florist", "Consult Florist"),
        "checkout" to Pair("Konfirmasi Pemesanan", "Checkout & Booking"),
        "event_date" to Pair("Tanggal Acara", "Event Date"),
        "venue_address" to Pair("Alamat Lokasi Gedung / Venue", "Venue Address"),
        "special_notes" to Pair("Catatan Khusus Pengantin", "Special Requests / Notes"),
        "vouchers" to Pair("Voucher & Promo", "Vouchers & Promos"),
        "payment_summary" to Pair("Rincian Pembayaran", "Payment Summary"),
        "subtotal" to Pair("Subtotal Paket", "Packages Subtotal"),
        "discount" to Pair("Potongan Diskon", "Discount"),
        "service_fee" to Pair("Biaya Layanan & Pemasangan", "Service & Setup Fee"),
        "total" to Pair("Total Pembayaran", "Total Amount"),
        "continue_payment" to Pair("Lanjut ke Pembayaran", "Proceed to Payment"),
        "pay_now" to Pair("Bayar Sekarang", "Pay Now"),
        "payment_instruction" to Pair("Instruksi Pembayaran", "Payment Instructions"),
        "va_number" to Pair("Nomor Virtual Account", "Virtual Account Number"),
        "copy" to Pair("Salin", "Copy"),
        "copied" to Pair("Berhasil disalin!", "Copied to clipboard!"),
        "qris_scan" to Pair("Pindai QRIS", "Scan QRIS"),
        "upload_proof" to Pair("Unggah Bukti Transfer", "Upload Transfer Proof"),
        "payment_pending" to Pair("Menunggu Pembayaran", "Awaiting Payment"),
        "payment_success" to Pair("Pembayaran Diterima!", "Payment Received!"),
        "deadline_hours" to Pair("Selesaikan dalam 23 jam 59 menit", "Complete within 23 hrs 59 mins"),
        "kyc_verification" to Pair("Verifikasi Identitas (KYC)", "Identity Verification (KYC)"),
        "kyc_desc" to Pair("Verifikasi KTP / Paspor dan foto selfie untuk keamanan transaksi booking dekorasi", "Verify ID document and selfie for booking transaction security"),
        "start_verification" to Pair("Mulai Verifikasi", "Start Verification"),
        "scan_document" to Pair("Pindai Dokumen", "Scan Document"),
        "take_selfie" to Pair("Foto Selfie & Liveness", "Take Selfie & Liveness"),
        "face_match_score" to Pair("Skor Kesesuaian Wajah", "Face Match Score"),
        "chat_support" to Pair("Konsultasi Wedding Florist", "Wedding Florist Consultation"),
        "live_call" to Pair("Panggilan Konsultasi", "Live Consultation Call"),
        "settings" to Pair("Pengaturan", "Settings"),
        "language" to Pair("Bahasa", "Language"),
        "appearance" to Pair("Tampilan & Tema", "Appearance & Theme"),
        "security" to Pair("Keamanan & 2FA", "Security & 2FA"),
        "app_lock" to Pair("Kunci Aplikasi (PIN)", "App Lock (PIN)"),
        "switch_account" to Pair("Ganti Akun", "Switch Account"),
        "add_account" to Pair("Tambah Akun Baru", "Add New Account"),
        "sign_in" to Pair("Masuk", "Sign In"),
        "sign_up" to Pair("Daftar Akun", "Sign Up"),
        "logout" to Pair("Keluar", "Sign Out"),
        "enter_pin" to Pair("Masukkan PIN Keamanan", "Enter Security PIN"),
        "cbir_title" to Pair("Visual Flower Search (CBIR)", "Visual Flower Search (CBIR)"),
        "cbir_subtitle" to Pair("Temukan dekorasi serupa menggunakan foto bunga impian Anda", "Find matching wedding decor using photos of your dream flowers"),
        "similarity" to Pair("Kemiripan", "Similarity"),
        "camera" to Pair("Kamera", "Camera"),
        "gallery" to Pair("Galeri", "Gallery")
    )

    fun get(key: String, lang: AppLanguage): String {
        val pair = strings[key] ?: return key
        return if (lang == AppLanguage.ID) pair.first else pair.second
    }
}
