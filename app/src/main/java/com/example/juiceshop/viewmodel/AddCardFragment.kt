package com.example.juiceshop.viewmodel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.juiceshop.R
import com.example.juiceshop.databinding.FragmentAddCardBinding
import com.example.juiceshop.utils.ApiManager

class AddCardFragment : Fragment() {

    private var binding: FragmentAddCardBinding? = null
    private lateinit var nameEditView: EditText
    private lateinit var cardNumberEditView: EditText
    private lateinit var countDigitsTextView: TextView
    private lateinit var monthEditText: EditText
    private lateinit var yearEditText: EditText
    private lateinit var submitButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddCardBinding.inflate(inflater, container, false)
        val root: View = binding!!.root

        nameEditView = root.findViewById(R.id.name)
        cardNumberEditView = root.findViewById(R.id.cardNumber)
        countDigitsTextView = root.findViewById(R.id.countDigits)
        monthEditText = root.findViewById(R.id.expiryMonth)
        yearEditText = root.findViewById(R.id.expiryYear)
        submitButton = root.findViewById(R.id.submitButton)

        submitButton.setOnClickListener {
            var cardNumber = cardNumberEditView.text.toString().toLongOrNull()
            if (cardNumber != null) {
                ApiManager.addCard(ApiManager.createCardJson(
                    nameEditView.text.toString(),
                    cardNumber,
                    monthEditText.text.toString(),
                    yearEditText.text.toString()
                ))
            }
        }

        return root
    }
}