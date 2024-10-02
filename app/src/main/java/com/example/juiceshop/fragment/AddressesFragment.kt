package com.example.juiceshop.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.juiceshop.R
import com.example.juiceshop.adapters.AddressAdapter
import com.example.juiceshop.adapters.CreditCardAdapter
import com.example.juiceshop.databinding.FragmentAddressesBinding
import com.example.juiceshop.model.Address
import com.example.juiceshop.model.CreditCard
import com.example.juiceshop.utils.ApiManager
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class AddressesFragment : Fragment() {

    private var binding: FragmentAddressesBinding? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AddressAdapter
    private lateinit var addNewAddressButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddressesBinding.inflate(inflater, container, false)
        val root: View = binding!!.root

        recyclerView = root.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        addNewAddressButton = root.findViewById(R.id.addNewAddressButton)
        addNewAddressButton.setOnClickListener {
            findNavController().navigate(
                R.id.action_addresses_to_add_addresses,
                null,
//                NavOptions.Builder().setPopUpTo(R.id.navigation_login, true).build()
                        )
        }

        ApiManager.getAddresses(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d("debug", "onFailure: $e")
            }

            override fun onResponse(call: Call, response: Response) {
                var json = response.body?.string()
                Log.d("debug", "onSuccuess: ${json}")
                if (response.isSuccessful) {
                    val addressList = mutableListOf<Address>()
                    val jsonObject = JSONObject(json)
                    val dataArray: JSONArray = jsonObject.getJSONArray("data")

                    for (i in 0 until dataArray.length()) {
                        val addressObject = dataArray.getJSONObject(i)
                        val address = Address(
                            userId = addressObject.getInt("UserId"),
                            id = addressObject.getInt("id"),
                            fullName = addressObject.getString("fullName"),
                            mobileNum = addressObject.getLong("mobileNum"),
                            zipCode = addressObject.getString("zipCode"),
                            streetAddress = addressObject.getString("streetAddress"),
                            city = addressObject.getString("city"),
                            state = addressObject.getString("state"),
                            country = addressObject.getString("country")
                        )
                        addressList.add(address)
                    }
                    Log.d("debug", "found ${addressList.size} addresses")
                    showAddresses(addressList)
                }
            }

        })

        return root
    }

    private fun showAddresses(list: List<Address>) {
        activity?.runOnUiThread {
            adapter = AddressAdapter(requireContext(), list) {

            }
            recyclerView.adapter = adapter
        }
    }
}