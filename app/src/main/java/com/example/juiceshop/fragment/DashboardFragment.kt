package com.example.juiceshop.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.juiceshop.model.Option
import com.example.juiceshop.R
import com.example.juiceshop.utils.SharedPrefHelper
import com.example.juiceshop.databinding.FragmentDashboardBinding
import com.example.juiceshop.adapters.OptionAdapter

class DashboardFragment : Fragment() {

    private var binding: FragmentDashboardBinding? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: OptionAdapter

    @SuppressLint("ResourceType")
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {

        binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding!!.root

        recyclerView = root.findViewById(R.id.item_list)

        var optionList: List<Option>
        var onCLickList: List<View.OnClickListener>

        if (SharedPrefHelper.token != null) {
            optionList = listOf(
                Option("Profile"),
                Option("Digital wallet"),
                Option("My Payment Options"),
                Option("Log out")
            )
            onCLickList = listOf(
                View.OnClickListener { _ ->
                    findNavController().navigate(R.id.action_dashboard_to_profile)
                },
                View.OnClickListener { _ ->
                    findNavController().navigate(R.id.action_dashboard_to_wallet)
                },
                View.OnClickListener { _ ->
                    findNavController().navigate(R.id.action_dashboard_to_cards)
                },
                View.OnClickListener { _ ->
                    SharedPrefHelper.token = null
                    findNavController().navigate(R.id.navigation_dashboard)
                }
            )
        } else {
            optionList = listOf(
                Option("Log in"),
                Option("Register")
            )
            onCLickList = listOf(
                View.OnClickListener { _ ->
                    findNavController().navigate(R.id.action_dashboard_to_login)
                },
                View.OnClickListener { _ ->
                    findNavController().navigate(R.id.action_dashboard_to_register)
                }
            )
        }
        adapter = context?.let { optionList?.let{ OptionAdapter(requireContext(), optionList, onCLickList) }}!!
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}