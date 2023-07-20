package com.sachet.MenuService.model

import org.springframework.data.annotation.Id


data class User(
    private val id: String? = null,
    var name: String? = null,
    var userName: String? = null,
    var email: String? = null,
    var password: String? = null,
    var roles: List<String>? = null
)
