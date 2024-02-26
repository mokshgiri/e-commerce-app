package com.example.ecommerce.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommerce.R
import com.example.ecommerce.activities.CartListActivity
import com.example.ecommerce.activities.SettingsActivity
import com.example.ecommerce.adapters.DashboardItemsListAdapter
import com.example.ecommerce.adapters.MyProductsListAdapter
import com.example.ecommerce.firebase.FirebaseClass
import com.example.ecommerce.model.Product
import com.example.ecommerce.viewModels.CartViewModel
import com.example.ecommerce.viewModels.DashboardViewModel
import com.myshoppal.ui.fragments.BaseFragment

class DashboardFragment : BaseFragment() {


//    private lateinit var dashboardViewModel: DashboardViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var tvNoDashboardItemsFound: TextView
    private lateinit var mAllProductsList: ArrayList<Product>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (isAdded) {
            initializeViews()
        }
    }

    private fun initializeViews() {
        tvNoDashboardItemsFound = requireView().findViewById(R.id.tv_no_dashboard_items_found)
        recyclerView = requireView().findViewById(R.id.rv_dashboard_items)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
//        dashboardViewModel =
//            ViewModelProviders.of(this).get(DashboardViewModel::class.java)

        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        loadAllDashboardItems()

        return view
    }

    private fun loadAllDashboardItems() {
        showProgressDialog(requireContext().resources.getString(R.string.please_wait))
        FirebaseClass().getAllDashboardProducts(this)
    }

    fun allDashboardProductsFetchSuccess(allProductList: ArrayList<Product>) {
        hideProgressDialog()

//        mAllProductsList = ArrayList()
        if (isAdded) {
            if (allProductList.size > 0) {
                mAllProductsList = allProductList

                recyclerView.visibility = View.VISIBLE
                tvNoDashboardItemsFound.visibility = View.GONE

                recyclerView.layoutManager = GridLayoutManager(activity, 2)
                recyclerView.setHasFixedSize(true)
                val adapter = DashboardItemsListAdapter(requireActivity(), allProductList)
                recyclerView.adapter = adapter
            } else {
                recyclerView.visibility = View.GONE
                tvNoDashboardItemsFound.visibility = View.VISIBLE
            }
        }
    }

    fun errorGettingAllDashboardProducts() {
        hideProgressDialog()
//        showSnackBar(resources.getString(R.string.err_getting_all_dashboard_products), true)
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

            R.id.myCart ->{
                val intent = Intent(requireActivity(), CartListActivity::class.java)
                intent.putExtra("allProductsList", mAllProductsList)
                startActivity(intent)

                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}