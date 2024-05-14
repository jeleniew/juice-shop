package com.example.juiceshop.fragment

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.juiceshop.utils.ApiManager
import com.example.juiceshop.R
import com.example.juiceshop.databinding.FragmentHomeBinding
import com.example.juiceshop.adapters.ShopItemAdapter
import com.example.juiceshop.model.ShopItem
import org.json.JSONArray

class HomeFragment : Fragment() {

    private var binding: FragmentHomeBinding? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ShopItemAdapter
    private lateinit var searchView: SearchView
    private lateinit var progressBar: ProgressBar
    private lateinit var notFoundTextView: TextView


    @SuppressLint("ResourceType")
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {

        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding!!.root

        recyclerView = root.findViewById(R.id.item_list)
        searchView = root.findViewById(R.id.search_view)
        progressBar = root.findViewById(R.id.progressBar)
        notFoundTextView = root.findViewById(R.id.not_found_text)

        var json = activity?.intent?.getStringExtra("ALL_ITEM_LIST")

        parseJsonToItemList(json)
        recyclerView.layoutManager = LinearLayoutManager(context)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                progressBar.visibility = View.VISIBLE;
                ApiManager.getProducts(
                    searchView.query.toString(),
                    onFail = { _: Int, _: String ->
                        activity?.runOnUiThread {
                            progressBar.visibility = View.INVISIBLE;
                            notFoundTextView.text = "Nothing was not found";
                        }
                    },
                    onSuccess = { parseJsonToItemList(it) })
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                // TODO
                return false
            }
        })

        searchView.setOnCloseListener {
            progressBar.visibility = View.VISIBLE
            // TODO: why getProducts?
            ApiManager.getProducts(
                searchView.query.toString(),
                onFail = { _: Int, _: String ->
                    activity?.runOnUiThread {
                        progressBar.visibility = View.INVISIBLE;
                        notFoundTextView.text = "Nothing was not found";
                    }
                },
                onSuccess = {
                    activity?.runOnUiThread {
                        progressBar.visibility = View.INVISIBLE;
                        searchView.clearFocus()
                    }
                })
            false
        }

        return root
    }

    private fun parseJsonToItemList(json: String?) {
        if (json == null) {
            activity?.runOnUiThread {
                progressBar.visibility = View.INVISIBLE
                adapter = ShopItemAdapter(requireContext(), emptyList(), findNavController())
                recyclerView.adapter = adapter
            }
            return
        }
        Log.d("debug", "produckts: $json")
        var productList = JSONArray(json)
        var itemList = ArrayList<ShopItem>()

        for (i in 0 until productList.length()) {
            Thread {
                val product = productList.getJSONObject(i)
                val id = product.getInt("id")
                val name = product.getString("name")
                val price = product.getString("price")
                val imageName = product.getString("image")
                ApiManager.requestProductPicture(imageName,
                    callback = { image: Bitmap? ->
                        itemList.add(ShopItem(id, name, price, image))
                        synchronized(itemList) {
                            if (itemList.size == productList.length()) {
                                var sortedList = itemList.sortedBy { it.name }
                                activity?.runOnUiThread {
                                    progressBar.visibility = View.INVISIBLE
                                    adapter = ShopItemAdapter(requireContext(), sortedList, findNavController())
                                    recyclerView.adapter = adapter
                                }
                            }
                        }
                    }
                )
            }.start()
        }
    }

    override fun onResume() {
        super.onResume()
        progressBar.visibility = View.VISIBLE
        ApiManager.getProducts(
            searchView.query.toString(),
            onFail = { _: Int, _: String ->
                activity?.runOnUiThread {
                    progressBar.visibility = View.INVISIBLE;
                    notFoundTextView.text = "Nothing was not found";
                }
            },
            onSuccess = {
                    json ->
                activity?.runOnUiThread {
                    progressBar.visibility = View.INVISIBLE;
                    json?.let { parseJsonToItemList(it) }
                    searchView.clearFocus()
                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}