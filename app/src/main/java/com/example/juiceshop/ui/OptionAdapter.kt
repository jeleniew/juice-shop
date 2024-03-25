package com.example.juiceshop.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.juiceshop.Option
import com.example.juiceshop.R

class OptionAdapter(
    private val context: Context,
    private var itemList: List<Option>,
    private var onClickListeners: List<View.OnClickListener>
) : RecyclerView.Adapter<OptionAdapter.OptionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OptionViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.option, parent, false)
        return OptionViewHolder(view)
    }

    override fun onBindViewHolder(holder: OptionViewHolder, position: Int) {
        val item = itemList[position]
        holder.bind(item, onClickListeners[position])
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class OptionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.optionText)

        fun bind(item: Option, onClickListener: View.OnClickListener) {
            nameTextView.text = item.name
            itemView.setOnClickListener(onClickListener)
        }
    }
}
