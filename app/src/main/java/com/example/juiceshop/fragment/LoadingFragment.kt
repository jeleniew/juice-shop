package com.example.juiceshop.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.juiceshop.utils.ApiManager
import com.example.juiceshop.activity.MainActivity
import com.example.juiceshop.R
import com.example.juiceshop.utils.SharedPrefHelper
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
        SharedPrefHelper.init(requireContext())
        if (SharedPrefHelper.token == null && SharedPrefHelper.email != null && SharedPrefHelper.password != null) {
            ApiManager.logIn(
                SharedPrefHelper.email!!, SharedPrefHelper.password!!, true,
            onSuccess = {
                getItems()
            }, onError = {
                getItems()
            })
        } else {
            getItems()
        }

        return root
    }

    private fun getItems() {
        ApiManager.getAllProducts(
            onSuccess = {
                    json ->
                json?.let { startMainActivity(it) }
            }
        ) { statusCode, message ->
            startLoadingFailedFragment(statusCode, message)
        }
    }
    private fun startMainActivity(json: String) {
        var intent = Intent(context, MainActivity::class.java)
        intent.putExtra("ALL_ITEM_LIST", json)
        startActivity(intent)
        activity?.finish()
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