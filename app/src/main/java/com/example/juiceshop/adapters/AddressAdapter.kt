package com.example.juiceshop.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.juiceshop.R
import com.example.juiceshop.model.Address

class AddressAdapter (
    private val context: Context,
    private var itemList: List<Address>,
    private val showContinueButton: (Boolean) -> Unit): RecyclerView.Adapter<AddressAdapter.AddressViewHolder>() {

    private var selectedPosition = RecyclerView.NO_POSITION

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.address_item, parent, false)
        return AddressViewHolder(view)
    }

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        val item = itemList[position]
        holder.bind(item, position == selectedPosition)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class AddressViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.nameText)
        private val addressTextView: TextView = itemView.findViewById(R.id.addressText)
        private val countryTextView: TextView = itemView.findViewById(R.id.countryText)
        private val editButton: ImageView = itemView.findViewById(R.id.editButton)
        private val deleteButton: ImageView = itemView.findViewById(R.id.deleteButton)
        @SuppressLint("ResourceAsColor")
        fun bind(item: Address, isSelected: Boolean) {
            Log.d("Debug", "bindingning")
            nameTextView.text = item.fullName
            addressTextView.text = "${item.streetAddress}, ${item.city}, ${item.state}, ${item.zipCode}"
            countryTextView.text = item.country

            itemView.setBackgroundColor(if (isSelected) R.color.grey else R.color.transparent)

            itemView.setOnClickListener {
                if (adapterPosition == RecyclerView.NO_POSITION) return@setOnClickListener

                notifyItemChanged(selectedPosition)
                selectedPosition = adapterPosition
                notifyItemChanged(selectedPosition)

                showContinueButton(true)
            }
        }
    }
}