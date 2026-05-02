package com.halliSanthe.app.models

import java.io.Serializable

data class Product(

    var productId: String = "",

    var name: String = "",

    var price: String = "",

    var category: String = "",

    var description: String = "",

    var imageUrl: String = "",

    var sellerId: String = "",

    var sellerName: String = "",

    var sellerPhone: String = "",

    var timestamp: Long = System.currentTimeMillis()
) : Serializable