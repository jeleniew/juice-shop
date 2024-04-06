package com.example.juiceshop.model

import android.graphics.Bitmap

class ShopItem(id: Int, name: String, price: String, image: Bitmap?) {

    var id: Int
    var name: String
    var price: String
    var image: Bitmap?

    init {
        this.id = id
        this.name = name
        this.price = price
        this.image = image
    }

}
