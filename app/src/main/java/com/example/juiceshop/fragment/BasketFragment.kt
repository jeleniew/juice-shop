package com.example.juiceshop.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.juiceshop.R
import com.example.juiceshop.adapters.BasketItemAdapter
import com.example.juiceshop.adapters.CreditCardAdapter
import com.example.juiceshop.databinding.FragmentBasketBinding
import com.example.juiceshop.model.BasketItem
import com.example.juiceshop.model.CreditCard
import com.example.juiceshop.utils.ApiManager
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class BasketFragment : Fragment() {

    private var binding: FragmentBasketBinding? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: BasketItemAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBasketBinding.inflate(inflater, container, false)
        val root: View = binding!!.root

        recyclerView = root.findViewById(R.id.recyclerView)

        recyclerView.layoutManager = LinearLayoutManager(context)

        ApiManager.getBasket( object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                TODO("Not yet implemented")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    var json = response.body?.string()
                    Log.d("debug", "basket: $json")
                    var jsonObject = JSONObject(json)
                    var dataObject = jsonObject.getJSONObject("data")
                    if (dataObject.has("Products")) {
                        val productsArray = dataObject.getJSONArray("Products")
                        val productsList = mutableListOf<BasketItem>()

                        Log.d("debug", "product's length: ${productsArray.length()}")
                        for (i in 0 until productsArray.length()) {
                            val productJson = productsArray.getJSONObject(i)
                            val basketItem = productJson.getJSONObject("BasketItem")
                            val id = basketItem.getInt("id")
                            val productName = productJson.getString("name")
                            val productQuantity = basketItem.getInt("quantity")
                            val productPrice = productJson.getDouble("price")
                            val productImageUrl = productJson.getString("image")

                            ApiManager.requestProductPicture(productImageUrl) { image ->
                                if (image != null) {
                                    val basketItem = BasketItem(id, image, productName, productQuantity, productPrice)
                                    productsList.add(basketItem)
                                    Log.d("debug", "currentProduct: ${productsList.size}")

                                    if (productsList.size == productsArray.length()) {
                                        showItems(productsList)
                                    }
                                } else {
                                    Log.e("debug", "Failed to download image for product: $productName")
                                }
                            }
                        }
                    }
                }
            }
        })

        return root
    }

    fun showItems(itemList: List<BasketItem>) {
        Log.d("debug", "showItems: ${itemList.size}")
        activity?.runOnUiThread {
            adapter = BasketItemAdapter(requireContext(), itemList)
            recyclerView.adapter = adapter
        }
    }

}