package com.example.juiceshop.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.juiceshop.R
import com.example.juiceshop.databinding.FragmentAddNewAddressBinding
import com.example.juiceshop.databinding.FragmentBasketBinding
import com.example.juiceshop.utils.ApiManager
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

class AddNewAddressFragment : Fragment() {

    private var binding: FragmentAddNewAddressBinding? = null
    private lateinit var countryText: EditText
    private lateinit var nameText: EditText
    private lateinit var mobileNumberText: EditText
    private lateinit var zipCodeText: EditText
    private lateinit var addressText: EditText
    private lateinit var cityText: EditText
    private lateinit var stateText: EditText
    private lateinit var backButton: Button
    private lateinit var submitButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddNewAddressBinding.inflate(inflater, container, false)
        val root: View = binding!!.root

        countryText = root.findViewById(R.id.countryText)
        nameText = root.findViewById(R.id.nameText)
        mobileNumberText = root.findViewById(R.id.mobileNumberText)
        zipCodeText = root.findViewById(R.id.ZIPCodeText)
        addressText = root.findViewById(R.id.addressText)
        cityText = root.findViewById(R.id.cityText)
        stateText = root.findViewById(R.id.stateText)

        backButton = root.findViewById(R.id.backButton)
        submitButton = root.findViewById(R.id.submitButton)

        submitButton.setOnClickListener {
            val country = countryText.text.toString()
            val name = nameText.text.toString()
            val mobileNumber = mobileNumberText.text.toString().toLong()
            val zipCode = zipCodeText.text.toString()
            val address = addressText.text.toString()
            val city = cityText.text.toString()
            val state = stateText.text.toString()
            ApiManager.addAddress(country, name, mobileNumber, zipCode, address, city, state, object: Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.d("debug", "onFailure: $e")
                }

                override fun onResponse(call: Call, response: Response) {
                    Log.d("debug", "response: ${response.code}")
                    if (response.isSuccessful) {
                        // TODO popup
                        requireActivity().runOnUiThread {  Toast.makeText(requireContext(), "Added address", Toast.LENGTH_LONG)}
                    }
                }

            })
        }

        return root
    }
}