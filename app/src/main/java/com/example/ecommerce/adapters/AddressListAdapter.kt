package com.example.ecommerce.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommerce.R
import com.example.ecommerce.activities.AddEditAddressActivity
import com.example.ecommerce.activities.CheckoutActivity
import com.example.ecommerce.model.Address
import com.example.ecommerce.model.CartItem
import com.example.ecommerce.utils.Constants


/**
 * A adapter class for products list items.
 */
// TODO Step 6: Add the parameter as products fragment as we cannot call the delete function of products fragment on the delete button click.
// START
open class AddressListAdapter(
    private val context: Context,
    private var list: ArrayList<Address>,
    private val fromCartListActivity: Boolean,
    private val mCartListItems: ArrayList<CartItem>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
// END

    /**
     * Inflates the item views which is designed in xml layout file
     *
     * create a new
     * {@link ViewHolder} and initializes some private fields to be used by RecyclerView.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_address_layout,
                parent,
                false
            )
        )
    }

    /**
     * Binds each item in the ArrayList to a view
     *
     * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
     * an item.
     *
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is MyViewHolder) {
            holder.fullName.text = model.name
            holder.completeAddress.text = model.address
            holder.addressType.text = model.address
            holder.mobileNo.text = model.mobile.toString()

            if (fromCartListActivity){
                holder.parentLinLayout.setOnClickListener {
//                    Toast.makeText(context, "Address is : ${model.name}", Toast.LENGTH_SHORT).show()
                    val intent = Intent(context, CheckoutActivity::class.java)
                    intent.putExtra(Constants.EXTRA_SELECTED_ADDRESS, model)
                    intent.putExtra(Constants.ALL_CART_LIST, mCartListItems)
                    context.startActivity(intent)
                }
            }
            else{
                holder.parentLinLayout.isFocusable = false
                holder.parentLinLayout.isClickable = false

            }
        }
    }
    /**
     * Gets the number of items in the list
     */
    override fun getItemCount(): Int {
        return list.size
    }

    fun notifyEditItem(activity : Activity, position : Int){
        val intent = Intent(context, AddEditAddressActivity::class.java)
        intent.putExtra(Constants.EXTRA_ADDRESS_DETAILS, list[position])
        activity.startActivity(intent)

        notifyItemChanged(position)
    }

//    fun notifyDeleteItem(activity: Activity, position: Int){
//        FirebaseClass().deleteAddressItemFromFirebase()
//    }

    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     */
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val fullName : TextView = itemView.findViewById(R.id.tv_address_full_name)
        val parentLinLayout : LinearLayout = itemView.findViewById(R.id.parentLinearLayout)
        val completeAddress : TextView = itemView.findViewById(R.id.tv_address_details)
        val mobileNo : TextView = itemView.findViewById(R.id.tv_address_mobile_number)
        val addressType : TextView = itemView.findViewById(R.id.tv_address_type)

//        val itemLinearLayout : LinearLayout  = itemView.findViewById(R.id.mainLinearLayout)
//        val productImageView : CircleImageView = itemView.findViewById(R.id.iv_item_image)
//        val itemName : TextView = itemView.findViewById(R.id.tv_item_name)
//        val itemPrice : TextView = itemView.findViewById(R.id.tv_item_price)
//        val deleteProduct : ImageButton = itemView.findViewById(R.id.ib_delete_product)
    }
}