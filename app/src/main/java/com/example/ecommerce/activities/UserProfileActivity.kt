package com.example.ecommerce.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.ecommerce.utils.Constants

import com.example.ecommerce.R
import com.example.ecommerce.firebase.FirebaseClass
import com.example.ecommerce.model.User
import com.example.ecommerce.utils.GlideLoader
import java.io.IOException

class UserProfileActivity : BaseActivity() {

    private lateinit var toolbar : Toolbar
    private lateinit var etFirstName : EditText
    private lateinit var etLastName : EditText
    private lateinit var etEmail : EditText
    private lateinit var etMobieNo : EditText
    private lateinit var rbMale : RadioButton
    private lateinit var rbFemale : RadioButton
    private lateinit var btnSubmit : Button
    private lateinit var ivUserPhoto : ImageView
    private lateinit var tvTitle : TextView
    private lateinit var userDetails : User
    private var uri : Uri?= null
    private var userProfileImageUrl : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        hideStatusBar()
        initializeViews()

        userDetails = User()

        if (intent.hasExtra(Constants.EXTRA_USER_DETAILS)){
            userDetails = intent.getParcelableExtra(Constants.EXTRA_USER_DETAILS)!!
        }

        etFirstName.setText(userDetails.firstName)
        etLastName.setText(userDetails.lastName)
        etEmail.setText(userDetails.email)
        etEmail.isEnabled = false

        if (userDetails.profileCompleted == 0){
            tvTitle.text = resources.getString(R.string.complete_profile_title)
            tvTitle.setPadding(0,0,0,0)

            etFirstName.isEnabled = false
            etLastName.isEnabled = false

            btnSubmit.setOnClickListener {
                if (validateUserProfileDetails()){

                    showProgressDialog(resources.getString(R.string.please_wait)
                    )
                    if (uri != null) {
                        FirebaseClass().uploadImageToFirebaseStorage(this, uri!!, 0, Constants.USER_PROFILE_IMAGE)
                    }
                    else{
                        updateUserProfileDetails(0)
                    }

                }
            }

        }
        else{
            setupActionBar()

            tvTitle.text = resources.getString(R.string.edit_profile)

            if (userDetails.mobile != null){
                etMobieNo.setText(userDetails.mobile.toString())
            }

            if (userDetails.gender == Constants.FEMALE){
                rbFemale.isChecked = true
            }
            else{
                rbMale.isChecked = true
            }

            GlideLoader(this).loadImage(userDetails.image, ivUserPhoto)

            btnSubmit.setOnClickListener {
                if (validateUserProfileDetails()){

                    showProgressDialog(resources.getString(R.string.please_wait)
                    )
                    if (uri != null) {
                        FirebaseClass().uploadImageToFirebaseStorage(this, uri!!, 1, Constants.USER_PROFILE_IMAGE)
                    }
                    else{
                        updateUserProfileDetails(1)
                    }

                }
            }
        }


        ivUserPhoto.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this@UserProfileActivity, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
//                showSnackBar(resources.getString(R.string.already_have_storage_permission), false)
                Constants.showImageChooser(this@UserProfileActivity)
            }
            else{
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), Constants.READ_STORAGE_PERMISSION_CODE)
            }
        }


    }

    private fun updateUserProfileDetails(profileCompleted: Int) {

        val userHashMap = HashMap<String, Any>()

        val firstName = etFirstName.text.toString().trim()
        val lastName = etLastName.text.toString().trim()

        if (firstName != userDetails.firstName){
            userHashMap[Constants.FIRST_NAME] = firstName
        }

        if (lastName != userDetails.lastName){
            userHashMap[Constants.LAST_NAME] = lastName
        }

        val gender = if (rbMale.isChecked){
            Constants.MALE
        }
        else{
            Constants.FEMALE
        }

        val mobileNumber = etMobieNo.text.toString().trim()

        if (mobileNumber.isNotEmpty() && mobileNumber != userDetails.mobile.toString()){
            userHashMap[Constants.MOBILE] = mobileNumber.toLong()
        }

        if (gender.isNotEmpty() && gender != userDetails.gender) {
            userHashMap[Constants.GENDER] = gender
        }

        if (userProfileImageUrl.isNotEmpty()){
            userHashMap[Constants.USER_IMAGE] = userProfileImageUrl
        }

        userHashMap[Constants.COMPLETE_PROFILE] = 1

        FirebaseClass().updateUserDetails(this, userHashMap, profileCompleted)

    }

    private fun setupActionBar() {
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar

        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_ios_new_24)

        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun initializeViews() {
        toolbar = findViewById(R.id.toolbar_user_profile_activity)
        etEmail = findViewById(R.id.et_email)
        etFirstName = findViewById(R.id.et_first_name)
        etMobieNo = findViewById(R.id.et_mobile_number)
        etLastName = findViewById(R.id.et_last_name)
        btnSubmit = findViewById(R.id.btn_submit)
        rbMale = findViewById(R.id.rb_male)
        rbFemale = findViewById(R.id.rb_female)
        ivUserPhoto = findViewById(R.id.iv_user_photo)
        tvTitle = findViewById(R.id.tv_title)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
//                showSnackBar(resources.getString(R.string.storage_permission_granted), false)
                Constants.showImageChooser(this@UserProfileActivity)
            }
            else{
                showSnackBar(resources.getString(R.string.storage_permission_denied), true)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK){
            if (requestCode == Constants.PICK_IMAGE_REQUEST_CODE){
                if (data != null){
                    try{
                        uri = data.data!!

//                        ivUserPhoto.setImageURI(selectedImageFileUri)
                        GlideLoader(this).loadImage(uri!!, ivUserPhoto)
                    }
                    catch (e: IOException){
                        showSnackBar(resources.getString(R.string.image_Selection_failed), false)
                    }
                }
            }
        }
    }

    fun validateUserProfileDetails() : Boolean{
        return when{
            TextUtils.isEmpty(etMobieNo.text.toString().trim()) ->{
                showSnackBar(resources.getString(R.string.err_msg_enter_mobile), true)
                false
            }
            else ->{
                true
            }
        }
    }

    fun userProfileUpdateSuccess(profileCompleted: Int) {
        hideProgressDialog()


        Toast.makeText(
            this@UserProfileActivity,
            resources.getString(R.string.profile_update_successful),
            Toast.LENGTH_SHORT
        )
            .show()

        if (profileCompleted == 0) {
            val intent = Intent(this@UserProfileActivity, MainActivity::class.java)
            startActivity(intent)
        }
        finish()
    }

    fun imageUploadSuccess(imageURL: String, profileCompleted: Int){
//        hideProgressDialog()

        userProfileImageUrl = imageURL
        updateUserProfileDetails(profileCompleted)

//        Toast.makeText(this@UserProfileActivity, "Image has been uploaded successfully. Image URL is $imageURL", Toast.LENGTH_SHORT).show()
    }
}