package com.example.ecommerce.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.ecommerce.R
import com.example.ecommerce.viewModels.CartViewModel

class OrdersFragment : Fragment() {
    private lateinit var cartViewModel: CartViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        cartViewModel =
            ViewModelProviders.of(this).get(CartViewModel::class.java)
        val view = inflater.inflate(R.layout.fragment_orders, container, false)

//        val textView: TextView = view.findViewById(R.id.text_dashboard)
        cartViewModel.text.observe(viewLifecycleOwner, Observer {
//            textView.text = it
        })

        return view
    }

}