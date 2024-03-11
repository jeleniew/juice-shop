package com.example.juiceshop.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.juiceshop.ApiManager
import com.example.juiceshop.R
import com.example.juiceshop.databinding.FragmentHomeBinding
import com.example.juiceshop.ui.ShopItemAdapter
import com.example.juiceshop.ui.ShopItem
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

        var products = json?.let { parseJsonToItemList(it) }
        adapter = context?.let { products?.let{ShopItemAdapter(requireContext(), products) }}!!
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                progressBar.visibility = View.VISIBLE;
                Log.d("debug", "looking for: " + searchView.transitionName + searchView.query)
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
                            json?.let { it1 -> parseJsonToItemList(it1) }
                                ?.let { it2 -> adapter.updateProducts(it2) };
                        }
                    })
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                // TODO
                return false
            }
        })

        searchView.setOnCloseListener (object : SearchView.OnCloseListener {
            override fun onClose(): Boolean {
                progressBar.visibility = View.VISIBLE;
                Log.d("debug", "looking for: " + searchView.transitionName + searchView.query)
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
                            json?.let { it1 -> parseJsonToItemList(it1) }
                                ?.let { it2 -> adapter.updateProducts(it2) };
                            searchView.clearFocus()
                        }
                    })
                return false
            }

        })

        return root
    }

    private fun parseJsonToItemList(json: String): ArrayList<ShopItem> {
        var productList = JSONArray(json)
        var itemList = ArrayList<ShopItem>()

        for (i in 0 until productList.length()) {
            val product = productList.getJSONObject(i)
            val name = product.getString("name")
            val price = product.getString("price")
            val image = product.getString("image")
            itemList.add(ShopItem(name, price, image))
        }
        return itemList;
    }

    override fun onResume() {
        super.onResume()
        progressBar.visibility = View.VISIBLE;
        Log.d("debug", "looking for: " + searchView.transitionName + searchView.query)
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
                    json?.let { it1 -> parseJsonToItemList(it1) }
                        ?.let { it2 -> adapter.updateProducts(it2) };
                    searchView.clearFocus()
                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}