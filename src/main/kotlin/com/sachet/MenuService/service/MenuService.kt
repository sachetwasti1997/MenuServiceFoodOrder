package com.sachet.MenuService.service

import com.google.cloud.storage.BlobId
import com.google.cloud.storage.BlobInfo
import com.google.cloud.storage.Storage
import com.sachet.MenuService.exceptions.NotFoundException
import com.sachet.MenuService.model.Menu
import com.sachet.MenuService.producer.MenuCreatedEventPublisher
import com.sachet.MenuService.repository.MenuRepository
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class MenuService(
    private val menuRepository: MenuRepository,
    private val storage: Storage,
    private val jwtUtil: JwtUtil,
    private val menuCreatedEventPublisher: MenuCreatedEventPublisher
) {
    @Value("\${bucket_name}")
    lateinit var bucket_name: String

    fun createMenu(@Valid menu: Menu, multipartFile: MultipartFile, headers: Map<String, String>): Menu {
        val bearer = "Bearer "
        menu.userId = jwtUtil.extractUsername(headers["authorization"]!!.substring(bearer.length))
        println(menu)
        val menuSaved = menuRepository.save(menu)
        Thread {
            val fileName = "${System.nanoTime()}${multipartFile.originalFilename}"
            val blobId = BlobId.of(bucket_name, fileName)
            val blobInfo = BlobInfo.newBuilder(blobId).build()
            val str = storage.create(blobInfo, multipartFile.bytes)
            menu.image = str.name
            menuRepository.save(menu)
        }.start()
        menuCreatedEventPublisher.sendMenuCreatedEvent(menu)
        return menuSaved
    }

    fun getMenu(id: String): Menu {
        val menu = menuRepository.findById(id)
        if (menu.isEmpty) {
            throw NotFoundException("Menu with id $id not found!")
        }
        return menu.get()
    }

    fun getMenuItems(): MutableIterable<Menu> {
        return menuRepository.findAll()
    }

    fun deleteMenu(menu: Menu) {
        menuRepository.delete(menu)
        Thread {
            val b = BlobId.of(bucket_name, menu.image)
            storage.delete(b)
        }.start()
    }

    fun updateMenu(menu: Menu, multipartFile: MultipartFile?): Menu {
        val menuL = menuRepository.save(menu)
        Thread {
            if (multipartFile != null) {
                val fileName = "${System.nanoTime()}${multipartFile.originalFilename}"
                val blobId = BlobId.of(bucket_name, fileName)
                val blobInfo = BlobInfo.newBuilder(blobId).build()
                val str = storage.create(blobInfo, multipartFile.bytes)
                menu.image = str.name
                menuRepository.save(menu)
            }
        }.start()
        return menuL
    }
}