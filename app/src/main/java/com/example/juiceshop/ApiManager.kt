package com.example.juiceshop

import android.util.Log
import com.example.juiceshop.ui.ShopItemView
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class ApiManager {

    companion object {
        private val URL = "https://juice-shop.herokuapp.com/"

        fun getAllProducts(onSuccess: () -> Unit): List<ShopItemView> {
            var shopItemList = ArrayList<ShopItemView>()

            requestAllProducts(object : Callback {

                override fun onFailure(call: Call, e: IOException) {
                    Log.d("debug", "Exception: $e")
                }

                override fun onResponse(call: Call, response: Response) {
                    Log.d("debug", "Response: $response")
                    var statusCode = response.code
                    when (statusCode) {
                        200 -> onSuccess
                        else -> Log.d("debug", "failed with response code: " + statusCode + " and message: " + response.message)
                    }
                }

            })

            return shopItemList;
        }

        private fun requestAllProducts(callback: Callback) {
            var url = URL + "rest/products/search?"
            var client = OkHttpClient()
            var request = Request.Builder()
                .url(url)
                .get()
                .build()
            client.newCall(request).enqueue(callback)
        }

    }
}