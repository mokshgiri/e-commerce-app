package com.example.ecommerce.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommerce.R
import com.example.ecommerce.adapters.CartListAdapter
import com.example.ecommerce.firebase.FirebaseClass
import com.example.ecommerce.model.CartItem
import com.example.ecommerce.model.Product
import com.example.ecommerce.utils.Constants


class CartListActivity : BaseActivity() {

    private lateinit var toolbar : Toolbar
    private lateinit var tvNoCartItemFound : TextView
    private lateinit var tvSubTotal : TextView
    private lateinit var llCheckout : LinearLayout
    private lateinit var btnCheckOut : Button
    private lateinit var tvShippingCharge : TextView
    private lateinit var tvTotalAmount : TextView
    private lateinit var recyclerView : RecyclerView
    private lateinit var mAllProductsList : ArrayList<Product>
    private lateinit var mCarListItems : ArrayList<CartItem>
    private lateinit var adapter : CartListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart_list)

        hideStatusBar()
        initializeViews()

        mAllProductsList = intent.getParcelableArrayListExtra<Product>("allProductsList")!!

        setupActionBar()
        loadCartListData(false)

        btnCheckOut.setOnClickListener {
            val intent = Intent(this@CartListActivity, AddressListActivity::class.java)
            intent.putExtra(Constants.EXTRA_CART_ACTIVITY, true)
            intent.putExtra(Constants.ALL_CART_LIST, mCarListItems)
            startActivity(intent)
        }
    }

    private fun loadCartListData(updateQuantity: Boolean) {

        if (!updateQuantity) {
            showProgressDialog(resources.getString(R.string.please_wait))
        }

        FirebaseClass().getCartList(this@CartListActivity)
    }

    private fun setupActionBar() {
        setSupportActionBar(toolbar)

        val actionBar = supportActionBar
        actionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.baseline_arrow_back_ios_new_24_white)
        }
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun initializeViews() {
        toolbar = findViewById(R.id.toolbar_cart_list_activity)
        tvNoCartItemFound = findViewById(R.id.tv_no_cart_item_found)
        tvSubTotal = findViewById(R.id.tv_sub_total)
        tvShippingCharge = findViewById(R.id.tv_shipping_charge)
        tvTotalAmount = findViewById(R.id.tv_total_amount)
        llCheckout = findViewById(R.id.ll_checkout)
        recyclerView = findViewById(R.id.rv_cart_items_list)
        btnCheckOut = findViewById(R.id.btn_checkout)
    }

    fun allCartListItemsFetchSuccess(allCartListItems: ArrayList<CartItem>) {

        hideProgressDialog()

//        for (product in mAllProductsList){
//            for (cartItem in allCartListItems){
//                if(product.productId == cartItem.productId){
//                    cartItem.productQuantity = product.productQuantity
//
//                    if (product.productQuantity == 0){
//                        cartItem.productQuantity = product.productQuantity
//                    }
//
//                }
//            }
//        }

        if (allCartListItems.size > 0) {

            mCarListItems = allCartListItems

            llCheckout.visibility = View.VISIBLE
            recyclerView.visibility = View.VISIBLE
            tvNoCartItemFound.visibility = View.GONE

            val newList = ArrayList<Int>()
            for (cartItem in mCarListItems) {

                newList.add(cartItem.productPrice!! * cartItem.cartQuantity!!)
            }

            recyclerView.layoutManager = LinearLayoutManager(this@CartListActivity)
            recyclerView.setHasFixedSize(true)
            adapter = CartListAdapter(this, mCarListItems, true)
            recyclerView.adapter = adapter

            val totalSubAmount = newList.sum()
            tvSubTotal.text = totalSubAmount.toString()
            tvShippingCharge.text = 10.toString()

            tvTotalAmount.text = (totalSubAmount + 10).toString()

            Log.d("totalAmount", totalSubAmount.toString())
        }

        else{
            recyclerView.visibility = View.GONE
            tvNoCartItemFound.visibility = View.VISIBLE
            llCheckout.visibility = View.GONE
        }
    }

    fun errorGettingAllCartListItems() {
        hideProgressDialog()
    }

    fun successProductListFromFirebase(allProductListItems: ArrayList<Product>) {

    }

    fun updateCartProductQuantityAndPriceSuccess() {
        loadCartListData(true)
    }

    fun cartItemDeletedSuccess(position: Int) {
        adapter.notifyItemRemoved(position)
        showSnackBar("Item deleted successfully", true)

        if (mCarListItems.isEmpty()) {
            recyclerView.visibility = View.GONE
            tvNoCartItemFound.visibility = View.VISIBLE
            llCheckout.visibility = View.GONE
        }
    }
}