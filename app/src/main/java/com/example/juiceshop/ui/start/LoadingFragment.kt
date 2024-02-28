package com.example.juiceshop.ui.start

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.juiceshop.ApiManager
import com.example.juiceshop.databinding.FragmentLoadingBinding

class LoadingFragment: Fragment() {

    private var binding: FragmentLoadingBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoadingBinding.inflate(inflater, container, false)
        val root: View = binding!!.root
        ApiManager.getAllProducts { startMainActivity() }

        return root
    }

    private fun startMainActivity() {
        var intent = Intent()
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}