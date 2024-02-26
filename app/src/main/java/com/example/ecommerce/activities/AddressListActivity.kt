package com.example.ecommerce.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommerce.R
import com.example.ecommerce.SwipeToEditDeleteCallback
import com.example.ecommerce.adapters.AddressListAdapter
import com.example.ecommerce.firebase.FirebaseClass
import com.example.ecommerce.model.Address
import com.example.ecommerce.model.CartItem
import com.example.ecommerce.utils.Constants
import java.util.ArrayList

class AddressListActivity : BaseActivity() {

    private lateinit var toolbar : Toolbar
    private lateinit var tvTitle : TextView
    private lateinit var addAddressBtn : TextView
    private lateinit var recyclerView : RecyclerView
    private lateinit var tvNoAddressFound : TextView
    private lateinit var swipeLeftRightLinLayout : LinearLayout
    private lateinit var mAddressListItems : ArrayList<Address>
    private lateinit var mCartListItems : ArrayList<CartItem>
    private var fromCartListActivity : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address_list)

        fromCartListActivity = intent.getBooleanExtra(Constants.EXTRA_CART_ACTIVITY, false)
        mCartListItems = ArrayList()

        if (fromCartListActivity) {
            mCartListItems = intent.getParcelableArrayListExtra(Constants.ALL_CART_LIST)!!
        }

        hideStatusBar()
        initializeViews()
        setupActionBar()
        loadAllAddressDetails()

        addAddressBtn.setOnClickListener {
            val intent = Intent(this@AddressListActivity, AddEditAddressActivity::class.java)
            startActivity(intent)
        }

    }

    private fun loadAllAddressDetails() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirebaseClass().getAllAddressDetails(this)
    }

    private fun setupActionBar() {
        setSupportActionBar(toolbar)

        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_ios_new_24_white)

        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun initializeViews() {
        toolbar = findViewById(R.id.toolbar_address_list_activity)
        addAddressBtn = findViewById(R.id.tv_add_address)
        tvTitle = findViewById(R.id.tv_title)
        recyclerView = findViewById(R.id.rv_address_list)
        tvNoAddressFound = findViewById(R.id.tv_no_address_found)
        swipeLeftRightLinLayout = findViewById(R.id.swipeLeftRightLinLayout)
    }

    fun addressListDataFetchSuccess(allAddressListItems: ArrayList<Address>) {
        hideProgressDialog()

        if (allAddressListItems.size > 0) {

                mAddressListItems = allAddressListItems

                swipeLeftRightLinLayout.visibility = View.VISIBLE
                recyclerView.visibility = View.VISIBLE
                tvNoAddressFound.visibility = View.GONE

                recyclerView.layoutManager = LinearLayoutManager(this)

                val adapter = AddressListAdapter(this, mAddressListItems, fromCartListActivity, mCartListItems)
                recyclerView.adapter = adapter

            if (!fromCartListActivity) {

                val editSwipeHandler =
                    object : SwipeToEditDeleteCallback(this@AddressListActivity) {
                        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                            when (direction) {
                                ItemTouchHelper.RIGHT -> {
                                    adapter.notifyEditItem(
                                        this@AddressListActivity,
                                        viewHolder.adapterPosition
                                    )
                                }

                                ItemTouchHelper.LEFT -> {
//                            adapter.notifyEditItem(this@AddressListActivity,viewHolder.adapterPosition,)
//                            showSnackBar("Delete", true)
                                    showProgressDialog(resources.getString(R.string.please_wait))
                                    FirebaseClass().deleteAddressItemFromFirebase(
                                        this@AddressListActivity,
                                        mAddressListItems[viewHolder.adapterPosition].id
                                    )
                                }
                            }

                        }

                    }

                val editItemTouchHelper = ItemTouchHelper(editSwipeHandler)
                editItemTouchHelper.attachToRecyclerView(recyclerView)

            }
            else{
                addAddressBtn.visibility = View.GONE
                swipeLeftRightLinLayout.visibility = View.GONE
                tvTitle.text = resources.getString(R.string.select_address)
            }
        }

        else{
            swipeLeftRightLinLayout.visibility = View.GONE
            recyclerView.visibility = View.GONE
            tvNoAddressFound.visibility = View.VISIBLE
//            llCheckout.visibility = View.GONE

        }
    }

    fun noDataExistsInDatabase() {
        hideProgressDialog()
    }

    fun deleteAddressSuccess() {
        hideProgressDialog()
        showSnackBar(resources.getString(R.string.address_deleted_success), false)

        if (mAddressListItems.isEmpty()){
            recyclerView.visibility = View.GONE
            swipeLeftRightLinLayout.visibility = View.GONE
            tvNoAddressFound.visibility = View.VISIBLE
        }
        else{
            loadAllAddressDetails()
        }
    }

    fun deleteAddressFailed() {
        hideProgressDialog()
        showSnackBar(resources.getString(R.string.address_deleted_failed), true)

        loadAllAddressDetails()
    }

}