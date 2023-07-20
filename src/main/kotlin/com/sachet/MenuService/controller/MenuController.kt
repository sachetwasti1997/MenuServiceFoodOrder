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
        @RequestParam(value = "menu") menu: String,
        @RequestParam(value = "file") file: MultipartFile,
        @RequestHeader headers: Map<String, String>
    ): Menu {
        val objMpr = ObjectMapper()
        //
        //
        val menuJson = objMpr.readValue<Menu>(menu)
        return menuService.createMenu(menuJson, file, headers)
    }

    @GetMapping("/{id}")
    fun getMenu(@PathVariable id: String): Menu? {
        return menuService.getMenu(id)
    }

    @GetMapping
    fun getAll(): MutableIterable<Menu> {
        return menuService.getMenuItems()
    }

    @DeleteMapping
    fun deleteMenu(@RequestBody menu: Menu) {
        menuService.deleteMenu(menu)
    }

    @PutMapping
    fun updateMenu(@RequestParam("file") file: MultipartFile?, @RequestParam("menu") menu: String): Menu {
        val objMpr: ObjectMapper
        lateinit var menuJson: Menu
        if (file != null) {
            objMpr = ObjectMapper()
            menuJson = objMpr.readValue<Menu>(menu)
        }
        return menuService.updateMenu(menuJson, file)
    }
}