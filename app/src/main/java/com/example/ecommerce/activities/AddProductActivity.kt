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
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class AddProductActivity : BaseActivity() {
    private lateinit var toolbar : androidx.appcompat.widget.Toolbar
    private lateinit var ivProductImage : ImageView
    private lateinit var ivAddUpdateProduct : ImageView
    private lateinit var etProductTitle : EditText
    private lateinit var etProductPrice : EditText
    private lateinit var etProductDescription : EditText
    private lateinit var etProductQuantity : EditText
    private lateinit var btnSubmit : Button
    private lateinit var jsonObject: JSONObject
    private lateinit var notificationObj: JSONObject
    private lateinit var dataObj: JSONObject
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

    fun productDetailsUploadedSuccessfully(product: Product) {
        hideProgressDialog()

        Toast.makeText(this@AddProductActivity, resources.getString(R.string.product_uploaded_success_message), Toast.LENGTH_SHORT)
            .show()

        sendNotification(product)
//        FirebaseClass().getAllTokens()
        finish()
    }

    private fun sendNotification(product: Product) {
        val productTitle = product.productTitle
        val userName = product.userName
        val userId = product.uid

        val body = resources.getString(R.string.checkout_this_new_project)

        try{
            notificationObj = JSONObject()

            notificationObj.put("title", productTitle)
            notificationObj.put("body", body)

            dataObj = JSONObject()
            dataObj.put("userId", userId)

            FirebaseClass().getAllTokens(this@AddProductActivity, null)

        }
        catch (exception : Exception){

        }
    }


    fun getTokenList(tokenList: ArrayList<String>) {
        for (token in tokenList){
            Log.d("tokenForProduct", token)

            jsonObject = JSONObject()

            jsonObject.put("notification",notificationObj)
            jsonObject.put("data",dataObj)
            jsonObject.put("to",token)
            jsonObject.put("priority", "high")

            callApi(jsonObject)
        }
    }


    private fun callApi(jsonObject : JSONObject){
        val JSON = "application/json; charset=utf-8".toMediaType()

//        val JSON = MediaType.get("application/json; charset=utf-8")
        val client = OkHttpClient()

        val url = "https://fcm.googleapis.com/fcm/send"

        val body = RequestBody.create(JSON, jsonObject.toString())

        val request = Request.Builder().url(url).post(body).header("Authorization", "Bearer ${Constants.FCM_SERVER_KEY}")
            .build()

        client.newCall(request).enqueue(object : Callback{
            override fun onFailure(call: Call, e: IOException) {
                Log.d("error", e.message.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                Log.d("Respond", "Success")

                Log.d("reeponseText", response.message.toString())
            }

        })
    }
}