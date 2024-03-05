package com.example.juiceshop.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.juiceshop.R
import com.example.juiceshop.databinding.FragmentHomeBinding
import com.example.juiceshop.ui.ShopItemAdapter
import com.example.juiceshop.ui.ShopItem
import org.json.JSONArray

class HomeFragment : Fragment() {

    private var binding: FragmentHomeBinding? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ShopItemAdapter


    @SuppressLint("ResourceType")
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {

        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding!!.root


        var json = activity?.intent?.getStringExtra("ALL_ITEM_LIST")
        Log.d("debug", "json: $json")
        var productList = JSONArray(json)

        var itemList = ArrayList<ShopItem>()

        for (i in 0 until productList.length()) {
            val product = productList.getJSONObject(i)
            val name = product.getString("name")
            val price = product.getString("price")
            val image = product.getString("image")
            itemList.add(ShopItem(name, price, image))
        }

        adapter = context?.let { ShopItemAdapter(it, itemList) }!!
        recyclerView = root.findViewById(R.id.item_list)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}