package com.example.juiceshop.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.juiceshop.R
import com.example.juiceshop.databinding.FragmentAddCardBinding
import com.example.juiceshop.utils.ApiManager
import com.example.juiceshop.utils.PopUpManager
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class AddCardFragment : Fragment() {

    private var binding: FragmentAddCardBinding? = null
    private lateinit var nameEditView: EditText
    private lateinit var nameTextView: TextView
    private lateinit var cardNumberEditView: EditText
    private lateinit var countDigitsTextView: TextView
    private lateinit var monthEditText: EditText
    private lateinit var monthTextView: TextView
    private lateinit var yearEditText: EditText
    private lateinit var yearTextView: TextView
    private lateinit var submitButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddCardBinding.inflate(inflater, container, false)
        val root: View = binding!!.root

        nameEditView = root.findViewById(R.id.name)
        nameTextView = root.findViewById(R.id.nameHint)
        cardNumberEditView = root.findViewById(R.id.cardNumber)
        countDigitsTextView = root.findViewById(R.id.countDigits)
        monthEditText = root.findViewById(R.id.expiryMonth)
        monthTextView = root.findViewById(R.id.monthHint)
        yearEditText = root.findViewById(R.id.expiryYear)
        yearTextView = root.findViewById(R.id.yearHint)
        submitButton = root.findViewById(R.id.submitButton)

        nameEditView.setOnFocusChangeListener { _, _ ->
            if (nameEditView.text == null) {
                nameTextView.visibility = View.VISIBLE
            } else {
                nameTextView.visibility = View.INVISIBLE
            }
        }

//        nameEditView.setOnKeyListener {
//            if (nameEditView.text != null) {
//                nameTextView.visibility = View.INVISIBLE
//            }
//        }

        cardNumberEditView.setOnFocusChangeListener { _, focus ->
            // TODO: counting numbers
            var cardNumber = cardNumberEditView.text.toString()
            if (!focus && ( cardNumber.length != 16 )) {
                countDigitsTextView.visibility = View.VISIBLE
                countDigitsTextView.text = "Please enter a valid sixteen digit card number."
                countDigitsTextView.setTextColor(resources.getColor(R.color.red))
            } else {
                countDigitsTextView.visibility = View.INVISIBLE
            }
        }

        monthEditText.setOnFocusChangeListener { _, focus ->
            var month = monthEditText.text.toString().toIntOrNull()
            if(!focus && (month == null || month > 12 || month < 1 )) {
                monthTextView.visibility = View.VISIBLE
            } else {
                monthTextView.visibility = View.INVISIBLE
            }
        }

        yearEditText.setOnFocusChangeListener { _, focus ->
            var month = yearEditText.text.toString().toIntOrNull()
            if(!focus && (month == null || month > 2099 || month < 2080 )) {
                yearTextView.visibility = View.VISIBLE
            } else {
                yearTextView.visibility = View.INVISIBLE
            }
        }

        submitButton.setOnClickListener {
            var cardName = nameEditView.text.toString()
            var cardNumber = cardNumberEditView.text.toString().toLongOrNull()
            var month = monthEditText.text.toString().toIntOrNull()
            var year = yearEditText.text.toString().toIntOrNull()

            if (cardName != null && cardNumber != null) {
                ApiManager.addCard(
                    ApiManager.createCardJson(
                    nameEditView.text.toString(),
                    cardNumber,
                    monthEditText.text.toString(),
                    yearEditText.text.toString()
                ),
                object: Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        Log.d("debug", "cards: failed")
                        TODO("Not yet implemented")
                    }

                    override fun onResponse(call: Call, response: Response) {
                        var json = response.body?.string()
                        var responseCode = response.code
                        Log.d("debug", "responseCode: $responseCode" )
                        if (responseCode == 201) {
                            activity?.runOnUiThread { findNavController().popBackStack() }
                        } else {
                            Log.d("Debug", "add Card failes: $json")
                            var errors = JSONObject(json).getJSONArray("errors")
                            var message = errors.getJSONObject(0).getString("message")
                            activity?.runOnUiThread {
                                PopUpManager.showPopUp(
                                    requireContext(),
                                    message,
                                    "Ok",
                                    null
                                )
                            }
                        }
                    }
                })
            }
        }

        return root
    }
}