package com.example.ecommerce.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommerce.R
import com.example.ecommerce.adapters.CartListAdapter
import com.example.ecommerce.model.Address
import com.example.ecommerce.model.CartItem
import com.example.ecommerce.utils.Constants

class CheckoutActivity : BaseActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var tvCheckoutAddressType: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var tvCheckoutFullName: TextView
    private lateinit var tvCheckoutAddress: TextView
    private lateinit var tvCheckoutAdditionalNote: TextView
    private lateinit var tvCheckoutOtherDetails: TextView
    private lateinit var btnPlaceOrder: Button
    private var subTotal: String? = null
    private lateinit var mCartListItems: ArrayList<CartItem>
    private lateinit var tvMobileNo: TextView
    private lateinit var tvItemsReceipt: TextView
    private var addressDetails: Address? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        initializeViews()
        setupActionBar()

        hideStatusBar()

        addressDetails = intent.getParcelableExtra(Constants.EXTRA_SELECTED_ADDRESS)
        mCartListItems = intent.getParcelableArrayListExtra(Constants.ALL_CART_LIST)!!

        loadCartListItemDetails()

        if (addressDetails != null) {
            tvCheckoutFullName.text = addressDetails!!.name
            tvCheckoutAddressType.text = addressDetails!!.type
            tvCheckoutAddress.text = addressDetails!!.address
            tvCheckoutAdditionalNote.text = addressDetails!!.additionalNote
            tvMobileNo.text = addressDetails!!.mobile.toString()

            if (addressDetails!!.otherDetails!!.isNotEmpty()) {
                tvCheckoutOtherDetails.text = addressDetails!!.otherDetails
//            tvItemsReceipt.text = addressDetails!!.
            }
        }


        btnPlaceOrder.setOnClickListener {
            val intent = Intent(this@CheckoutActivity, OrdersActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadCartListItemDetails() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        val adapter = CartListAdapter(this, mCartListItems, false)
        recyclerView.adapter = adapter

        for (item in mCartListItems) {
            val availableQuantity = item.productQuantity

            if (availableQuantity!! > 0) {
                val price = item.productPrice
                val cartQuantity = item.cartQuantity

//                subTotal += (price!! * cartQuantity!!).toString()
            }

        }

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
        toolbar = findViewById(R.id.toolbar_checkout_activity)
        tvCheckoutFullName = findViewById(R.id.tv_checkout_full_name)
        tvCheckoutAddress = findViewById(R.id.tv_checkout_address)
        tvCheckoutAdditionalNote = findViewById(R.id.tv_checkout_additional_note)
        tvCheckoutOtherDetails = findViewById(R.id.tv_checkout_other_details)
        tvCheckoutAddressType = findViewById(R.id.tv_checkout_address_type)
        tvMobileNo = findViewById(R.id.tv_mobile_number)
        tvItemsReceipt = findViewById(R.id.tv_items_receipt)
        recyclerView = findViewById(R.id.rv_cart_list_items)
        btnPlaceOrder = findViewById(R.id.btn_place_order)
    }
}