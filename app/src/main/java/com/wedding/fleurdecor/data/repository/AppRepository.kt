package com.wedding.fleurdecor.data.repository

import com.wedding.fleurdecor.R
import com.wedding.fleurdecor.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AppRepository {

    // Language & Theme State
    private val _currentLanguage = MutableStateFlow(AppLanguage.ID)
    val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

    private val _themeMode = MutableStateFlow(ThemeMode.Light)
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    fun setLanguage(lang: AppLanguage) {
        _currentLanguage.value = lang
    }

    fun setThemeMode(mode: ThemeMode) {
        _themeMode.value = mode
    }

    // Auth & Accounts
    private val defaultUser1 = User(
        id = 1L,
        fullName = "Amanda Kartika",
        firstName = "Amanda",
        lastName = "Kartika",
        username = "amandakartika",
        email = "amanda.kartika@example.com",
        phone = "+62 812 3456 7890",
        kycStatus = KycStatus.Approved,
        completionPercent = 90,
        isTwoFactorEnabled = true,
        appLockPin = "1234"
    )

    private val defaultUser2 = User(
        id = 2L,
        fullName = "Rizky Pratama",
        firstName = "Rizky",
        lastName = "Pratama",
        username = "rizkypratama",
        email = "rizky.pratama@example.com",
        phone = "+62 813 9876 5432",
        kycStatus = KycStatus.Pending,
        completionPercent = 70,
        isTwoFactorEnabled = false,
        appLockPin = "4321"
    )

    private val _savedAccounts = MutableStateFlow(
        listOf(
            SavedAccount(1L, defaultUser1, "token_usr_1", isActive = true),
            SavedAccount(2L, defaultUser2, "token_usr_2", isActive = false)
        )
    )
    val savedAccounts: StateFlow<List<SavedAccount>> = _savedAccounts.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(defaultUser1)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _isAuthenticated = MutableStateFlow(true)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    fun switchToAccount(accountId: Long): String {
        val account = _savedAccounts.value.find { it.id == accountId } ?: return ""
        _savedAccounts.update { list ->
            list.map { it.copy(isActive = (it.id == accountId)) }
        }
        _currentUser.value = account.user
        _isAuthenticated.value = true
        return account.user.fullName
    }

    fun switchToNextAccount(): String {
        val list = _savedAccounts.value
        if (list.size <= 1) return ""
        val currentIndex = list.indexOfFirst { it.isActive }.takeIf { it >= 0 } ?: 0
        val nextIndex = (currentIndex + 1) % list.size
        val nextAccount = list[nextIndex]
        return switchToAccount(nextAccount.id)
    }

    fun logout() {
        _isAuthenticated.value = false
        _currentUser.value = null
        _savedAccounts.update { list -> list.map { it.copy(isActive = false) } }
    }

    fun login(user: User) {
        _currentUser.value = user
        _isAuthenticated.value = true
        _savedAccounts.update { list ->
            val existing = list.find { it.user.email == user.email }
            if (existing != null) {
                list.map { it.copy(isActive = (it.id == existing.id)) }
            } else {
                val newAccount = SavedAccount(
                    id = System.currentTimeMillis(),
                    user = user,
                    tokenRef = "token_${System.currentTimeMillis()}",
                    isActive = true
                )
                list.map { it.copy(isActive = false) } + newAccount
            }
        }
    }

    // App Lock State
    private val _isAppLocked = MutableStateFlow(false)
    val isAppLocked: StateFlow<Boolean> = _isAppLocked.asStateFlow()

    fun unlockApp(pin: String): Boolean {
        val user = _currentUser.value ?: defaultUser1
        return if (pin == user.appLockPin) {
            _isAppLocked.value = false
            true
        } else false
    }

    fun lockApp() {
        _isAppLocked.value = true
    }

    // Categories
    val categories = listOf(
        Category(1L, "Pelaminan & Altar", "altar", "Dekorasi panggung pelaminan mewah dan altar suci", R.drawable.wedding_hero_banner, 8),
        Category(2L, "Entrance & Gazebo", "arch", "Gerbang pergola mawar, lorong masuk kristal, dan gazebo", R.drawable.wedding_arch_editorial, 6),
        Category(3L, "Centerpiece & VIP", "centerpiece", "Rangkaian bunga meja jamuan ballroom elegan", R.drawable.wedding_table_centerpiece, 12),
        Category(4L, "Bridal Bouquet", "bouquet", "Buket tangan pengantin premium dan corsage", R.drawable.bridal_bouquet_cbir, 15)
    )

    // Tiers & Catalogs
    private val tierAltarSilver = CatalogTier(
        id = 101L, catalogId = 1L, name = "Silver Grace Package",
        description = "Dekorasi panggung pelaminan 6 meter, mawar impor pastel, hydrangeas, dan 4 pilar standing flowers kristal.",
        price = 18500000L, capacity = "Gedung / Aula Medium (200-400 tamu)", stock = 4,
        features = listOf("Pelaminan Bunga Segar 6m", "4 Standing Flower Kristal", "Karpet Mawar Melati", "Lighting Warm White LED", "Pemasangan & Pembongkaran Lengkap")
    )

    private val tierAltarGold = CatalogTier(
        id = 102L, catalogId = 1L, name = "Gold Opulence Package",
        description = "Dekorasi megah panggung 10 meter, cascading David Austin roses, chandelier kristal gantung, dan karpet velvet.",
        price = 28000000L, salePrice = 25500000L, capacity = "Ballroom Hotel Bintang 5 (500-1000 tamu)", stock = 3,
        features = listOf("Pelaminan Bunga Penuh 10m (Cascading)", "8 Standing Flowers & Chandelier Gantung", "Dekorasi Area Akad / Blessing Eksklusif", "Karpet Velvet Champagne 25m", "Lighting Ambient Show", "Gratis 2 Hand Bouquet")
    )

    private val tierAltarPlatinum = CatalogTier(
        id = 103L, catalogId = 1L, name = "Platinum Grandeur Suite",
        description = "Karya seni flora tanpa batas 16 meter, bunga impor Holland & Ecuador, pilar kaca cermin, dan visual mapping floral.",
        price = 42000000L, capacity = "Grand Ballroom / Convention Center (1000+ tamu)", stock = 2,
        features = listOf("Pelaminan Mega Flora 16m Custom Design", "Lorong Masuk Floral Tunnel 12m", "Konsultasi 3D & Sampel Bunga Langsung", "Tim Florist Dedicated On-Site 24 Jam", "All-Inclusive Sound & Lighting Integration")
    )

    private val tierArchIntimate = CatalogTier(
        id = 201L, catalogId = 2L, name = "Intimate Garden Pergola",
        description = "Lengkungan kayu alami dengan rangkaian bunga mawar putih, delphinium, eucalyptus segar, dan lilin lentera.",
        price = 12000000L, capacity = "Outdoor / Glasshouse Intimate (100-300 tamu)", stock = 5,
        features = listOf("Circular Altar Arch 3x3m", "Rangkaian Bunga Mawar & Peony", "12 Lentera Kaca dengan Lilin Aromaterapi", "Kursi Pengantin Kayu Klasik")
    )

    private val tierArchPavilion = CatalogTier(
        id = 202L, catalogId = 2L, name = "Grand Pavilion Tunnel",
        description = "Gazebo megah kubah bunga dengan kanopi wisteria gantung dan lorong masuk karpet mawar.",
        price = 19500000L, salePrice = 17500000L, capacity = "Grand Ballroom Entrance", stock = 3,
        features = listOf("Gazebo Kubah Kaca 4x4m", "Wisteria & Mawar Gantung Full Ceiling", "Welcome Sign Acrylic Berbunga", "Lighting Fairy Lights Dramatis")
    )

    private val tierCenterpieceStandard = CatalogTier(
        id = 301L, catalogId = 3L, name = "10 Meja VIP Ballroom",
        description = "Rangkaian bunga tinggi vas kristal untuk 10 meja jamuan utama keluarga dan VIP.",
        price = 8500000L, capacity = "10 Round Tables", stock = 6,
        features = listOf("10 Vas Kristal Tinggi 90cm", "Bunga Mawar, Hydrangea, & Anggrek Bulan", "Nomor Meja Eksklusif & Napkin Ring Floral", "Floating Candle Votives")
    )

    private val tierCenterpieceGrand = CatalogTier(
        id = 302L, catalogId = 3L, name = "20 Meja VIP & Royal Long Table",
        description = "Kombinasi 20 meja bundar dan 1 royal bridal long table sepanjang 8 meter bertabur bunga cascading.",
        price = 15000000L, capacity = "20 Tables + Long Bridal Table", stock = 4,
        features = listOf("20 Centerpiece Vas Kristal Megah", "Meja Panjang Pengantin 8m Full Floral Runner", "Kandelabra Lilin Emas", "Set Lengkap Menu Card & Custom Placecard")
    )

    private val tierBouquetClassic = CatalogTier(
        id = 401L, catalogId = 4L, name = "Classic Bridal & Groom Suite",
        description = "Buket mawar Quicksand dan ranunculus dengan pita sutra alami, plus 1 boutonniere pengantin pria.",
        price = 2800000L, capacity = "Pengantin Pria & Wanita", stock = 8,
        features = listOf("1 Hand Bouquet Pengantin Premium", "1 Boutonniere Jas Groom", "Preserved Flower Keepsake Box", "Pengantaran Khusus Kotak Dingin")
    )

    private val tierBouquetEntourage = CatalogTier(
        id = 402L, catalogId = 4L, name = "Full Bridal Entourage Suite",
        description = "Set lengkap buket pengantin, 4 buket bridesmaid, 6 corsage keluarga orang tua, dan 6 boutonniere groomsmen.",
        price = 5200000L, salePrice = 4600000L, capacity = "Pengantin & Keluarga Inti", stock = 5,
        features = listOf("1 Master Bridal Bouquet", "4 Bridesmaid Petite Bouquets", "6 Mother Corsages Anggrek", "7 Father & Groomsmen Boutonnieres", "Flower Girl Petal Basket")
    )

    val catalogs = listOf(
        Catalog(
            id = 1L,
            title = "The Royal Parisian Blossom Altar",
            description = "Inspirasi dekorasi altar pernikahan mewah bergaya Eropa klasik dengan perpaduan mawar ivory, blush peony, dan kristal gantung yang memesona.",
            categoryId = 1L,
            categoryName = "Pelaminan & Altar",
            theme = "Royal Luxury & European Elegance",
            rating = 4.9f,
            reviewCount = 48,
            isFeatured = true,
            isOnSale = true,
            imageRes = R.drawable.wedding_hero_banner,
            mediaResList = listOf(R.drawable.wedding_hero_banner, R.drawable.wedding_arch_editorial, R.drawable.wedding_table_centerpiece),
            tiers = listOf(tierAltarSilver, tierAltarGold, tierAltarPlatinum)
        ),
        Catalog(
            id = 2L,
            title = "Ethereal Botanical Garden Arch",
            description = "Lengkungan altar suci terbuka dengan nuansa taman botani asri, wangi lavender alami, dan pilar bunga mawar pastel yang syahdu.",
            categoryId = 2L,
            categoryName = "Entrance & Gazebo",
            theme = "Romantic Garden & Pastel Botanical",
            rating = 4.8f,
            reviewCount = 35,
            isFeatured = true,
            isOnSale = false,
            imageRes = R.drawable.wedding_arch_editorial,
            mediaResList = listOf(R.drawable.wedding_arch_editorial, R.drawable.wedding_hero_banner),
            tiers = listOf(tierArchIntimate, tierArchPavilion)
        ),
        Catalog(
            id = 3L,
            title = "Crystal Ballroom Banquet Tablescape",
            description = "Penataan meja jamuan resepsi mewah dengan centerpiece menjulang tinggi, lilin apung romantis, dan anggrek putih segar.",
            categoryId = 3L,
            categoryName = "Centerpiece & VIP",
            theme = "Glamour Ballroom & Crystal Glow",
            rating = 4.95f,
            reviewCount = 52,
            isFeatured = false,
            isOnSale = false,
            imageRes = R.drawable.wedding_table_centerpiece,
            mediaResList = listOf(R.drawable.wedding_table_centerpiece, R.drawable.wedding_hero_banner),
            tiers = listOf(tierCenterpieceStandard, tierCenterpieceGrand)
        ),
        Catalog(
            id = 4L,
            title = "Blush Quicksand & Lavender Bouquet Suite",
            description = "Rangkaian bunga tangan pengantin eksklusif menggunakan mawar Quicksand premium, ranunculus salju, dan aksen lavender harum.",
            categoryId = 4L,
            categoryName = "Bridal Bouquet",
            theme = "Vintage Romance & Boho Chic",
            rating = 5.0f,
            reviewCount = 29,
            isFeatured = false,
            isOnSale = true,
            imageRes = R.drawable.bridal_bouquet_cbir,
            mediaResList = listOf(R.drawable.bridal_bouquet_cbir, R.drawable.wedding_arch_editorial),
            tiers = listOf(tierBouquetClassic, tierBouquetEntourage)
        )
    )

    // Cart State
    private val _cartItems = MutableStateFlow<List<CartItem>>(
        listOf(
            CartItem(1L, catalogs[0], tierAltarGold, quantity = 1)
        )
    )
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    fun addToCart(catalog: Catalog, tier: CatalogTier) {
        _cartItems.update { current ->
            val existingIndex = current.indexOfFirst { it.catalog.id == catalog.id && it.selectedTier.id == tier.id }
            if (existingIndex >= 0) {
                current.toMutableList().apply {
                    this[existingIndex] = this[existingIndex].copy(quantity = this[existingIndex].quantity + 1)
                }
            } else {
                current + CartItem(System.currentTimeMillis(), catalog, tier, quantity = 1)
            }
        }
    }

    fun updateCartQuantity(cartItemId: Long, newQuantity: Int) {
        if (newQuantity <= 0) {
            removeFromCart(cartItemId)
        } else {
            _cartItems.update { list ->
                list.map { if (it.id == cartItemId) it.copy(quantity = newQuantity) else it }
            }
        }
    }

    fun removeFromCart(cartItemId: Long) {
        _cartItems.update { list -> list.filterNot { it.id == cartItemId } }
    }

    fun clearCart() {
        _cartItems.value = emptyList()
    }

    // Wishlist State
    private val _wishlistIds = MutableStateFlow<Set<Long>>(setOf(1L, 4L))
    val wishlistIds: StateFlow<Set<Long>> = _wishlistIds.asStateFlow()

    fun toggleWishlist(catalogId: Long) {
        _wishlistIds.update { set ->
            if (set.contains(catalogId)) set - catalogId else set + catalogId
        }
    }

    // Vouchers
    val vouchers = listOf(
        Voucher(1L, "WEDDINGBLISS", "Diskon Rp 1.500.000", "Potongan langsung Rp 1.500.000 untuk paket pelaminan", discountAmount = 1500000L, minSpend = 15000000L, expiresAt = "31 Des 2026", isClaimed = true),
        Voucher(2L, "ROSEGOLD10", "Diskon 10% Spesial", "Potongan 10% hingga Rp 2.000.000 untuk semua dekorasi", discountPercent = 10, minSpend = 10000000L, expiresAt = "15 Nov 2026", isClaimed = true),
        Voucher(3L, "EARLYBIRD2026", "Diskon Rp 2.500.000", "Diskon booking tanggal pernikahan 6 bulan ke depan", discountAmount = 2500000L, minSpend = 25000000L, expiresAt = "31 Des 2026", isClaimed = false)
    )

    private val _appliedVoucher = MutableStateFlow<Voucher?>(vouchers[0])
    val appliedVoucher: StateFlow<Voucher?> = _appliedVoucher.asStateFlow()

    fun applyVoucher(voucher: Voucher?) {
        _appliedVoucher.value = voucher
    }

    // Orders
    private val _orders = MutableStateFlow<List<Order>>(
        listOf(
            Order(
                id = 1001L,
                orderNumber = "ORD-2026-8812",
                status = OrderStatus.Processing,
                paymentStatus = PaymentStatus.Paid,
                items = listOf(
                    OrderItem(1L, tierAltarGold.id, "The Royal Parisian Blossom Altar", "Gold Opulence Package", R.drawable.wedding_hero_banner, 25500000L, 1),
                    OrderItem(4L, tierBouquetClassic.id, "Blush Quicksand & Lavender Bouquet Suite", "Classic Bridal & Groom", R.drawable.bridal_bouquet_cbir, 2800000L, 1)
                ),
                eventDate = "18 Desember 2026",
                venueAddress = "Grand Ballroom Hotel Mulia Senayan, Jakarta Pusat",
                subtotal = 28300000L,
                discount = 1500000L,
                serviceFee = 500000L,
                grandTotal = 27300000L,
                vaNumber = "8801 2345 6789 0012",
                vaBank = "BCA Virtual Account",
                vaExpiry = "24 Jam",
                paymentProofUploaded = true,
                createdAt = "28 Okt 2026, 14:20 WIB"
            ),
            Order(
                id = 1002L,
                orderNumber = "ORD-2026-9043",
                status = OrderStatus.Pending,
                paymentStatus = PaymentStatus.Pending,
                items = listOf(
                    OrderItem(2L, tierArchIntimate.id, "Ethereal Botanical Garden Arch", "Intimate Garden Pergola", R.drawable.wedding_arch_editorial, 12000000L, 1)
                ),
                eventDate = "24 Januari 2027",
                venueAddress = "Plataran Hutan Kota, GBK Jakarta",
                subtotal = 12000000L,
                discount = 0L,
                serviceFee = 350000L,
                grandTotal = 12350000L,
                vaNumber = "8899 9876 5432 9043",
                vaBank = "Mandiri Virtual Account",
                vaExpiry = "23 jam 45 menit",
                paymentProofUploaded = false,
                createdAt = "30 Okt 2026, 09:15 WIB"
            )
        )
    )
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    fun createOrder(eventDate: String, address: String, notes: String): Order {
        val items = _cartItems.value
        val subtotal = items.sumOf { it.selectedTier.salePrice ?: it.selectedTier.price }
        val discount = _appliedVoucher.value?.discountAmount ?: 0L
        val fee = 500000L
        val grandTotal = (subtotal - discount + fee).coerceAtLeast(0L)

        val newOrder = Order(
            id = System.currentTimeMillis(),
            orderNumber = "ORD-2026-${(1000..9999).random()}",
            status = OrderStatus.Pending,
            paymentStatus = PaymentStatus.Pending,
            items = items.map {
                OrderItem(
                    catalogId = it.catalog.id,
                    tierId = it.selectedTier.id,
                    catalogTitle = it.catalog.title,
                    tierName = it.selectedTier.name,
                    imageRes = it.catalog.imageRes,
                    price = it.selectedTier.salePrice ?: it.selectedTier.price,
                    quantity = it.quantity
                )
            },
            eventDate = eventDate.ifBlank { "15 Januari 2027" },
            venueAddress = address.ifBlank { "The Tribrata Darmawangsa, Jakarta Selatan" },
            subtotal = subtotal,
            discount = discount,
            serviceFee = fee,
            grandTotal = grandTotal,
            vaNumber = "8801 ${((1000..9999).random())} ${((1000..9999).random())} ${((1000..9999).random())}",
            vaBank = "BCA Virtual Account",
            vaExpiry = "23 jam 59 menit",
            paymentProofUploaded = false,
            createdAt = "Hari ini, Baru Saja"
        )

        _orders.update { listOf(newOrder) + it }
        clearCart()
        return newOrder
    }

    fun markOrderPaid(orderId: Long) {
        _orders.update { list ->
            list.map {
                if (it.id == orderId) it.copy(
                    paymentStatus = PaymentStatus.Paid,
                    status = OrderStatus.Confirmed,
                    paymentProofUploaded = true
                ) else it
            }
        }
    }

    // CBIR (Content-Based Image Retrieval) Visual Search simulation
    fun searchByImage(): List<CbirSearchResult> {
        return listOf(
            CbirSearchResult(1L, catalogs[3], catalogs[3].tiers[0], similarityPercent = 96, distanceScore = 0.04f),
            CbirSearchResult(2L, catalogs[0], catalogs[0].tiers[1], similarityPercent = 91, distanceScore = 0.09f),
            CbirSearchResult(3L, catalogs[1], catalogs[1].tiers[0], similarityPercent = 84, distanceScore = 0.16f),
            CbirSearchResult(4L, catalogs[2], catalogs[2].tiers[0], similarityPercent = 78, distanceScore = 0.22f)
        )
    }

    // Chat
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(1L, "Fleur Décor", isStaff = true, text = "Halo Kak Amanda! Selamat atas rencana pernikahannya ✨ Ada konsep tema warna bunga yang ingin didiskusikan hari ini?", timestamp = "10:00"),
            ChatMessage(2L, "Amanda Kartika", isStaff = false, text = "Halo kak, kami rencana acara di Ballroom Mulia Senayan tanggal 18 Desember. Tertarik sama The Royal Parisian Blossom Altar dengan nuansa champagne blush.", timestamp = "10:02"),
            ChatMessage(3L, "Fleur Décor", isStaff = true, text = "Pilihan yang sangat anggun Kak! Untuk Ballroom Mulia, Gold Opulence Package sangat proporsional karena sudah termasuk cascading floral dan 8 pilar kristal. Mau kami buatkan 3D moodboard gratis?", timestamp = "10:05")
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    fun sendMessage(text: String, imageRes: Int? = null) {
        val user = _currentUser.value ?: defaultUser1
        val newMsg = ChatMessage(
            id = System.currentTimeMillis(),
            senderName = user.fullName,
            isStaff = false,
            text = text,
            imageRes = imageRes,
            timestamp = "Baru saja"
        )
        _chatMessages.update { it + newMsg }
    }

    // Notifications
    private val _notifications = MutableStateFlow<List<NotificationItem>>(
        listOf(
            NotificationItem(1L, "Pesanan Dikonfirmasi", "Pembayaran untuk ORD-2026-8812 telah diterima. Tim desainer bunga sedang menyiapkan moodboard.", "order", "10 mnt lalu", isRead = false),
            NotificationItem(2L, "Promo Akhir Pekan", "Gunakan kode WEDDINGBLISS untuk diskon Rp 1.500.000 pelaminan impian Anda.", "promo", "2 jam lalu", isRead = false),
            NotificationItem(3L, "Konsultasi Terjadwal", "Florist Ratna Dewi siap untuk video call konsultasi dekorasi pukul 15:00 WIB.", "consultation", "1 hari lalu", isRead = true)
        )
    )
    val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

    fun markNotificationsRead() {
        _notifications.update { list -> list.map { it.copy(isRead = true) } }
    }

    // KYC
    fun submitKyc(docType: DocumentType, docNumber: String) {
        _currentUser.update { current ->
            current?.copy(
                kycStatus = KycStatus.Approved,
                completionPercent = 100
            )
        }
    }
}
