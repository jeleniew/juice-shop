package com.example.juiceshop

import android.util.Log
import com.example.juiceshop.ui.ShopItem
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class ApiManager {

    companion object {
//        private val URL = "https://juice-shop.herokuapp.com/"
        private val URL = "https://juice-shop.exemplary.pl/"
        fun getAllProducts(onSuccess: (json: String?) -> Unit, onFail: (code: Int, message: String) -> Unit): List<ShopItem> {
            var shopItemList = ArrayList<ShopItem>()

            requestAllProducts(object : Callback {

                override fun onFailure(call: Call, e: IOException) {
                    Log.d("debug", "Exception: $e")
                }

                override fun onResponse(call: Call, response: Response) {
                    Log.d("debug", "Response: $response")
                    var responseBody = response.body?.string()
                    var statusCode = response.code
                    var responseMessage = response.message

                    var data = JSONObject(responseBody).get("data").toString()

                    when (statusCode) {
                        200 -> { onSuccess(data) }
                        else -> onFail(statusCode, responseMessage)//Log.d("debug", "failed with response code: " + statusCode + " and message: " + response.message)
                    }
                }

            })

            return shopItemList;
        }

        fun getProducts(name: String, onSuccess: (json: String?) -> Unit, onFail: (code: Int, message: String) -> Unit): List<ShopItem> {
            var shopItemList = ArrayList<ShopItem>()

            requestProducts(name, object : Callback {

                override fun onFailure(call: Call, e: IOException) {
                    Log.d("debug", "Exception: $e")
                }

                override fun onResponse(call: Call, response: Response) {
                    Log.d("debug", "Response: $response")
                    var responseBody = response.body?.string()
                    var statusCode = response.code
                    var responseMessage = response.message

                    var data = JSONObject(responseBody).get("data").toString()

                    when (statusCode) {
                        200 -> { onSuccess(data) }
                        else -> onFail(statusCode, responseMessage)//Log.d("debug", "failed with response code: " + statusCode + " and message: " + response.message)
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

        private fun requestProducts(name: String, callback: Callback) {
            var url = URL + "rest/products/search?q=" + name
            var client = OkHttpClient()
            var request = Request.Builder()
                .url(url)
                .get()
                .build()
            client.newCall(request).enqueue(callback)
        }
    }
}