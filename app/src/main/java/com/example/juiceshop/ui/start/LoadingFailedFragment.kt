package com.example.juiceshop.ui.start

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.juiceshop.R
import com.example.juiceshop.databinding.FragmentLoadingFailedBinding

class LoadingFailedFragment(statusCode: Int, message: String): Fragment() {

    private var binding: FragmentLoadingFailedBinding? = null
    private var statusCode: Int
    private var message: String

    init {
        this.statusCode = statusCode
        this.message = message
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoadingFailedBinding.inflate(inflater, container, false)
        val root: View = binding!!.root
        var textView = root.findViewById<TextView>(R.id.loading_failed_message)
        textView.text = ("$statusCode $message")
        return root
    }

}