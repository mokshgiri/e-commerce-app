package com.example.ecommerce.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import com.example.ecommerce.R
import com.example.ecommerce.firebase.FirebaseClass
import com.example.ecommerce.model.User
import com.example.ecommerce.utils.Constants
import com.example.ecommerce.utils.GlideLoader
import de.hdodenhof.circleimageview.CircleImageView

class SettingsActivity : BaseActivity() {

    private lateinit var toolbar : Toolbar
    private lateinit var btnLogout : Button
    private lateinit var btnEdit : TextView
    private lateinit var tvName : TextView
    private lateinit var tvGender : TextView
    private lateinit var tvEmail : TextView
    private lateinit var tvMobileNumber : TextView
    private lateinit var tvAddress : TextView
    private lateinit var addressBtn : CircleImageView
    private lateinit var ivUserPhoto : ImageView
    private lateinit var sharedPreferences: SharedPreferences
    private var userDetails : User ?= null
//    private lateinit var firebase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        hideStatusBar()
        intializeViews()
        setupActionBar()

//        loadUserData()

        sharedPreferences = getSharedPreferences(Constants.LOGIN_PREFERENCES, MODE_PRIVATE)

        btnEdit.setOnClickListener {
            val intent = Intent(this@SettingsActivity, UserProfileActivity::class.java)
            intent.putExtra(Constants.EXTRA_USER_DETAILS, userDetails)
            startActivity(intent)
        }

        btnLogout.setOnClickListener {
            logOutCurrentUser()
        }

        addressBtn.setOnClickListener {
            val intent = Intent(this@SettingsActivity, AddressListActivity::class.java)
            startActivity(intent)
        }
    }

    private fun logOutCurrentUser() {
        AlertDialog.Builder(this)
            .setMessage("Are you sure you want to log out ?")
            .setTitle("Log Out")
            .setIcon(R.drawable.baseline_logout_24)
            .setPositiveButton("Yes") { _, _ ->
                FirebaseClass().logOutCurrentUser(this)
                sharedPreferences.edit().putBoolean("isLoggedIn", false).apply()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun galleryPickImageEasyWay() {
        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
//                    binding.imageView.setImageURI(uri);
                    Log.d("PhotoPicker", "Selected URI: $uri")
                } else {
                    Log.d("PhotoPicker", "No media selected")
                }
            }
        btnEdit.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }

    private fun loadUserData() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirebaseClass().getUserDetails(this@SettingsActivity)
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

    private fun intializeViews() {
        toolbar = findViewById(R.id.toolbar_settings_activity)
        btnLogout = findViewById(R.id.btn_logout)
        btnEdit = findViewById(R.id.tv_edit)
        tvName = findViewById(R.id.tv_name)
        tvGender = findViewById(R.id.tv_gender)
        tvEmail = findViewById(R.id.tv_email)
        tvMobileNumber = findViewById(R.id.tv_mobile_number)
        tvAddress = findViewById(R.id.tv_address)
        ivUserPhoto = findViewById(R.id.iv_user_photo)
        addressBtn = findViewById(R.id.addressBtn)
    }

    fun logoutSuccess() {
        Toast.makeText(this@SettingsActivity, resources.getString(R.string.logged_out_successful), Toast.LENGTH_SHORT).show()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
    }

    fun userDataFetchSuccess(user: User) {
        hideProgressDialog()

        userDetails = user

        GlideLoader(this).loadImage(user.image, ivUserPhoto)
        tvName.text = "${user.firstName} ${user.lastName}"
        tvEmail.text = user.email
        tvMobileNumber.text = user.mobile.toString()
        tvGender.text = user.gender

//        tvAddress.text = use
    }

    override fun onResume() {
        super.onResume()

        loadUserData()
    }
}