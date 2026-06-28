package com.halliSanthe.app.models

import java.io.Serializable

data class Artisan(
    val name: String,
    val specialty: String,
    val imageRes: Int? = null, 
    val bio: String = "",
    val age: Int = 45,
    val phone: String = "+919876543210"
) : Serializable {
    val initials: String
        get() {
            val parts = name.split(" ")
            return if (parts.size > 1) {
                "${parts[0][0]}${parts[1][0]}".uppercase()
            } else {
                "${name[0]}".uppercase()
            }
        }
}
