package com.example.juiceshop.ui

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.juiceshop.R

class ShopItemAdapter(private val context: Context, private val itemList: List<ShopItem>): RecyclerView.Adapter<ShopItemAdapter.ShopItemViewHolder>() {

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
            priceTextView.text = item.price + ""
            val imageResourceId = itemView.resources.getIdentifier(item.image, "drawable", itemView.context.packageName)
            if (imageResourceId != 0) {
                imageView.setImageResource(imageResourceId)
            } else {
                Log.d("debug", "failed to set the image: ${item.image} with identifier $imageResourceId")
            }
            Log.d("debug", "package ${itemView.context.packageName}")
        }
    }
}