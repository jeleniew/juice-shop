package com.example.juiceshop.fragment

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.juiceshop.R
import com.example.juiceshop.databinding.FragmentWalletBinding
import com.example.juiceshop.utils.ApiManager

class WalletFragment : Fragment() {

    private var binding: FragmentWalletBinding? = null
    private lateinit var balanceTextView: TextView
    private lateinit var addAmountEditTextView: EditText
    private lateinit var hintTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWalletBinding.inflate(inflater, container, false)
        var root = binding!!.root

        balanceTextView = root.findViewById(R.id.money)
        addAmountEditTextView = root.findViewById(R.id.addAmountEditTextView)
        hintTextView = root.findViewById(R.id.hintTextView)

        balanceTextView.setOnFocusChangeListener { view, hasFocus ->
            val border = view.background as GradientDrawable
            if (hasFocus) {
                border.setColor(ContextCompat.getColor(requireContext(), R.color.green))
            } else {
                border.setColor(ContextCompat.getColor(requireContext(), R.color.red))
            }
        }
        // TODO set visible on request's response
        addAmountEditTextView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val value = s.toString().toDoubleOrNull()
                if (value == null) {
                    hintTextView.text = "Please enter an amount"
                } else if ( value < 10 || value > 1000 ) {
                    hintTextView.text = "You can add a minimum of 10¤ and only up to 1000¤."
                } else {
                    hintTextView.text = ""
                }
            }
        })

        addAmountEditTextView.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val value = addAmountEditTextView.text.toString().toDoubleOrNull()
                if (value == null) {
                    hintTextView.text = "Please enter an amount"
                } else if ( value < 10 || value > 1000 ) {
                    hintTextView.text = "You can add a minimum of 10¤ and only up to 1000¤."
                } else {
                    hintTextView.text = ""
                    addAmountEditTextView.clearFocus()
                }
            }
            false
        }

        ApiManager.requestBalance {
            setBalance(it)
        }

        return root
    }

    private fun setBalance(balance: Double) {
        activity?.runOnUiThread {
            balanceTextView.text = String.format("%.2f", balance)
        }
    }
}