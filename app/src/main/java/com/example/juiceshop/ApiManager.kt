package com.example.juiceshop

import android.content.SharedPreferences
import android.util.Log
import com.example.juiceshop.ui.ShopItem
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
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

                    when (statusCode) {
                        200 -> {
                            var data = JSONObject(responseBody).get("data").toString()
                            onSuccess(data)
                        }
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
                    if(response.code == 200) {
                        // TODO: in case of non-existing keys we need to deal with exception
                        var token = JSONObject(json).getJSONObject("authentication").getString("token")
                        SharedPrefHelper.token = token
                        if (checked && responseCode == 200) {
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
                        Log.d("debug","$json")
                        var jsonArray = JSONObject(json).getJSONArray("data")
                        val questionList = mutableListOf<String>()

                        for (i in 0 until jsonArray.length()) {
                            val questionObject = jsonArray.getJSONObject(i)
                            val questionName = questionObject.getString("question")
                            Log.d("debug", "question: $questionName  id: ${questionObject.getString("id")}")
                            questionList.add(questionName)
                        }
                        callback(questionList)
                    } else {
                        callback(null)
                    }
                }
            })
        }
    }
}