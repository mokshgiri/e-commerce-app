package com.example.ecommerce.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Address(
    val uid : String ?= null,
    val name : String ?= null,
    val mobile : Long ?= null,
    val address : String ?= null,
    val zipCode : String ?= null,
    val additionalNote : String ?= null,
    val type : String ?= null,
    val otherDetails : String ?= null,
    val id : String ?= null,
) : Parcelable