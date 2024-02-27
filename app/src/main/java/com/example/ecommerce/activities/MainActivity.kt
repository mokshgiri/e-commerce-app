package com.example.ecommerce.activities

import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.example.ecommerce.R
import com.example.ecommerce.firebase.FirebaseClass
import com.example.ecommerce.fragments.OrdersFragment
import com.example.ecommerce.fragments.DashboardFragment
import com.example.ecommerce.fragments.ProductsFragment
import com.example.ecommerce.utils.Constants
import com.google.firebase.database.DatabaseReference
import com.google.firebase.messaging.FirebaseMessaging
import np.com.susanthapa.curved_bottom_navigation.CbnMenuItem
import np.com.susanthapa.curved_bottom_navigation.CurvedBottomNavigationView

class MainActivity : BaseActivity() {

    private lateinit var navView : CurvedBottomNavigationView
    private lateinit var frameLayout: FrameLayout
    private lateinit var actionBar : ActionBar
    private lateinit var customFont: Typeface
    private lateinit var toolbar: Toolbar
    private lateinit var toolbarTitle: TextView
    private lateinit var dbRef: DatabaseReference
    private lateinit var tokenId: String
    private lateinit var token: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        hideStatusBar()

        initializeViews()
        setUpActionbar()

        getFcmToken()

//        // Initialize the custom font
//        customFont = Typeface.createFromAsset(assets, "font/custom_font.ttf")

        loadFragment(DashboardFragment(), "DASHBOARD")

        navView = findViewById(R.id.nav_view)
//        navView.fabBackgroundColor. = R.drawable.app_gradient_color_background

        val menuItems = arrayOf(
//            CbnMenuItem(
//                R.drawable.baseline_home_24, // the icon
//                R.drawable.avd_home // the AVD that will be shown in FAB
//            ),
            CbnMenuItem(
                R.drawable.baseline_dashboard_24,
                R.drawable.avd_dashboard
            ),
            CbnMenuItem(
                R.drawable.baseline_product_basket_24,
                R.drawable.avd_product
            ),
            CbnMenuItem(
                R.drawable.baseline_shopping_bag_24,
                R.drawable.avd_shop
            )

        )
        navView.setMenuItems(menuItems, 0)

        navView.setOnMenuItemClickListener { _, i ->
            when (i) {
//                0 -> {
//                    loadFragment(HomeFragment(), "HOME")
//                }
                0 -> {
                    loadFragment(DashboardFragment(), "DASHBOARD")
                }
                1 -> {
                    loadFragment(ProductsFragment(), "PRODUCTS")
                }
                else -> {
                    loadFragment(OrdersFragment(), "ORDERS")
                }
            }
        }
    }

    private fun getFcmToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if (it.isSuccessful){
                token = it.result

                val userHashMap = HashMap<String, Any>()
                userHashMap[Constants.FCM_TOKEN] = token

                FirebaseClass().updateUserDetails(this@MainActivity, userHashMap, 1)
                FirebaseClass().getAllTokens(this@MainActivity, token)


                Log.d("fcmToken", token.toString())
            }
        }
    }

    private fun loadFragment(fragment: Fragment, name: String) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, fragment)
            .commit()

        toolbarTitle.text = name

//        actionBar = supportActionBar!!
//        actionBar.title = name
//        actionBar.title

    }

    private fun setUpActionbar() {
        setSupportActionBar(toolbar)

        val actionbar = supportActionBar
        actionbar?.setBackgroundDrawable(resources.getDrawable(R.drawable.app_gradient_color_background))
//        actionbar?.setDisplayHomeAsUpEnabled(true)
//        actionbar?.setHomeAsUpIndicator(R.drawable.ic_back_icon)
//        actionbar?.title = "Back"

        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun initializeViews(){
        frameLayout = findViewById(R.id.frameLayout)
        toolbar = findViewById(R.id.toolbar)
        toolbarTitle = findViewById(R.id.tv_title)
    }

    override fun onBackPressed() {
        doubleBackToExit()
    }

    fun tokenAddedSuccess() {
        Toast.makeText(this@MainActivity, "Token Added Successfully", Toast.LENGTH_SHORT).show()

    }

    fun getTokenList(tokenList: ArrayList<String>, token: String) {

        Log.d("tokenList1", tokenList.toString())
        Log.d("countListiTems", tokenList.count().toString())


        dbRef = FirebaseClass().databaseFcmToken.push()
        tokenId = dbRef.key!!

        if (tokenList.isEmpty() || !tokenList.contains(token)){
            FirebaseClass().storeFcmTokensToFirebase(this@MainActivity, dbRef,tokenId, token)
        }


        for (tokenItem in tokenList){
            Log.d("tokenItems", tokenItem)


        }


    }

}