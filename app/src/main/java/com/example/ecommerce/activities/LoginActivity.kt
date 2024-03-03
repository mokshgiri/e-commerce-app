package com.example.ecommerce.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputType
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import com.example.ecommerce.utils.Constants
import com.example.ecommerce.firebase.FirebaseClass
import com.example.ecommerce.R
import com.example.ecommerce.model.User
import com.example.ecommerce.retrofit.ApiService.ApiService
import com.example.ecommerce.retrofit.MyData
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

class LoginActivity : BaseActivity() {

    private lateinit var registerTextView : TextView
    private lateinit var btnLogin : Button
    private lateinit var tvForgotPass : TextView
    private lateinit var etEmail : EditText
    private lateinit var etPassword : EditText
    private lateinit var passwordLayout : TextInputLayout
    private lateinit var firebaseAuth : FirebaseAuth
    private lateinit var userDetails : User
    private lateinit var sharedPreferences : SharedPreferences
    private var isLoggedIn : Boolean = false
    private var passwordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        hideStatusBar()
        initializeViews()
        initializeFirebase()

        passwordToggleSetup()

        getDataFromApi()

        sharedPreferences = getSharedPreferences(Constants.LOGIN_PREFERENCES, MODE_PRIVATE)
        isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

        if (isLoggedIn){
            navigateToAnotherActivity()
        }

        registerTextView.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }

        btnLogin.setOnClickListener {
            logInRegisteredUser()
        }

        tvForgotPass.setOnClickListener {
            val intent = Intent(this@LoginActivity, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }
    }
    
    private fun passwordToggleSetup() {
        passwordLayout.setEndIconOnClickListener {
            if (passwordVisible) {
                // Hide the password
                etPassword.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

                // Reset the font to apply custom font
                etPassword.typeface = ResourcesCompat.getFont(this,R.font.sf_pro_text_medium)

                passwordLayout.setEndIconDrawable(R.drawable.password_visibilty_on)
            } else {
                // Show the password
                etPassword.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD

                // Reset the font to apply custom font
                etPassword.typeface = ResourcesCompat.getFont(this,R.font.sf_pro_text_medium)

                passwordLayout.setEndIconDrawable(R.drawable.password_visibilty_off)
            }
//            // Move the cursor to the end of the text
            etPassword.setSelection(etPassword.getText()?.length?:0)

            // Toggle the password visibility flag
            passwordVisible = !passwordVisible
        }

    }

    private fun logInRegisteredUser() {
        if (validateLoginDetails()) {
            showProgressDialog("Please wait")
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                if (it.isSuccessful) {
                    savePreferences()
                } else {
                    hideProgressDialog()
                    showSnackBar(it.exception?.message.toString(), true)
                }
            }
        }
    }

    private fun savePreferences() {
        sharedPreferences.edit().putBoolean("isLoggedIn", true).apply()
        FirebaseClass().getUserDetails(this@LoginActivity)
    }

    private fun initializeFirebase() {
        firebaseAuth = FirebaseAuth.getInstance()
    }

    private fun validateLoginDetails() : Boolean{
        return when {
            TextUtils.isEmpty(etEmail.text.toString().trim() { it <= ' ' }) -> {
                showSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                false
            }

            TextUtils.isEmpty(etPassword.text.toString().trim()) -> {
                showSnackBar(resources.getString(R.string.err_msg_enter_password), true)
                false
            }

            else -> {
//                showSnackBar(resources.getString(R.string.login_successful), false)
                true
            }
        }
    }

    private fun initializeViews() {
        registerTextView = findViewById(R.id.tv_register)
        btnLogin = findViewById(R.id.btn_login)
        etEmail = findViewById(R.id.et_email)
        etPassword = findViewById(R.id.et_password)
        tvForgotPass = findViewById(R.id.tv_forgot_password)

        passwordLayout = findViewById(R.id.til_password)
    }

    fun userLoggedInSuccess(user : User) {
        hideProgressDialog()

        userDetails = user
//
        Toast.makeText(this, R.string.login_successful, Toast.LENGTH_SHORT).show()
//        showSnackBar(resources.getString(R.string.login_successful), false)

//        Log.d("firstName", user.firstName.toString())
//        Log.d("lastName", user.lastName.toString())
//        Log.d("email", user.email.toString())
//        Log.d("uid", user.uid.toString())

        navigateToAnotherActivity()
    }

    private fun navigateToAnotherActivity() {
        if (userDetails.profileCompleted == 0) {
            val intent = Intent(this@LoginActivity, UserProfileActivity::class.java)
            intent.putExtra(Constants.EXTRA_USER_DETAILS, userDetails)
            startActivity(intent)
            finish()
        } else {
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
            finish()

            Log.d("triggered", "triggered")
        }
    }

    fun getDataFromApi(){

        val retrofitBuilder = Retrofit.Builder().baseUrl("https://dummyjson.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)

        val apiData = retrofitBuilder.getAllData()

        apiData.enqueue(object  : Callback<MyData>{
            override fun onResponse(call: Call<MyData>, response: Response<MyData>) {
                val data = response.body()

                Log.d("dataLog", data?.users.toString())

                val userList = data?.users

                val dataLogAt2 = userList!![1]
                val address = dataLogAt2.address

                Log.d("dataLogAddressAt2", address.city)
            }

            override fun onFailure(call: Call<MyData>, t: Throwable) {
                Log.d("failedMsg", t.message.toString())
            }

        })
    }
}