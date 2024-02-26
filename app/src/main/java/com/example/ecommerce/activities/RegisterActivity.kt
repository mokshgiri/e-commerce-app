package com.example.ecommerce.activities

import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.Toolbar
import com.example.ecommerce.firebase.FirebaseClass
import com.example.ecommerce.R
import com.example.ecommerce.model.User
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : BaseActivity() {

    private lateinit var loginTextView : TextView
    private lateinit var termsConditionsTextView : TextView
    private lateinit var termsConditionsDialog : BottomSheetDialog
    private lateinit var dialogCloseBtn : ImageView
    private lateinit var dialogTermsConditionsText : TextView
    private lateinit var toolbar : Toolbar
    private lateinit var etFirstName : EditText
    private lateinit var etLastName : EditText
    private lateinit var etEmail : EditText
    private lateinit var etPassword : EditText
    private lateinit var etConfirmPassword : EditText
    private lateinit var cbTermsAndConditions : AppCompatCheckBox
    private lateinit var registerBtn : Button
    private lateinit var firebaseAuth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        hideStatusBar()

        initializeViews()
        setUpActionBar()


        loginTextView.setOnClickListener {
            finish()
        }

        registerBtn.setOnClickListener {
            registerUser()
        }

//        val dialogCloseBtn : ImageView = termsConditionsDialog.findViewById(R.id.dialogBtn)!!

        termsConditionsTextView.setOnClickListener {

            termsConditionsDialog.show()
        }

        dialogCloseBtn.setOnClickListener {
           termsConditionsDialog.dismiss()

        }
    }


    private fun setUpActionBar() {
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar

        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_ios_new_24)

        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun initializeViews() {
        loginTextView = findViewById(R.id.tv_login)
        termsConditionsTextView = findViewById(R.id.tv_terms_condition)

        termsConditionsDialog = BottomSheetDialog(this)
        termsConditionsDialog.setContentView(R.layout.terms_conditions_dialog)

        dialogCloseBtn = termsConditionsDialog.findViewById(R.id.dialogBtn)!!
        dialogTermsConditionsText = termsConditionsDialog.findViewById(R.id.terms_conditions_dialog_text)!!

        toolbar = findViewById(R.id.toolbar_register_activity)
        etFirstName = findViewById(R.id.et_first_name)
        etLastName = findViewById(R.id.et_last_name)
        etPassword = findViewById(R.id.et_password)
        etConfirmPassword = findViewById(R.id.et_confirm_password)
        etEmail = findViewById(R.id.et_email)
        cbTermsAndConditions = findViewById(R.id.cb_terms_and_condition)

        registerBtn = findViewById(R.id.btn_register)

        firebaseAuth = FirebaseAuth.getInstance()
    }

    private fun validateRegisterDetails() : Boolean{
        return when {
            TextUtils.isEmpty(etFirstName.text.toString().trim() {it <= ' '}) -> {
                showSnackBar(resources.getString(R.string.err_msg_enter_first_name), true)
                false
            }

            TextUtils.isEmpty(etLastName.text.toString().trim()) -> {
                showSnackBar(resources.getString(R.string.err_msg_enter_last_name), true)
                false
            }

            TextUtils.isEmpty(etEmail.text.toString().trim()) -> {
                showSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                false
            }

            TextUtils.isEmpty(etPassword.text.toString().trim()) -> {
                showSnackBar(resources.getString(R.string.err_msg_enter_password), true)
                false
            }

            TextUtils.isEmpty(etConfirmPassword.text.toString().trim()) -> {
                showSnackBar(resources.getString(R.string.err_msg_enter_confirm_password), true)
                false
            }

            etConfirmPassword.text.toString().trim() != etPassword.text.toString().trim() -> {
                showSnackBar(resources.getString(R.string.password_mismatch), true)
                false
            }

            !cbTermsAndConditions.isChecked -> {
                showSnackBar(resources.getString(R.string.err_msg_enter_terms_conditions_check), true)
                false
            }

            else -> {
                true
            }
        }
    }

    private fun registerUser() {

        // Check with validate function if the entries are valid or not.
        if (validateRegisterDetails()) {

            showProgressDialog("Please wait")
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                if (it.isSuccessful){

                    val firebaseUser = it.result.user
                    val user = User(firebaseUser!!.uid, etFirstName.text.toString(), etLastName.text.toString(),
                        email, null, null,null,0)

                    FirebaseClass().registerUser(this@RegisterActivity, user)

//                    firebaseAuth.signOut()
//                    finish()
                }
                else{
                    hideProgressDialog()
                    showSnackBar(it.exception?.message.toString(), true)
                }
            }
        }
    }

    fun userRegistrationSuccess(){
        hideProgressDialog()

        Toast.makeText(this@RegisterActivity, resources.getString(R.string.register_successful), Toast.LENGTH_SHORT)
            .show()

        FirebaseAuth.getInstance().signOut()
        // Finish the Register Screen
        finish()
    }
}