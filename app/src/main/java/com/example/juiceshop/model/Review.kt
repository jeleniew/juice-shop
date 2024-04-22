package com.example.juiceshop.model

class Review(
    var message: String,
    var author: String,
    var product: Int,
    var likesCount: Int,
    var likedBy: ArrayList<String>,
    var id: String,
    var liked: Boolean) {

}