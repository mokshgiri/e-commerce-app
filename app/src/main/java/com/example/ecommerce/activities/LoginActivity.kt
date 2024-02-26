package com.example.ecommerce.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.ecommerce.utils.Constants
import com.example.ecommerce.firebase.FirebaseClass
import com.example.ecommerce.R
import com.example.ecommerce.model.User
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : BaseActivity() {

    private lateinit var registerTextView : TextView
    private lateinit var btnLogin : Button
    private lateinit var tvForgotPass : TextView
    private lateinit var etEmail : EditText
    private lateinit var etPassword : EditText
    private lateinit var firebaseAuth : FirebaseAuth
    private lateinit var sharedPreferences : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        hideStatusBar()
        initializeViews()
        initializeFirebase()

        sharedPreferences = getSharedPreferences(Constants.LOGIN_PREFERENCES, MODE_PRIVATE)


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
//        sharedPreferences.edit().putBoolean("isLoggedIn", true).apply()
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
    }

    fun userLoggedInSuccess(user : User) {
        hideProgressDialog()
//
        Toast.makeText(this, R.string.login_successful, Toast.LENGTH_SHORT).show()
//        showSnackBar(resources.getString(R.string.login_successful), false)

//        Log.d("firstName", user.firstName.toString())
//        Log.d("lastName", user.lastName.toString())
//        Log.d("email", user.email.toString())
//        Log.d("uid", user.uid.toString())

        if (user.profileCompleted == 0) {
            val intent = Intent(this@LoginActivity, UserProfileActivity::class.java)
            intent.putExtra(Constants.EXTRA_USER_DETAILS, user)
            startActivity(intent)
            finish()
        }else{
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
            finish()

            Log.d("triggered", "triggered")
        }

    }
}