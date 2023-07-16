package com.sachet.MenuService.repository

import com.sachet.MenuService.model.Menu
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface MenuRepository: CrudRepository<Menu, String> {
}