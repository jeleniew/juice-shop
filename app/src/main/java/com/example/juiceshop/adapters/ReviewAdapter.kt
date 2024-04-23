package com.example.juiceshop.adapters

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.juiceshop.R
import com.example.juiceshop.model.Review
import com.example.juiceshop.utils.ApiManager
import com.example.juiceshop.utils.SharedPrefHelper
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class ReviewAdapter(
    private val activity: Activity,
    private val context: Context,
    private var itemList: List<Review>
) : RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.review, parent, false)
        return ReviewViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val item = itemList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class ReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val authorTextView: TextView = itemView.findViewById(R.id.author)
        private val reviewTextView: TextView = itemView.findViewById(R.id.reviewText)
        private val likeButton: ImageButton = itemView.findViewById(R.id.likeButton)
        private val countLikesTextView: TextView = itemView.findViewById(R.id.countLikes)

        fun bind(item: Review) {
            authorTextView.text = item.author
            reviewTextView.text = item.message
            countLikesTextView.text = item.likesCount.toString()
            var isLiked = (item.likedBy.find { it == SharedPrefHelper.email } != null)
            if (isLiked) {
                likeButton.setImageResource(android.R.drawable.btn_star_big_on)
            }
            // TODO: only when logged we should setOnCLick
            if (SharedPrefHelper.token != null && !isLiked) {
                likeButton.setOnClickListener {
                    ApiManager.sendLikeClicked(item.id, object: Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        Log.d("debug", "sendLikeClicked: $e")
//                        TODO("Not yet implemented")
                    }

                    override fun onResponse(call: Call, response: Response) {
                        var jsonString = response.body?.string()

                        Log.d("debug", "clicked: $jsonString && ${response.code}")
                        if (response.isSuccessful && jsonString != null) {
                            val jsonObject = JSONObject(jsonString)
                            val updatedArray = jsonObject.getJSONArray("updated")
                            val updatedObject = updatedArray.getJSONObject(0)
                            val likesCount = updatedObject.getString("likesCount")
                            val likedByArray = updatedObject.getJSONArray("likedBy")
                            var isLikedByUser = false
                            for (i in 0 until likedByArray.length()) {
                                val email = likedByArray.getString(i)
                                if (email == SharedPrefHelper.email) {
                                    isLikedByUser = true
                                    break
                                }
                            }
                            activity?.runOnUiThread {
                                if (isLikedByUser) {
                                    likeButton.setImageResource(android.R.drawable.btn_star_big_on)
                                } else {
                                    likeButton.setImageResource(android.R.drawable.btn_star_big_off)
                                }
                                countLikesTextView.text = likesCount
                            }
                        }
                    }
                })
                }
            }
        }
    }
}
