package com.sachet.MenuService.exceptions

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
//import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType

//@ControllerAdvice
//class GlobalExceptionHandler: ResponseEntityExceptionHandler() {
//
//    @ExceptionHandler(NotFoundException::class)
//    fun handleMenuNotFoundException(ex: NotFoundException, request: WebRequest): ResponseEntity<Any> {
//        val resp = LinkedHashMap<String, Any>()
//        resp["message"] = ex.message.toString()
//        resp["status"] = HttpStatus.NOT_FOUND
//        return ResponseEntity(resp, HttpStatus.NOT_FOUND)
//    }
//
//}