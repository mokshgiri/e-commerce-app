package com.example.ecommerce.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommerce.R
import com.example.ecommerce.firebase.FirebaseClass
import com.example.ecommerce.model.CartItem
import com.example.ecommerce.utils.Constants
import com.example.ecommerce.utils.GlideLoader

/**
 * A adapter class for dashboard items list.
 */
open class CartListAdapter(
    private val context: Context,
    private var list: ArrayList<CartItem>,
    private val updateCartItems: Boolean
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_cart_layout,
                parent,
                false
            )
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is MyViewHolder) {

            GlideLoader(context).loadImage(model.productImage, holder.ivCartItemImage)

            holder.tvCartItemTitle.text = model.productTitle
            holder.tvCartItemPrice.text = "Rs. ${model.productPrice}"
            holder.tvCartQuantity.text = model.cartQuantity.toString()

            if (updateCartItems) {
                val currentQuantity = model.cartQuantity.toString().toInt()

                if (currentQuantity == model.productQuantity) {
                    holder.ibAddCartItem.visibility = View.INVISIBLE
                }

                if (currentQuantity == 1) {
                    holder.ibRemoveCartItem.visibility = View.INVISIBLE
                }

                holder.ibAddCartItem.setOnClickListener {

                    if (currentQuantity < model.productQuantity!!) {

                        val cartProductHashMap = HashMap<String, Any>()
                        cartProductHashMap[Constants.CART_QUANTITY] = currentQuantity + 1

                        FirebaseClass().updateCartProductQuantityAndPrice(
                            context as Activity,
                            cartProductHashMap,
                            model.productId
                        )
                    }
                }

                holder.ibRemoveCartItem.setOnClickListener {

                    if (currentQuantity > 1) {

                        val cartProductHashMap = HashMap<String, Any>()
                        cartProductHashMap[Constants.CART_QUANTITY] = currentQuantity - 1

                        FirebaseClass().updateCartProductQuantityAndPrice(
                            context as Activity,
                            cartProductHashMap,
                            model.productId
                        )

                    } else {
                        holder.ibRemoveCartItem.visibility = View.GONE
                    }
                }

                holder.ibDeleteCartItem.setOnClickListener {

                    FirebaseClass().deleteCartItemFromFirebase(
                        context as Activity,
                        model.productId,
                        position
                    )
                }
            }
            else{
                holder.ibAddCartItem.visibility = View.GONE
                holder.ibRemoveCartItem.visibility = View.GONE
                holder.ibDeleteCartItem.visibility = View.GONE
                holder.tvTotalQuantity.visibility = View.VISIBLE
            }
        }
    }

    /**
     * Gets the number of items in the list
     */
    override fun getItemCount(): Int {
        return list.size
    }

    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     */
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val ivCartItemImage : ImageView = itemView.findViewById(R.id.iv_cart_item_image)
        val tvCartItemTitle : TextView = itemView.findViewById(R.id.tv_cart_item_title)
        val tvCartItemPrice: TextView = itemView.findViewById(R.id.tv_cart_item_price)
        val tvCartQuantity : TextView = itemView.findViewById(R.id.tv_cart_quantity)
        val ibRemoveCartItem : ImageButton = itemView.findViewById(R.id.ib_remove_cart_item)
        val ibAddCartItem : ImageButton = itemView.findViewById(R.id.ib_add_cart_item)
        val ibDeleteCartItem : ImageButton = itemView.findViewById(R.id.ib_delete_cart_item)
        val tvTotalQuantity : TextView = itemView.findViewById(R.id.totalQuantityText)
    }
}