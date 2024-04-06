package com.example.juiceshop.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.example.juiceshop.model.ShopItem
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

object ApiManager {

    private val URL = "https://juice-shop.herokuapp.com/"
//        private val URL = "https://juice-shop.exemplary.pl/"
    fun getAllProducts(onSuccess: (json: String?) -> Unit, onFail: (code: Int, message: String) -> Unit): List<ShopItem> {
        var shopItemList = ArrayList<ShopItem>()

        requestAllProducts(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                Log.d("debug", "Exception: $e")
            }

            override fun onResponse(call: Call, response: Response) {
                var responseBody = response.body?.string()
                var statusCode = response.code
                var responseMessage = response.message

                when (statusCode) {
                    200 -> {
                        var data = JSONObject(responseBody).get("data").toString()
                        onSuccess(data)
                    }
                    else -> onFail(statusCode, responseMessage)
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
                var responseBody = response.body?.string()
                var statusCode = response.code
                var responseMessage = response.message

                var data = JSONObject(responseBody).get("data").toString()

                when (statusCode) {
                    200 -> { onSuccess(data) }
                    else -> onFail(statusCode, responseMessage)
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

    fun requestProductPicture(name: String, callback: (image: Bitmap?) -> Unit) {
        var url = URL + "assets/public/images/products/" + name
        var client = OkHttpClient()
        var request = Request.Builder()
            .url(url)
            .get()
            .build()
        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(null)
            }

            override fun onResponse(call: Call, response: Response) {
                var inputStream = response.body?.byteStream()
                var responseCode = response.code
                if (responseCode == 200) {
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    callback(bitmap)
                } else {
                    callback(null)
                }
            }
        })
    }

    fun requestPicture(name: String, callback: (image: Bitmap?) -> Unit) {
        var url = URL + name
        var client = OkHttpClient()
        var request = Request.Builder()
            .url(url)
            .get()
            .build()
        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(null)
            }

            override fun onResponse(call: Call, response: Response) {
                var inputStream = response.body?.byteStream()
                var responseCode = response.code
                if (responseCode == 200) {
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    callback(bitmap)
                } else {
                    callback(null)
                }
            }
        })
    }

    fun logIn(email: String,
              password: String,
              checked: Boolean,
              onSuccess: () -> Unit,
              onError: (text: String?) -> Unit) {
        requestLogIn(email, password, object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d("debug", "got exception: $e")
                onError(e.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                var json = response.body?.string()
                var responseCode = response.code
                if(responseCode == 200) {
                    // TODO: in case of non-existing keys we need to deal with exception
                    var token = JSONObject(json).getJSONObject("authentication").getString("token")
                    SharedPrefHelper.token = token  // TODO if not checked then we should log out
                    if (checked) {
                        SharedPrefHelper.email = email
                        SharedPrefHelper.password = password
                    }
                    onSuccess()
                } else {
                    onError(json)
                }
            }
        })
    }

    private fun requestLogIn(email: String, password: String, callback: Callback) {
        var url = URL + "rest/user/login"
        var client = OkHttpClient()

        var requestBody = FormBody.Builder()
            .add("email", email)
            .add("password", password)
            .build()

        var request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()
        client.newCall(request).enqueue(callback)
    }

    fun createSecurityQuestionJson(id: Int, question: String, date: String): String {
        val securityQuestion = JSONObject()
            .put("id", id)
            .put("question", question)
            .put("createdAt", date)
            .put("updatedAt", date)
        return securityQuestion.toString()
    }

    fun register(email: String, password: String, passwordRepeat: String, securityQuestionJson: String, securityAnswer: String, onSuccess: () -> Unit, onError: (String?) -> Unit) {
        var url = URL + "api/Users/"
        var client = OkHttpClient()

        var requestBody = FormBody.Builder()
            .add("email", email)
            .add("password", password)
            .add("passwordRepeat", passwordRepeat)
            .add("securityQuestion", securityQuestionJson)
            .add("securityAnswer", securityAnswer)
            .build()

        var request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()
        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                onError(e.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                val json = response.body?.string()
                val responseCode = response.code
                if (responseCode == 200) {
                    var status = JSONObject(json).getString("status")
                    if (status == "success") {
                        logIn(email, password, false, onSuccess, onError)
                    } else {
                        onError(status)
                    }
                } else {
                    onError(response.message)
                }
            }
        })
    }

    fun getSecurityQuestions(callback: (questionList: List<String>?) -> Unit) {
        var url = URL + "api/SecurityQuestions"
        var client = OkHttpClient()

        var request = Request.Builder()
            .url(url)
            .get()
            .build()
        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(null)
            }

            override fun onResponse(call: Call, response: Response) {
                val json = response.body?.string()
                val responseCode = response.code
                if (responseCode == 200) {
                    var jsonArray = JSONObject(json).getJSONArray("data")
                    val questionList = mutableListOf<String>()

                    for (i in 0 until jsonArray.length()) {
                        val questionObject = jsonArray.getJSONObject(i)
                        val questionName = questionObject.getString("question")
                        questionList.add(questionName)
                    }
                    callback(questionList)
                } else {
                    callback(null)
                }
            }
        })
    }
    fun requestBalance(callback: (balance: Double) -> Unit) {
        val url = URL + "rest/wallet/balance"
        val client = OkHttpClient()

        val request = Request.Builder()
            .url(url)
            .header("Authorization", "Bearer " + SharedPrefHelper.token)
//            .header("Cookie", getTokenJson())
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d("debug", "wallet: failed")
                callback(0.0)
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                val responseCode = response.code

                if (responseCode == 200) {
                    val jsonObject = JSONObject(responseBody)
                    val balance = jsonObject.getDouble("data")
                    callback(balance)
                } else {
                    callback(0.0)
                }
            }
        })
    }

    private fun getTokenJson(): String {
        // NullPointerException
        var token = SharedPrefHelper.token

        return "token=\"${token!!}\""
    }

    fun requestProfileData(callback: (email: String, profileImage: String) -> Unit) {
        var url = URL + "rest/user/whoami"
        var client = OkHttpClient()

        var request = Request.Builder()
            .url(url)
            .header("Cookie", getTokenJson())
            .get()
            .build()

        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d("debug", "profile: failed")
                TODO("Not yet implemented")
            }

            override fun onResponse(call: Call, response: Response) {
                var json = response.body?.string()
                // TODO: check code
                var user = JSONObject(json).getJSONObject("user")
                val email = if (user.has("email")) user.getString("email") else ""
                var profileImage = if (user.has("profileImage")) user.getString("profileImage") else ""
                callback(email, profileImage)
            }
        })
    }

    fun requestCards(callback: (json: String?) -> Unit) {
        var url = URL + "api/Cards"
        var client = OkHttpClient()

        var request = Request.Builder()
            .url(url)
            .header("Authorization", "Bearer " + SharedPrefHelper.token)
            .get()
            .build()

        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                TODO("Not yet implemented")
            }

            override fun onResponse(call: Call, response: Response) {
                var json = response.body?.string()
                var responseCode = response.code
                if (responseCode == 200) {
                    var obj = JSONObject(json)
                    var data = obj.getString("data")
                    callback(data)
                } else {
                    callback(null)
                }
            }
        })
    }

    fun addCard(cardJson: RequestBody) {
        var url = URL + "api/Cards/"
        var client = OkHttpClient()

        var request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer " + SharedPrefHelper.token)
            .post(cardJson)
            .build()
        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d("debug", "cards: failed")
                TODO("Not yet implemented")
            }

            override fun onResponse(call: Call, response: Response) {
                var json = response.body?.string()
                var responseCode = response.code
                if (responseCode == 200) {
                } else {
                }
            }
        })
    }

    fun createCardJson(fullName: String, cardNum: Long, expMonth: String, expYear: String): RequestBody {
        val jsonObject = JSONObject()
        jsonObject.put("fullName", fullName)
        jsonObject.put("cardNum", cardNum)
        jsonObject.put("expMonth", expMonth)
        jsonObject.put("expYear", expYear)
        val jsonMediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
        return RequestBody.create(jsonMediaType, jsonObject.toString())
    }
}