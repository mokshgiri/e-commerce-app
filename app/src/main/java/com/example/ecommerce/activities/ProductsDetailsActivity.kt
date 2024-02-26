package com.example.ecommerce.activities

import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import com.example.ecommerce.R
import com.example.ecommerce.firebase.FirebaseClass
import com.example.ecommerce.model.CartItem
import com.example.ecommerce.model.Product
import com.example.ecommerce.utils.Constants
import com.example.ecommerce.utils.GlideLoader
import de.hdodenhof.circleimageview.CircleImageView

class ProductsDetailsActivity : BaseActivity() {

    private lateinit var toolbar : Toolbar
    private lateinit var tvTitle : TextView
    private lateinit var ivProductDetailImage : ImageView
    private lateinit var tvProductDetailTitle : TextView
    private lateinit var etProductDetailPrice : EditText
    private lateinit var etProductDetailDescription : EditText
    private lateinit var etProductDetailQuantity : EditText
    private lateinit var productDetails : Product
    private lateinit var plusSign : CircleImageView
    private lateinit var minusSign : CircleImageView
    private lateinit var addToCart : Button
    private lateinit var goToCart : Button
    private var editable : Boolean ?= false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_products_details)

        hideStatusBar()
        initializeViews()
        setupActionBar()

        loadProductDetailsData()

        editable = intent.getBooleanExtra("editable", false)

        if (editable!!){
            addToCart.text = resources.getString(R.string.update_product)
        }


        tvProductDetailTitle.isEnabled = false
        etProductDetailPrice.isEnabled = false
        etProductDetailDescription.isEnabled = false
        etProductDetailQuantity.isEnabled = false

//        if (FirebaseClass().)

        plusSign.setOnClickListener {
            val currentQuantity = etProductDetailQuantity.text.toString().toInt()

            if (!editable!!) {
                if (currentQuantity < productDetails.productQuantity!!) {
                    etProductDetailQuantity.setText((currentQuantity + 1).toString())
                } else {
                    showSnackBar(
                        "Only ${productDetails.productQuantity} items are there in the stock",
                        true
                    )
                }
            }
            else{
                etProductDetailQuantity.setText((currentQuantity + 1).toString())
            }
        }

        minusSign.setOnClickListener {
            val currentQuantity = etProductDetailQuantity.text.toString().toInt()

            if (currentQuantity > 1) {
                etProductDetailQuantity.setText((currentQuantity - 1).toString())
            }
        }

        addToCart.setOnClickListener {
            if (!editable!!) {
                addToCart()
            }
            else{
                updateProductDetails()
            }
        }
    }

    private fun updateProductDetails() {
        showProgressDialog(resources.getString(R.string.please_wait))

        val productHashMap = HashMap<String, Any>()

        val currentPrice = productDetails.productPrice
        val updatedPrice = etProductDetailPrice.text.toString().toInt()

        val currentDescription = productDetails.productDescription
        val updatedDescription = etProductDetailDescription.text.toString()

        val currentQuantity = productDetails.productQuantity
        val updatedQuantity = etProductDetailQuantity.text.toString().toInt()

        if (currentPrice != updatedPrice){
            productHashMap[Constants.PRODUCT_PRICE] = updatedPrice
        }

        if (currentDescription != updatedDescription){
            productHashMap[Constants.PRODUCT_DESCRIPTION] = updatedDescription
        }

        if (currentQuantity != updatedQuantity){
            productHashMap[Constants.PRODUCT_QUANTITY] = updatedQuantity
        }

        FirebaseClass().updateProductDetails(this, productHashMap, productDetails)
    }

    private fun loadProductDetailsData() {
            val data = productDetails


//

//            tvProductDetailTitle.isEnabled = false
//            etProductDetailPrice.isEnabled = false
//            etProductDetailDescription.isEnabled = false
//            etProductDetailQuantity.isEnabled = false
//        }
        GlideLoader(this@ProductsDetailsActivity).loadImage(data?.productImage, ivProductDetailImage)

        tvProductDetailTitle.text = data?.productTitle
        etProductDetailPrice.text = Editable.Factory.getInstance().newEditable(data?.productPrice.toString())
        etProductDetailDescription.text = Editable.Factory.getInstance().newEditable(data?.productDescription.toString())
        etProductDetailQuantity.text = Editable.Factory.getInstance().newEditable(data?.productQuantity.toString())

        Log.d("name", data?.productTitle.toString())
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
        toolbar = findViewById(R.id.toolbar_product_details_activity)
        tvTitle = findViewById(R.id.tv_title)
        ivProductDetailImage = findViewById(R.id.iv_product_detail_image)
        tvProductDetailTitle = findViewById(R.id.tv_product_details_title)
        etProductDetailPrice = findViewById(R.id.tv_product_details_price)
        etProductDetailDescription = findViewById(R.id.tv_product_details_description)
        etProductDetailQuantity = findViewById(R.id.tv_product_details_available_quantity)
        plusSign = findViewById(R.id.plus_sign)
        minusSign = findViewById(R.id.minus_sign)
        addToCart = findViewById(R.id.btn_add_to_cart)
        goToCart = findViewById(R.id.btn_go_to_cart)

        productDetails = intent.getParcelableExtra<Product>("productDetails")!!
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (intent.getBooleanExtra("editable", false)) {
            menuInflater.inflate(R.menu.edit_product_menu, menu)
        }
        else{
            menuInflater.inflate(R.menu.add_to_cart_product_menu, menu)
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        when(id){
            R.id.edit_product ->{

                tvProductDetailTitle.isEnabled = true
                etProductDetailPrice.isEnabled = true
                etProductDetailDescription.isEnabled = true
                etProductDetailQuantity.isEnabled = true

                Toast.makeText(this@ProductsDetailsActivity,resources.getString(R.string.you_can_now_edit_the_details), Toast.LENGTH_SHORT)
                    .show()
                return true
            }

            R.id.add_to_cart ->{
//                showSnackBar("Will be added", false)
                addToCart()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun addToCart(){
        showProgressDialog(resources.getString(R.string.please_wait))

        val addToCart = CartItem(productDetails.uid, productDetails.userName, productDetails.productId,
            productDetails.productImage, productDetails.productTitle, productDetails.productPrice,
            productDetails.productDescription, productDetails.productQuantity, etProductDetailQuantity.text.toString().toInt())

        FirebaseClass().addCartItems(this, addToCart)
    }

    fun productAddedToCartSuccess() {
        hideProgressDialog()

        Toast.makeText(this@ProductsDetailsActivity, resources.getString(R.string.added_to_cart_success), Toast.LENGTH_SHORT).show()
        finish()
    }

    fun showAlertDialog(currentQuantity: Int, newQuantity: Int?, addToCart: CartItem) {
        hideProgressDialog()

        AlertDialog.Builder(this)
            .setMessage("$currentQuantity items are already there in your cart. You want to add $newQuantity more ?")
            .setTitle("Product already exists in Cart")
            .setIcon(R.drawable.avd_cart)
            .setPositiveButton("Yes") { _, _ ->
                showProgressDialog(resources.getString(R.string.please_wait))
                FirebaseClass().updateCartItems(this, addToCart, currentQuantity, newQuantity)
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    fun productDetailsUpdatedSuccess() {
        hideProgressDialog()

        Toast.makeText(this@ProductsDetailsActivity, "Product details updated successfully", Toast.LENGTH_SHORT)
            .show()

        finish()
    }
}