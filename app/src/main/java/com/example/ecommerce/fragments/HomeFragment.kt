package com.example.ecommerce.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.ecommerce.utils.Constants
import com.example.ecommerce.R
import com.example.ecommerce.activities.BaseActivity
import com.example.ecommerce.activities.CartListActivity
import com.example.ecommerce.activities.SettingsActivity

class HomeFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val sharedPreferences = requireActivity().getSharedPreferences(Constants.ECOMM_PREFERENCES,Context.MODE_PRIVATE)
        val userName = sharedPreferences.getString(Constants.LOGGED_IN_USERNAME, "")

//        (activity as? BaseActivity)?.showSnackBar("Hello ${userName.toString()}", false)


        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        when(id){
            R.id.action_settings ->{
//                (activity as? BaseActivity)?.showSnackBar("Settings clicked", false)
                val intent = Intent(requireActivity(), SettingsActivity::class.java)
                startActivity(intent)

                return true
            }

//            R.id.myCart ->{
//                val intent = Intent(requireActivity(), CartListActivity::class.java)
//                startActivity(intent)
//
//                return true
//            }
        }
        return super.onOptionsItemSelected(item)
    }
}