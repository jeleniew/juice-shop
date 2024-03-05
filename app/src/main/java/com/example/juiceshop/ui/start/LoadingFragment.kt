package com.example.juiceshop.ui.start

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.juiceshop.ApiManager
import com.example.juiceshop.MainActivity
import com.example.juiceshop.R
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
        ApiManager.getAllProducts(
            onSuccess = {
               json ->
                json?.let { startMainActivity(it) }
            }
        ) { statusCode, message ->
            startLoadingFailedFragment(statusCode, message)
        }

        return root
    }

    private fun startMainActivity(json: String) {
        var intent = Intent(context, MainActivity::class.java)
        intent.putExtra("ALL_ITEM_LIST", json)
        startActivity(intent)
    }

    private fun startLoadingFailedFragment(statusCode: Int, message: String) {
        var fragment = parentFragmentManager?.beginTransaction()
        fragment?.replace(R.id.nav_host_fragment_activity_loading, LoadingFailedFragment(statusCode, message))
        fragment?.commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}