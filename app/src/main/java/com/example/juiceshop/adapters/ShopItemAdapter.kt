package com.example.juiceshop.adapters

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
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
        }
    }
}