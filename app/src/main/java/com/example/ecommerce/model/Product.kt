package com.example.ecommerce.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Product(
    val uid : String ?= null,
    val userName : String ?= null,
    val productId : String ?= null,
    val productImage : String ?= null,
    val productTitle : String ?= null,
    val productPrice : Int ?= null,
    val productDescription : String ?= null,
    val productQuantity : Int ?= null
) : Parcelable