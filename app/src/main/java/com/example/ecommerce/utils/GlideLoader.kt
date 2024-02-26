package com.example.ecommerce.utils

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.ecommerce.R
import java.io.IOException

class GlideLoader(val context : Context) {
    fun loadImage(image : Any?, imageView: ImageView){
        try{
            Glide.with(context).load(image).centerCrop().placeholder(R.drawable.ic_user_placeholder__1_).into(imageView)
        }
        catch (e : IOException){
            e.printStackTrace()
        }
    }

}