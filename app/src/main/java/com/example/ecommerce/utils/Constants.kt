package com.example.ecommerce.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap

object Constants {

    const val USERS : String = "users"
    const val ECOMM_PREFERENCES : String = "EcommercePreferences"
    const val LOGIN_PREFERENCES : String = "LoginPreferences"
    const val LOGGED_IN_USERNAME : String = "logged_in_username"
    const val EXTRA_USER_DETAILS : String = "extra_user_Details"
    const val READ_STORAGE_PERMISSION_CODE = 2
    const val PICK_IMAGE_REQUEST_CODE = 1
    const val FIRST_NAME = "firstName"
    const val LAST_NAME = "lastName"
    const val MALE = "male"
    const val FEMALE = "female"
    const val MOBILE = "mobile"
    const val GENDER = "gender"
    const val USER_IMAGE = "image"
    const val USER_PROFILE_IMAGE = "User_Profile_Image"
    const val COMPLETE_PROFILE = "profileCompleted"
    const val PRODUCT_IMAGE = "Product_Image"
    const val PRODUCTS = "products"
    const val CART_ITEMS = "cartItems"
    const val PRODUCT_QUANTITY = "productQuantity"
    const val CART_QUANTITY = "cartQuantity"
    const val PRODUCT_PRICE = "productPrice"
    const val PRODUCT_DESCRIPTION = "productDescription"
    const val DEFAULT_CART_QUANTITY = 1
    const val HOME = "Home"
    const val OFFICE = "Office"
    const val OTHER = "Other"
    const val ADDRESS = "address"
    const val ZIP_CODE = "zipCode"
    const val ADDITIONAL_NOTE = "additionalNote"
    const val ADDRESS_TYPE = "type"
    const val ADDRESSES = "addresses"
    const val FULL_NAME = "name"
    const val EXTRA_ADDRESS_DETAILS = "AddressDetails"
    const val EXTRA_CART_ACTIVITY = "extra_cart_activity"
    const val EXTRA_SELECTED_ADDRESS = "extra_selected_address"
    const val ALL_CART_LIST = "all_products_list"
    const val FCM_TOKEN = "fcm_token"

    fun showImageChooser(activity: Activity){
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        activity.startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }

    fun getFileExtension(activity: Activity, uri: Uri?) : String?{
        return  MimeTypeMap.getSingleton().getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }

}