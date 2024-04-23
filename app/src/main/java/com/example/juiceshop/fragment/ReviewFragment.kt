package com.example.juiceshop.fragment

import android.graphics.Bitmap
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.juiceshop.R
import com.example.juiceshop.adapters.ReviewAdapter
import com.example.juiceshop.adapters.ShopItemAdapter
import com.example.juiceshop.databinding.FragmentReviewBinding
import com.example.juiceshop.model.Review
import com.example.juiceshop.utils.ApiManager
import com.example.juiceshop.utils.Constants
import com.example.juiceshop.utils.SharedPrefHelper
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class ReviewFragment : Fragment() {

    private var binding: FragmentReviewBinding? = null
    private lateinit var itemImage: ImageView
    private lateinit var itemName: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var price: TextView
    private lateinit var reviewsTextView: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var commentEditText: EditText
    private lateinit var countChar: TextView
    private lateinit var submitButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReviewBinding.inflate(inflater, container, false)
        val root = binding!!.root

        itemImage = root.findViewById(R.id.item_image)
        itemName = root.findViewById(R.id.itemName)
        descriptionTextView = root.findViewById(R.id.description)
        price = root.findViewById(R.id.price)
        reviewsTextView = root.findViewById(R.id.reviews)
        recyclerView = root.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        commentEditText = root.findViewById(R.id.addComment)
        countChar = root.findViewById(R.id.countChar)
        submitButton = root.findViewById(R.id.submit)

        val productId = requireArguments().getInt(Constants.PRODUCT_ID)
        val productName = requireArguments().getString(Constants.PRODUCT_NAME)
        val image = requireArguments().getParcelable<Bitmap>(Constants.PRODUCT_IMAGE)
        val price = requireArguments().getString(Constants.PRODUCT_PRICE)

        itemImage.setImageBitmap(image)
        this.itemName.text = productName
        this.price.text = price + "¤"

        loadReviews(productId)

        reviewsTextView.setOnClickListener {
            if (recyclerView.visibility == View.VISIBLE) {
                recyclerView.visibility = View.GONE
            } else {
                recyclerView.visibility = View.VISIBLE
            }
        }

        commentEditText.addTextChangedListener (
            object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    countChar.text = "${s?.length.toString()}/160"
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            }
        )

        submitButton.setOnClickListener {
            submitButton.isEnabled = false
            var message = commentEditText.text
            if (message != null) {
                var email = SharedPrefHelper.email
                email?.let {
                    ApiManager.puComment(
                        productId.toString(),
                        message.toString(),
                        email,
                        object : Callback {
                            override fun onFailure(call: Call, e: IOException) {
                                submitButton.isEnabled = true
                                TODO("Not yet implemented")
                            }

                            override fun onResponse(call: Call, response: Response) {
                                Log.d("debug", "response submit: ${response.message}")
                                if (response.isSuccessful) {
                                    commentEditText.text.clear()
                                    loadReviews(productId)
                                }
                                submitButton.isEnabled = true
                            }

                        })
                }
            }
        }

        return root
    }

    fun loadReviews(productId: Int) {
        ApiManager.requestReviews(productId, object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("ShopItemAdapter", "Failed to fetch reviews for $productId", e)
            }

            override fun onResponse(call: Call, response: Response) {
                val json = response.body?.string()
                if (response.isSuccessful) {
                    Log.d("debug", "review: $json")
                    var jsonObject = JSONObject(json)
                    var dataArray = jsonObject.getJSONArray("data")
                    var reviewList = ArrayList<Review>()
                    reviewsTextView.text = "Reviews (${dataArray.length()})"
                    for(i in 0 until dataArray.length()) {
                        var reviewJson = dataArray.getJSONObject(i)
                        var message = reviewJson.getString("message")
                        val author = reviewJson.getString("author")
                        val product = reviewJson.getInt("product")
                        val likesCount = reviewJson.getInt("likesCount")
                        val likedByJsonArray = reviewJson.getJSONArray("likedBy")
                        val likedBy = ArrayList<String>()
                        for(j in 0 until likedByJsonArray.length()) {
                            likedBy.add(likedByJsonArray.getString(j))
                        }
                        val id = reviewJson.getString("_id")
                        val liked = reviewJson.getBoolean("liked")
                        reviewList.add(Review(
                            message, author, product, likesCount, likedBy, id, liked
                        ))
                    }

                    requireActivity().runOnUiThread {
                        showReviews(reviewList)
                    }

                } else {
                    Log.d("debug", "review fail: ${response.message}")
                }
            }
        })
    }

    fun showReviews(reviewList: List<Review>) {
        var adapter = ReviewAdapter(requireActivity(), requireContext(), reviewList)
        recyclerView.adapter = adapter
    }

}