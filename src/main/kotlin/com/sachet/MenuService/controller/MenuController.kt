package com.sachet.MenuService.controller

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.sachet.MenuService.model.Menu
import com.sachet.MenuService.service.MenuService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/v1/menu")
class MenuController(
    private val menuService: MenuService
) {
    @PostMapping("/create")
    fun createMapping(
        @RequestPart("menu") menu: String,
        @RequestPart("file") file: MultipartFile
    ): Menu {
        val objMpr = ObjectMapper()
        //
        //
        val menuJson = objMpr.readValue<Menu>(menu)
        return menuService.createMenu(menuJson, file)
    }

    @GetMapping("/{id}")
    fun getMenu(@PathVariable id: String): Menu? {
        return menuService.getMenu(id)
    }

    @GetMapping
    fun getAll(): MutableIterable<Menu> {
        return menuService.getMenuItems()
    }
}