package com.sachet.MenuService.service

import com.sachet.MenuService.model.User
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.*
import java.util.function.Function
import javax.crypto.SecretKey


@Service
class JwtUtil {
    @Value("\${secure_key}")
    private val SECRET_KEY: String? = null
    fun extractUsername(token: String?): String {
        return extractClaim<String>(token, Function<Claims, String> { obj: Claims -> obj.getSubject() })
    }

    fun extractExpiration(token: String?): Date {
        return extractClaim<Date>(token, Function<Claims, Date> { obj: Claims -> obj.getExpiration() })
    }

    fun <T> extractClaim(token: String?, claimsResolver: Function<Claims, T>): T {
        val claims: Claims = extractAllClaims(token)
        return claimsResolver.apply(claims)
    }

    fun extractAllClaims(token: String?): Claims {
        val key: SecretKey = io.jsonwebtoken.security.Keys.hmacShaKeyFor(SECRET_KEY!!.toByteArray())
        return Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .getBody()
    }

    private fun isTokenExpired(token: String?): Boolean {
        return extractExpiration(token).before(Date())
    }

    fun validateToken(token: String?, userModel: User): Boolean {
        val username = extractUsername(token)
        return username == userModel.email && !isTokenExpired(token)
    }
}
