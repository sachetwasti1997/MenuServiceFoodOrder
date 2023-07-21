package com.sachet.MenuService.model

data class OrderCreatedEventModel(
    var id: String? = null,
    var status: String? = null,
    var userId: String? = null,
    var expiresAt: String? = null,
    var menuId: String? = null,
    var menuPrice: Double? = null
)
