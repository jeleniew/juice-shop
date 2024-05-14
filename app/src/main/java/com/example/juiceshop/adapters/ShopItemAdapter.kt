package com.example.juiceshop.adapters

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.recyclerview.widget.RecyclerView
import com.example.juiceshop.R
import com.example.juiceshop.model.Review
import com.example.juiceshop.model.ShopItem
import com.example.juiceshop.utils.ApiManager
import com.example.juiceshop.utils.Constants
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class ShopItemAdapter(
    private val context: Context,
    private var itemList: List<ShopItem>,
    private var navController: NavController
): RecyclerView.Adapter<ShopItemAdapter.ShopItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopItemViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.shop_item, parent, false)
        return ShopItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ShopItemViewHolder, position: Int) {
        val item = itemList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class ShopItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.item_name)
        private val priceTextView: TextView = itemView.findViewById(R.id.item_price)
        private val imageView: ImageView = itemView.findViewById(R.id.item_image)
        private val addToBasketButton: Button = itemView.findViewById(R.id.addToBasket)

        fun bind(item: ShopItem) {
            nameTextView.text = item.name
            priceTextView.text = item.price + "¤"
            val image = item.image
            if (image != null) {
                imageView.setImageBitmap(image)
            } else {
                Log.d("debug", "failed to set the image of ${item.name}")
            }
            itemView.setOnClickListener { _ ->
                var bundle = Bundle()
                bundle.putInt(Constants.PRODUCT_ID, item.id)
                bundle.putString(Constants.PRODUCT_NAME, item.name)
                bundle.putParcelable(Constants.PRODUCT_IMAGE, item.image)
                bundle.putString(Constants.PRODUCT_PRICE, item.price)
                navController.navigate(
                    R.id.action_home_to_review,
                    bundle,
                    NavOptions.Builder().setPopUpTo(R.id.navigation_review, true).build()
                )
            }
            addToBasketButton.setOnClickListener {
                ApiManager.getBasket(object: Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        TODO("Not yet implemented")
                    }

                    override fun onResponse(call: Call, response: Response) {
                        if (response.isSuccessful) {
                            var json = response.body?.string()
                            Log.d("debug", "getBasket result = $json")
                            var productsList =
                                JSONObject(json).getJSONObject("data")
                                    .getJSONArray("Products")
                            val productList = mutableListOf<JSONObject>()
                            for (i in 0 until productsList.length()) {
                                productList.add(productsList.getJSONObject(i))
                            }

                            var basketItemId: Int? = null
                            productList.forEach { product ->
                                if (product.getJSONObject("BasketItem")
                                        .getString("ProductId") == item.id.toString()
                                ) {
                                    basketItemId =
                                        product.getJSONObject("BasketItem").getInt("id")
                                    Log.d(
                                        "debug",
                                        "Znaleziono pasujące id w koszyku: $basketItemId"
                                    )
                                }
                            }
                            if (basketItemId == null) {
                                ApiManager.getItemQuantity(item.id, object: Callback {
                                    override fun onFailure(call: Call, e: IOException) {
                                        TODO("Not yet implemented")
                                    }

                                    override fun onResponse(call: Call, response: Response) {
                                        if (response.isSuccessful) {
                                            var id = JSONObject(response.body?.string()).getJSONObject("data").getInt("id")
                                            addProductToBasket(id, item.id)
                                        } else {
                                            Log.d("debug", "failed to get basketItemId = ${response.body?.string()}")
                                        }
                                    }
                                })
                            } else {
                                ApiManager.addBasketItemId(basketItemId!!, object : Callback {
                                    override fun onFailure(call: Call, e: IOException) {
                                        TODO("Not yet implemented")
                                    }

                                    override fun onResponse(
                                        call: Call,
                                        response: Response
                                    ) {
                                        if (response.isSuccessful) {
                                            var basketItemId = response?.message
                                            var json = response.body?.string()
                                            var data = JSONObject(json).getJSONObject("data")
                                            var id = data.getInt("id")
                                            var quantity = data.getInt("quantity")
                                            ApiManager.changeBasketItemQuantity(
                                                id,
                                                quantity + 1,
                                                object : Callback {
                                                    override fun onFailure(call: Call, e: IOException) {
                                                        TODO("Not yet implemented")
                                                    }

                                                    override fun onResponse(
                                                        call: Call,
                                                        response: Response
                                                    ) {
                                                        addProductToBasket(id, item.id)
                                                    }
                                                })
                                        } else {
                                            var json = response.body?.string()
                                            var error =
                                                JSONObject(json).getString(
                                                    "error"
                                                )
                                            (context as Activity).runOnUiThread {
                                                Toast.makeText(context, error, Toast.LENGTH_SHORT)
                                            }
                                        }
                                    }
                                })
                            }
                        } else {
                            Log.d("debug", "add ${item.id} to basket result: ${response.body?.string()}")
                            var error = JSONObject(response.body?.string()).getString("error")
                            (context as Activity).runOnUiThread {
                                Toast.makeText(context, error, Toast.LENGTH_SHORT)
                            }
                        }
                    }
                })
            }
        }

        fun addProductToBasket(id: Int, productId: Int) {
            ApiManager.addBasketItem(productId, object: Callback {
                override fun onFailure(call: Call, e: IOException) {
                    TODO("Not yet implemented")
                }

                override fun onResponse(call: Call, response: Response) {

                    if (response.isSuccessful) {
                        ApiManager.getBasket(object: Callback {
                            override fun onFailure(call: Call, e: IOException) {
                                TODO("Not yet implemented")
                            }

                            override fun onResponse(call: Call, response: Response) {
                                (context as Activity).runOnUiThread { Toast.makeText(context, "Successfully added TODO", Toast.LENGTH_LONG) }
                            }
                        })
                    } else {
                        Log.d("debug", "add $id to basket result: ${response.body?.string()}")
                    }
                }
            })
        }
    }
}