package com.example.juiceshop.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.juiceshop.R
import com.example.juiceshop.adapters.CreditCardAdapter
import com.example.juiceshop.databinding.FragmentCardsBinding
import com.example.juiceshop.model.CreditCard
import com.example.juiceshop.utils.ApiManager
import org.json.JSONArray

class CardsFragment : Fragment() {

    private var binding: FragmentCardsBinding? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CreditCardAdapter
    private lateinit var addCardButton: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCardsBinding.inflate(inflater, container, false)
        val root: View = binding!!.root

        recyclerView = root.findViewById(R.id.recyclerView)
        addCardButton = root.findViewById(R.id.addCardButton)

        recyclerView.layoutManager = LinearLayoutManager(context)

        addCardButton.setOnClickListener {
            findNavController().navigate(R.id.action_cards_to_add_cards)
        }

        ApiManager.requestCards {
            var cardArrayJson = JSONArray(it)
            var cardList= mutableListOf<CreditCard>()
            for (i in 0 until cardArrayJson.length()) {
                val cardJson = cardArrayJson.getJSONObject(i)
                val fullName = cardJson.getString("fullName")
                val cardNum = cardJson.getString("cardNum")
                val expMonth = cardJson.getString("expMonth")
                val expYear = cardJson.getString("expYear")

                val card = CreditCard(fullName, cardNum, expMonth, expYear)
                cardList.add(card)
            }
            showCards(cardList)
        }

        return root
    }

    fun showCards(cardList: List<CreditCard>) {
        activity?.runOnUiThread {
            adapter = CreditCardAdapter(requireContext(), cardList)
            recyclerView.adapter = adapter
        }
    }
}