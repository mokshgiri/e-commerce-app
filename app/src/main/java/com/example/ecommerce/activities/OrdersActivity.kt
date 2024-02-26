package com.example.ecommerce.activities

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.example.ecommerce.R

class OrdersActivity : BaseActivity() {

    private lateinit var toolbar : Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders)

        hideStatusBar()

        initializeViews()
        setupActionBar()
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
        toolbar = findViewById(R.id.toolbar_orders_activity)
    }
}