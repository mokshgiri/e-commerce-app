package com.example.ecommerce.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Orders(
    val uid : String ?= null,
    val items : ArrayList<CartItem> ?= null,
    val address : String ?= null,
    val title : String ?= null,
    val image : String ?= null,
    val subTotalAmount : String ?= null,
    val shippingCharge : String ?= null,
    val totalAmount : String ?= null,
    val id : String ?= null,
) : Parcelable