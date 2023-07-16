package com.sachet.MenuService.service

import com.google.cloud.storage.Acl
import com.google.cloud.storage.BlobId
import com.google.cloud.storage.BlobInfo
import com.google.cloud.storage.Storage
import com.google.protobuf.compiler.PluginProtos.CodeGeneratorResponse.File
import com.sachet.MenuService.exceptions.NotFoundException
import com.sachet.MenuService.model.Menu
import com.sachet.MenuService.repository.MenuRepository
import jakarta.validation.Valid
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.Paths
import java.util.Arrays
import kotlin.io.path.Path
import kotlin.jvm.optionals.getOrNull

@Service
class MenuService(
    private val menuRepository: MenuRepository,
    private val storage: Storage
) {
    fun createMenu(@Valid menu: Menu, multipartFile: MultipartFile): Menu {
        val menuSaved = menuRepository.save(menu)
        Thread {
            val fileName = "${System.nanoTime()}${multipartFile.originalFilename}"
            val blobId = BlobId.of("full_stack_application", fileName)
            val blobInfo = BlobInfo.newBuilder(blobId).build()
            val str = storage.create(blobInfo, multipartFile.bytes)
            menu.image = str.name
            menuRepository.save(menu)
        }.start()
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
}