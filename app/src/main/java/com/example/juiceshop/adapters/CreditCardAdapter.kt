package com.example.juiceshop.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.juiceshop.R
import com.example.juiceshop.model.CreditCard

class CreditCardAdapter(private val context: Context, private var itemList: List<CreditCard>): RecyclerView.Adapter<CreditCardAdapter.CreditCardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CreditCardViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.credit_card_item, parent, false)
        return CreditCardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CreditCardViewHolder, position: Int) {
        val item = itemList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class CreditCardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cardNumberTextView: TextView = itemView.findViewById(R.id.cardNumber)
        private val cardNameTextView: TextView = itemView.findViewById(R.id.cardName)
        private val expireDateTextView: TextView = itemView.findViewById(R.id.expireDate)

        fun bind(item: CreditCard) {
            cardNumberTextView.text = item.cardNumber
            cardNameTextView.text = item.name
            expireDateTextView.text = item.expiryMonth + "/" + item.expiryYear
        }
    }
}