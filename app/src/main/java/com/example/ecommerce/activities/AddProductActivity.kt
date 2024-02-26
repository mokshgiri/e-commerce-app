package com.example.ecommerce.activities

import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.example.ecommerce.R
import com.example.ecommerce.firebase.FirebaseClass
import com.example.ecommerce.model.Product
import com.example.ecommerce.utils.Constants
import com.example.ecommerce.utils.GlideLoader

class AddProductActivity : BaseActivity() {
    private lateinit var toolbar : androidx.appcompat.widget.Toolbar
    private lateinit var ivProductImage : ImageView
    private lateinit var ivAddUpdateProduct : ImageView
    private lateinit var etProductTitle : EditText
    private lateinit var etProductPrice : EditText
    private lateinit var etProductDescription : EditText
    private lateinit var etProductQuantity : EditText
    private lateinit var btnSubmit : Button
    private lateinit var pickMedia : ActivityResultLauncher<PickVisualMediaRequest>
    private var productImageUri : Uri ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)

        hideStatusBar()
        initializeViews()
        setupActionBar()

        pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    productImageUri = uri
                    ivAddUpdateProduct.setImageDrawable(resources.getDrawable(R.drawable.baseline_edit_24))
                    GlideLoader(this).loadImage(uri, ivProductImage)
                    Log.d("PhotoPicker", "Selected URI: $uri")
                } else {
                    Log.d("PhotoPicker", "No media selected")
                }
            }

        ivAddUpdateProduct.setOnClickListener {
            selectImage()
        }

        btnSubmit.setOnClickListener {
            if (validateProductDetails()){
                showProgressDialog(resources.getString(R.string.please_wait))
//                uploadProductDetails()
                uploadProductImage()
            }
        }
    }

    private fun uploadProductDetails(imageURL: String) {
        val userName = getSharedPreferences(Constants.ECOMM_PREFERENCES, MODE_PRIVATE)
            .getString(Constants.LOGGED_IN_USERNAME, "")

        val uid = FirebaseClass().getCurrentUserId()

        val dbRef = FirebaseClass().databaseProduct.push()
        val productId =  dbRef.key
        val product = Product(uid, userName, productId, imageURL,etProductTitle.text.toString().trim(),
            etProductPrice.text.toString().toInt(), etProductDescription.text.toString().trim(),
            etProductQuantity.text.toString().toInt())

        FirebaseClass().uploadProductDetails(this, product, dbRef)
    }

    private fun uploadProductImage() {
        FirebaseClass().uploadImageToFirebaseStorage(this@AddProductActivity, productImageUri!!, null, Constants.PRODUCT_IMAGE)
    }

    private fun selectImage() {

//        btnEdit.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
//        }
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
        toolbar = findViewById(R.id.toolbar_add_product_activity)
        ivProductImage = findViewById(R.id.iv_product_image)
        ivAddUpdateProduct = findViewById(R.id.iv_add_update_product)
        etProductPrice = findViewById(R.id.et_product_price)
        etProductDescription = findViewById(R.id.et_product_description)
        etProductTitle = findViewById(R.id.et_product_title)
        etProductQuantity = findViewById(R.id.et_product_quantity)
        btnSubmit = findViewById(R.id.btn_submit)
    }

    private fun validateProductDetails() : Boolean{
        return when {
            TextUtils.isEmpty(etProductTitle.text.toString().trim() { it <= ' ' }) -> {
                showSnackBar(resources.getString(R.string.err_msg_enter_product_title), true)
                false
            }

            TextUtils.isEmpty(etProductPrice.text.toString().trim()) -> {
                showSnackBar(resources.getString(R.string.err_msg_enter_product_price), true)
                false
            }

            TextUtils.isEmpty(etProductQuantity.text.toString().trim()) -> {
                showSnackBar(resources.getString(R.string.err_msg_enter_product_quantity), true)
                false
            }

            TextUtils.isEmpty(etProductDescription.text.toString().trim()) -> {
                showSnackBar(resources.getString(R.string.err_msg_enter_product_description), true)
                false
            }

            productImageUri == null -> {
                showSnackBar(resources.getString(R.string.err_msg_select_product_image), true)
                false
            }

            else -> {
                Log.d("productImageUri", productImageUri.toString())
//                showSnackBar(resources.getString(R.string.login_successful), false)
                true
            }
        }
    }
    fun imageUploadSuccess(imageURL: String, profileCompleted: Int?){
//        hideProgressDialog()
//
//        showSnackBar("Image uploaded successfully", false)
        uploadProductDetails(imageURL)
  }

    fun productDetailsUploadedSuccessfully() {
        hideProgressDialog()

        Toast.makeText(this@AddProductActivity, resources.getString(R.string.product_uploaded_success_message), Toast.LENGTH_SHORT)
            .show()

        finish()
    }
}