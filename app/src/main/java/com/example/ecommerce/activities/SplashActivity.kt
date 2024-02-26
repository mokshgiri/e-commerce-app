package com.example.ecommerce.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowInsets
import android.view.WindowManager
import com.example.ecommerce.R
import com.example.ecommerce.utils.Constants

class SplashActivity : BaseActivity() {

    private  lateinit var sharedPreferences: SharedPreferences
    private var isLoggedIn = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        sharedPreferences = getSharedPreferences(Constants.LOGIN_PREFERENCES, MODE_PRIVATE)
        isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

        hideStatusBar()

        Handler().postDelayed(
            {
                if (isLoggedIn){
                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                    finish()
                }
                else {

                    startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                    finish()
                }
            },
            2500
        )
    }

}