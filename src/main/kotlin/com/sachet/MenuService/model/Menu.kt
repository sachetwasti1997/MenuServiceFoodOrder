package com.sachet.MenuService.model

import jakarta.validation.constraints.NotBlank
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class Menu(
    @Id var id: String ?= null,
    @field:NotBlank(message = "Please enter a valid name!")
    var name: String ?= null,
    var description: String ?= null,
    var specialTag: String ?= null,
    var category: String ?= null,
    @field:NotBlank(message = "Price cannot be empty")
    var price: Double ?= null,
    var image: String ?= null
)
