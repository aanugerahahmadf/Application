package com.wedding.fleurdecor.data.model

data class User(
    val id: Long,
    val fullName: String,
    val firstName: String,
    val lastName: String,
    val username: String,
    val email: String,
    val phone: String,
    val avatarUrl: String? = null,
    val kycStatus: KycStatus = KycStatus.Pending,
    val completionPercent: Int = 85,
    val isTwoFactorEnabled: Boolean = false,
    val appLockPin: String? = "1234"
)

data class SavedAccount(
    val id: Long,
    val user: User,
    val tokenRef: String,
    val isActive: Boolean
)

data class Category(
    val id: Long,
    val name: String,
    val slug: String,
    val description: String,
    val imageRes: Int,
    val catalogCount: Int
)

data class CatalogTier(
    val id: Long,
    val catalogId: Long,
    val name: String,
    val description: String,
    val price: Long, // in IDR
    val salePrice: Long? = null,
    val capacity: String,
    val stock: Int = 5,
    val features: List<String>
)

data class Catalog(
    val id: Long,
    val title: String,
    val description: String,
    val categoryId: Long,
    val categoryName: String,
    val theme: String,
    val rating: Float,
    val reviewCount: Int,
    val isFeatured: Boolean = false,
    val isOnSale: Boolean = false,
    val imageRes: Int,
    val mediaResList: List<Int>,
    val tiers: List<CatalogTier>
)

data class CartItem(
    val id: Long,
    val catalog: Catalog,
    val selectedTier: CatalogTier,
    val quantity: Int = 1
)

data class Voucher(
    val id: Long,
    val code: String,
    val title: String,
    val description: String,
    val discountAmount: Long? = null,
    val discountPercent: Int? = null,
    val minSpend: Long = 0,
    val expiresAt: String,
    val isClaimed: Boolean = false
)

data class OrderItem(
    val catalogId: Long,
    val tierId: Long,
    val catalogTitle: String,
    val tierName: String,
    val imageRes: Int,
    val price: Long,
    val quantity: Int
)

data class Order(
    val id: Long,
    val orderNumber: String,
    val status: OrderStatus,
    val paymentStatus: PaymentStatus,
    val items: List<OrderItem>,
    val eventDate: String,
    val venueAddress: String,
    val subtotal: Long,
    val discount: Long,
    val serviceFee: Long,
    val grandTotal: Long,
    val vaNumber: String? = null,
    val vaBank: String? = null,
    val vaExpiry: String? = null,
    val qrisUrl: String? = null,
    val paymentProofUploaded: Boolean = false,
    val createdAt: String
)

data class CbirSearchResult(
    val id: Long,
    val catalog: Catalog,
    val matchedTier: CatalogTier?,
    val similarityPercent: Int,
    val distanceScore: Float
)

data class ChatMessage(
    val id: Long,
    val senderName: String,
    val isStaff: Boolean,
    val text: String,
    val imageRes: Int? = null,
    val timestamp: String,
    val isRead: Boolean = true
)

data class NotificationItem(
    val id: Long,
    val title: String,
    val body: String,
    val type: String, // "order", "promo", "consultation", "system"
    val timestamp: String,
    val isRead: Boolean = false
)

data class ReviewItem(
    val id: Long,
    val authorName: String,
    val rating: Int,
    val date: String,
    val comment: String,
    val tierName: String
)
