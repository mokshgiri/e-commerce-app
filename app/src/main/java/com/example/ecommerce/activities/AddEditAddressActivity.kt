package com.example.ecommerce.activities

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.example.ecommerce.R
import com.example.ecommerce.firebase.FirebaseClass
import com.example.ecommerce.model.Address
import com.example.ecommerce.utils.Constants

class AddEditAddressActivity : BaseActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var etFullName: EditText
    private lateinit var etPhoneNo: EditText
    private lateinit var etAddress: EditText
    private lateinit var etZipCode: EditText
    private lateinit var etAdditionalNote: EditText
    private lateinit var etOtherDetails: EditText
    private lateinit var rbHome: RadioButton
    private lateinit var rbOffice: RadioButton
    private lateinit var rbOther: RadioButton
    private lateinit var submitBtn: Button
    private var mAddressDetails : Address ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_address)

        hideStatusBar()

        initializeViews()
        setupActionBar()

        mAddressDetails = intent.getParcelableExtra(Constants.EXTRA_ADDRESS_DETAILS)
        if (mAddressDetails != null){
            etFullName.text = Editable.Factory.getInstance().newEditable(mAddressDetails!!.name)
            etPhoneNo.text = Editable.Factory.getInstance().newEditable(mAddressDetails!!.mobile.toString())
            etAddress.text = Editable.Factory.getInstance().newEditable(mAddressDetails!!.address)
            etZipCode.text = Editable.Factory.getInstance().newEditable(mAddressDetails!!.zipCode)
            etAdditionalNote.text = Editable.Factory.getInstance().newEditable(mAddressDetails!!.additionalNote)
            etOtherDetails.text = Editable.Factory.getInstance().newEditable(mAddressDetails!!.otherDetails)

            when(mAddressDetails!!.type){
                Constants.HOME ->{
                    rbHome.isChecked = true
                }
                Constants.OFFICE ->{
                    rbOffice.isChecked = true
                }
                else ->{
                    rbOther.isChecked = true
                    etOtherDetails.visibility = View.VISIBLE
                }
            }
        }

        etOtherDetails.visibility = View.GONE

        // Move the visibility check inside the radio button listener
        rbOther.setOnCheckedChangeListener { _, isChecked ->

            if (isChecked) {
                etOtherDetails.visibility = View.VISIBLE
            }
            else{
                etOtherDetails.visibility = View.GONE
            }
        }

        submitBtn.setOnClickListener {
            if (validateAddressDetails()) {
                showProgressDialog(resources.getString(R.string.please_wait))

                val fullName = etFullName.text.toString()
                val phone = etPhoneNo.text.toString().toLong()
                val etAddress = etAddress.text.toString()
                val zipCode = etZipCode.text.toString()
                val additionalNote = etAdditionalNote.text.toString()
                val otherDetails = etOtherDetails.text.toString()

                val addressType = when {
                    rbHome.isChecked -> {
                        Constants.HOME
                    }

                    rbOffice.isChecked -> {
                        Constants.OFFICE
                    }

                    else -> {
                        Constants.OTHER
                    }
                }

                val dbRef = FirebaseClass().databaseUser.child(FirebaseClass().getCurrentUserId())
                    .child(Constants.ADDRESSES).push()

                val addressId =  dbRef.key

                val address = Address(
                    FirebaseClass().getCurrentUserId(),
                    fullName.replace('/', '-'),
                    phone,
                    etAddress.replace('/', '-'),
                    zipCode.replace('/', '-'),
                    additionalNote.replace('/', '-'),
                    addressType.replace('/', '-'),
                    otherDetails.replace('/', '-'),
                    addressId
                )

                if (mAddressDetails == null) {

                    FirebaseClass().addAddress(this, address, dbRef)
                }
                else{
                    val addressHashMap =    HashMap<String, Any>()

                    if (fullName != mAddressDetails!!.name){
                        addressHashMap[Constants.FULL_NAME] = fullName
                    }
                    if (phone != mAddressDetails!!.mobile){
                        addressHashMap[Constants.MOBILE] = phone
                    }
                    if (etAddress != mAddressDetails!!.address){
                        addressHashMap[Constants.ADDRESS] = etAddress
                    }
                    if (zipCode != mAddressDetails!!.zipCode){
                        addressHashMap[Constants.ZIP_CODE] = zipCode
                    }
                    if (additionalNote != mAddressDetails!!.additionalNote){
                        addressHashMap[Constants.ADDITIONAL_NOTE] = additionalNote
                    }
                    if (addressType != mAddressDetails!!.type){
                        addressHashMap[Constants.ADDRESS_TYPE] = addressType
                    }

                    FirebaseClass().updateAddress(this, addressHashMap, mAddressDetails!!.id)
//                    Toast.makeText(this@AddEditAddressActivity, "Address will be updated soon", Toast.LENGTH_SHORT).show()
                }
            }
        }
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
        toolbar = findViewById(R.id.toolbar_add_edit_address_activity)
        etFullName = findViewById(R.id.et_full_name)
        etPhoneNo = findViewById(R.id.et_phone_number)
        etAddress = findViewById(R.id.et_address)
        etZipCode = findViewById(R.id.et_zip_code)
        etAdditionalNote = findViewById(R.id.et_additional_note)
        rbHome = findViewById(R.id.rb_home)
        rbOffice = findViewById(R.id.rb_office)
        rbOther = findViewById(R.id.rb_other)
        submitBtn = findViewById(R.id.btn_submit_address)
        etOtherDetails = findViewById(R.id.et_other_details)
    }

    private fun validateAddressDetails(): Boolean {
        return when {
            TextUtils.isEmpty(etFullName.text.toString().trim() { it <= ' ' }) -> {
                showSnackBar(resources.getString(R.string.err_msg_enter_full_name), true)
                false
            }

            TextUtils.isEmpty(etAddress.text.toString().trim()) -> {
                showSnackBar(resources.getString(R.string.err_msg_enter_address), true)
                false
            }

            TextUtils.isEmpty(etAdditionalNote.text.toString().trim()) -> {
                showSnackBar(resources.getString(R.string.err_msg_enter_additional_note), true)
                false
            }

            TextUtils.isEmpty(etPhoneNo.text.toString().trim()) -> {
                showSnackBar(resources.getString(R.string.err_msg_enter_phone_no), true)
                false
            }

            TextUtils.isEmpty(etZipCode.text.toString().trim()) -> {
                showSnackBar(resources.getString(R.string.err_msg_enter_zip_code), true)
                false
            }

//            TextUtils.isEmpty(etOtherDetails.text.toString().trim()) -> {
//                if (rbOther.isChecked) {
//                    showSnackBar(resources.getString(R.string.err_msg_enter_other_details), true)
//                }
//                else{
//
//                }
//                    false
//
//            }

            else -> {
                true
            }
        }
    }

    fun addAddressSuccess() {
        hideProgressDialog()

        val notifySuccessMessage = if (mAddressDetails != null){
            resources.getString(R.string.address_updated_success)
        }
        else{
            resources.getString(R.string.address_added_success)
        }
        Toast.makeText(
            this@AddEditAddressActivity,
            notifySuccessMessage,
            Toast.LENGTH_SHORT
        ).show()
        finish()

    }

    fun addAddressFailure() {
        hideProgressDialog()

        Toast.makeText(
            this@AddEditAddressActivity,
            resources.getString(R.string.address_added_failed),
            Toast.LENGTH_SHORT
        ).show()
        finish()
    }
}