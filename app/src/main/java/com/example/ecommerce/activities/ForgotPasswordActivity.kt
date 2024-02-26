package com.example.ecommerce.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.example.ecommerce.R
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : BaseActivity() {
    private lateinit var toolbar : Toolbar
    private lateinit var tvRegister : TextView
    private lateinit var etEmail : EditText
    private lateinit var btnSubmit : Button
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        hideStatusBar()

        initializeViews()
        setupToolbar()
        initializeFirebase()

        tvRegister.setOnClickListener {
            val intent = Intent(this@ForgotPasswordActivity, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnSubmit.setOnClickListener {
            val email = etEmail.text.toString().trim()

            if (email.isNotEmpty()){
                showProgressDialog("Please wait")

                firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener {
                    if (it.isSuccessful){
                        hideProgressDialog()
                        Toast.makeText(this@ForgotPasswordActivity, resources.getText(R.string.password_recovery_text),
                            Toast.LENGTH_SHORT).show()

                        finish()
                    }
                    else{
                        hideProgressDialog()
                        showSnackBar(it.exception?.message.toString(), true)
                    }
                }
            }
            else{
                showSnackBar(resources.getString(R.string.err_msg_enter_email), true)
            }
        }
    }

    private fun initializeFirebase() {
        firebaseAuth = FirebaseAuth.getInstance()
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)

        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_ios_new_24)

        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun initializeViews() {
        toolbar = findViewById(R.id.toolbar_forgot_password_activity)
        tvRegister = findViewById(R.id.tv_register)
        etEmail = findViewById(R.id.et_email)
        btnSubmit = findViewById(R.id.btn_submit)
    }
}