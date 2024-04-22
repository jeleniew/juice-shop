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
//    private val URL = "https://juice-shop.exemplary.pl/"

    fun getRequest(endpoint: String, callback: Callback, requireToken: Boolean) {
        var url = URL + endpoint
        var client = OkHttpClient()
        var request = Request.Builder()
            .url(url)
            .apply {
                if (requireToken) {
                    header("Authorization", "Bearer " + SharedPrefHelper.token)
                }
            }
            .get()
            .build()
        client.newCall(request).enqueue(callback)
    }

    fun postRequest(endpoint: String, requestBody: RequestBody, callback: Callback, requireToken: Boolean) {
        var url = URL + endpoint
        var client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .apply {
                if (requireToken) {
                    header("Authorization", "Bearer " + SharedPrefHelper.token)
                }
            }
            .post(requestBody)
            .build()
        client.newCall(request).enqueue(callback)
    }
    fun getAllProducts(onSuccess: (json: String?) -> Unit, onFail: (code: Int, message: String) -> Unit): List<ShopItem> {
        var shopItemList = ArrayList<ShopItem>()

        requestAllProducts(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                Log.d("debug", "Exception: $e")
                onFail(408, e.message?:"")
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
        getRequest("rest/products/search?", callback, false)
    }

    private fun requestProducts(name: String, callback: Callback) {
        getRequest("rest/products/search?q=$name", callback, false)
    }

    fun requestProductPicture(name: String, callback: (image: Bitmap?) -> Unit) {
        var callback1 = object: Callback {
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
        }
        getRequest("assets/public/images/products/$name", callback1, false)
    }

    fun requestPicture(name: String, callback: (image: Bitmap?) -> Unit) {
        // TODO: svg does not work
        var callback1 = object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(null)
            }

            override fun onResponse(call: Call, response: Response) {
                // TODO: svg images do not work
                var inputStream = response.body?.byteStream()
                var responseCode = response.code
                if (responseCode == 200) {
                    Log.d("debug", "resonseCode 200")
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    callback(bitmap)
                } else {
                    Log.d("debug", "resonseCode $responseCode")
                    callback(null)
                }
            }
        }
        getRequest("$name", callback1, false)
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

                    SharedPrefHelper.rememberMe = checked
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
        var requestBody = FormBody.Builder()
            .add("email", email)
            .add("password", password)
            .build()
        postRequest("rest/user/login", requestBody, callback, false)
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
        var requestBody = FormBody.Builder()
            .add("email", email)
            .add("password", password)
            .add("passwordRepeat", passwordRepeat)
            .add("securityQuestion", securityQuestionJson)
            .add("securityAnswer", securityAnswer)
            .build()

        var callback1 = object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                onError(e.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                val json = response.body?.string()
                val responseCode = response.code
                if (responseCode == 201) {
                    var status = JSONObject(json).getString("status")
                    if (status == "success") {
                        logIn(email, password, false, onSuccess, onError)
                    } else {
                        onError(status)
                    }
                } else {
                    var errors = JSONObject(json).getJSONArray("errors")
                    var message = errors.getJSONObject(0).getString("message")
                    onError(message)
                }
            }
        }
        postRequest("api/Users/", requestBody, callback1, false)
    }

    fun getSecurityQuestions(callback: (questionList: List<String>?) -> Unit) {
        var callback1 = object: Callback {
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
        }
        getRequest("api/SecurityQuestions", callback1, false)
    }
    fun requestBalance(callback: (balance: Double) -> Unit) {
        var callback1 = object : Callback {
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
        }
        getRequest("rest/wallet/balance", callback1, true)
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
                Log.d("debug", "profileData: $json")
                // TODO: check code
                var user = JSONObject(json).getJSONObject("user")
                val email = if (user.has("email")) user.getString("email") else ""
                var profileImage = if (user.has("profileImage")) user.getString("profileImage") else ""
                callback(email, profileImage)
            }
        })
    }

    fun requestCards(callback: (json: String?, success: Boolean) -> Unit) {
        var callback1 = object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                TODO("Not yet implemented")
            }

            override fun onResponse(call: Call, response: Response) {
                var json = response.body?.string()
                var responseCode = response.code
                if (responseCode == 200) {
                    var obj = JSONObject(json)
                    var data = obj.getString("data")
                    callback(data, true)
                } else {
                    callback(json, false)
                }
            }
        }
        getRequest("api/Cards", callback1, true)
    }

    fun addCard(cardJson: RequestBody, callback: Callback) {
        postRequest("api/Cards/", cardJson, callback, true)
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

    fun setUserName(username: String, callback: Callback) {
        val requestBody = FormBody.Builder()
            .add("username", username)
            .build()
        var url = URL + "profile"
        var client = OkHttpClient()

        var request = Request.Builder()
            .url(url)
            .header("Cookie", getTokenJson())
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(callback)
    }

    fun requestReviews(shopItemId: Int, callback: Callback) {
        getRequest("rest/products/${shopItemId}/reviews", callback, false)
    }

    fun sendLikeClicked(shopItemId: String, callback: Callback) {
        val requestBody = FormBody.Builder()
            .add("id", shopItemId)
            .build()
        postRequest("rest/products/reviews", requestBody, callback, true)
    }
}