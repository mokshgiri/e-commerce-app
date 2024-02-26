package com.example.ecommerce.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    val uid : String ?= null,
    val firstName : String ?= null,
    val lastName : String ?= null,
    val email : String ?= null,
    val image : String ?= null,
    val mobile : Long ?= null,
    val gender : String ?= null,
    val profileCompleted : Int = 0,
    val cartItems : HashMap<String, CartItem> ?= null,
    val addresses : HashMap<String, Address> ?= null
) : Parcelable