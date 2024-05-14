package com.example.juiceshop.model

import android.graphics.Bitmap

class BasketItem(
    var id: Int,
    var image: Bitmap,
    var name: String,
    var quantity: Int,
    var price: Double
)