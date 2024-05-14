package com.example.juiceshop.adapters

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.juiceshop.R
import com.example.juiceshop.model.BasketItem
import com.example.juiceshop.utils.ApiManager
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class BasketItemAdapter (private val context: Context, private var itemList: List<BasketItem>): RecyclerView.Adapter<BasketItemAdapter.BasketItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BasketItemViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.basket_item, parent, false)
        return BasketItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: BasketItemViewHolder, position: Int) {
        val item = itemList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class BasketItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val itemImage: ImageView = itemView.findViewById(R.id.itemImage)
        private val itemName: TextView = itemView.findViewById(R.id.itemName)
        private val minus: ImageButton = itemView.findViewById(R.id.minus)
        private val quantityTextView: TextView = itemView.findViewById(R.id.quantity)
        private val plus: ImageButton = itemView.findViewById(R.id.plus)
        private val price: TextView = itemView.findViewById(R.id.price)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton)

        fun bind(item: BasketItem) {
            itemImage.setImageBitmap(item.image)
            itemName.text = item.name
            minus.setOnClickListener {
                changeQuantity(item, item.quantity - 1)
            }
            quantityTextView.text = item.quantity.toString()
            plus.setOnClickListener {
                changeQuantity(item, item.quantity + 1)
            }
            price.text = item.price.toString() + "¤"
            deleteButton.setOnClickListener {
                ApiManager.deleteBasketItem(item.id, object: Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        TODO("Not yet implemented")
                    }

                    override fun onResponse(call: Call, response: Response) {
                        if (response.isSuccessful) {
                            itemList = itemList.filter { it.id != item.id }
                            (context as Activity).runOnUiThread { notifyDataSetChanged() }
                        } else {
                            var error = JSONObject(response.body?.string()).getString("error")
                            (context as Activity).runOnUiThread { Toast.makeText(context, error, Toast.LENGTH_SHORT).show() }
                        }
                    }
                })
            }
        }

        private fun changeQuantity(item: BasketItem, quantity: Int) {
            ApiManager.changeBasketItemQuantity(item.id, quantity, object: Callback {
                override fun onFailure(call: Call, e: IOException) {
                    TODO("Not yet implemented")
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        Log.d("debug", "quantity success = ${response.body?.string()}")
                        (context as Activity).runOnUiThread { quantityTextView.text = quantity.toString() }
                        item.quantity = quantity
                    } else {
                        var error = JSONObject(response.body?.string()).getString("error")
                        (context as Activity).runOnUiThread { Toast.makeText(context, error, Toast.LENGTH_SHORT) }
                    }
                }
            })
        }
    }
}