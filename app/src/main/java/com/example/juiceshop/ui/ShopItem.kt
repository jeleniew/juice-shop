package com.example.juiceshop.ui

import android.util.Log

class ShopItem(name: String, price: String, image: String) {

    var name: String
    var price: String
    var image: String

    init {
        this.name = name
        this.price = price
        this.image = removeExtention(image)
        Log.d("debug","image: $image")
    }

    private fun removeExtention(image: String): String {
        if(image.contains(".")) {
            return image.substring(0, image.indexOf('.'))
        }
        return image
    }

}
