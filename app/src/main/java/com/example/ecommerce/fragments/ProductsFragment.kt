package com.example.ecommerce.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommerce.R
import com.example.ecommerce.activities.AddProductActivity
import com.example.ecommerce.adapters.MyProductsListAdapter
import com.example.ecommerce.firebase.FirebaseClass
import com.example.ecommerce.model.Product
import com.myshoppal.ui.fragments.BaseFragment

class ProductsFragment : BaseFragment() {

    private lateinit var allProductsList : ArrayList<Product>
    private lateinit var tvNoProductsFound : TextView
    private lateinit var recyclerView : RecyclerView

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_products, container, false)

        if (isAdded) {
            // If attached, load product details
            loadAllProductDetails()
        }

        return view

    }

    private fun initializeViews() {

        tvNoProductsFound = requireView().findViewById(R.id.tv_no_products_found)
        recyclerView = requireView().findViewById(R.id.recyclerView)

    }

    private fun loadAllProductDetails() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirebaseClass().getProductDetails(this)
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_product_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        when(id){
            R.id.add_product ->{

//                (activity as? BaseActivity)?.showSnackBar("Settings clicked", false)
                val intent = Intent(requireActivity(), AddProductActivity::class.java)
                startActivity(intent)

                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun productsDetailsFetchSuccess(productList: ArrayList<Product>) {
        hideProgressDialog()

        // Check if the fragment is attached to an activity
        if (isAdded) {
            if (productList.size > 0) {
                allProductsList = productList

                recyclerView.visibility = View.VISIBLE
                tvNoProductsFound.visibility = View.GONE

                recyclerView.layoutManager = LinearLayoutManager(activity)
                recyclerView.setHasFixedSize(true)
                val adapter = MyProductsListAdapter(requireActivity(), productList, this)
                recyclerView.adapter = adapter
            } else {
                recyclerView.visibility = View.GONE
                tvNoProductsFound.visibility = View.VISIBLE
            }
        }
//
//        for (product in productList) {
//            Log.d("productName", product.productTitle!!)
//
//        }

    }

    fun deleteProduct(productId: String?) {
        showDeleteAlertDialog(productId)
    }

    private fun showDeleteAlertDialog(productId: String?) {
        AlertDialog.Builder(requireContext())
            .setMessage("Are you sure you want to delete this item ?")
            .setTitle("Delete Product")
            .setIcon(R.drawable.ic_vector_delete)
            .setPositiveButton("Yes") { _, _ ->
                showProgressDialog(resources.getString(R.string.please_wait))
                FirebaseClass().deleteProductFromFirebase(this, productId)
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    fun productDeletedSuccess() {
        hideProgressDialog()

        if (allProductsList.isEmpty()){
            recyclerView.visibility = View.GONE
            tvNoProductsFound.visibility = View.VISIBLE
        }

//        showSnackBar(resources.getString(R.string.product_deleted_success_message), false)
    }
}