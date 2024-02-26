package com.example.ecommerce.activities

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.ecommerce.R
import com.google.android.material.snackbar.Snackbar

open class BaseActivity : AppCompatActivity() {
    private lateinit var progressDialog : Dialog
    private lateinit var tvProgressText : TextView
    private var doubleBackToExitPressedOnce = false

    fun hideStatusBar(){
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
    }

    fun showProgressDialog(text : String) {
        progressDialog = Dialog(this)
        progressDialog.setContentView(R.layout.dialog_progress)
        progressDialog.setCancelable(false)

        tvProgressText = progressDialog.findViewById(R.id.tv_progress_text)

        tvProgressText.text = text

        progressDialog.show()
    }

    fun hideProgressDialog(){
        progressDialog.dismiss()
    }

    fun showSnackBar(message : String, errorMessage : Boolean){
        val snackBar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
        val snackBarView = snackBar.view

        if (errorMessage){
            snackBarView.setBackgroundColor(ContextCompat.getColor(this@BaseActivity, R.color.colorSnackBarError))
        }
        else{
            snackBarView.background = resources.getDrawable(R.color.colorSnackBarSuccess)
        }
        snackBar.show()

    }

    fun doubleBackToExit() {
        if (doubleBackToExitPressedOnce){
        super.onBackPressed()
        return
    }

        doubleBackToExitPressedOnce = true

        Toast.makeText(this@BaseActivity, resources.getString(R.string.please_click_back_again_to_exit), Toast.LENGTH_SHORT).show()

        Handler().postDelayed({doubleBackToExitPressedOnce = false}, 2000)
    }
}