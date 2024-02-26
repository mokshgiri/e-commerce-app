package com.example.ecommerce.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommerce.R
import com.example.ecommerce.activities.ProductsDetailsActivity
import com.example.ecommerce.fragments.ProductsFragment
import com.example.ecommerce.model.Product
import com.example.ecommerce.utils.GlideLoader
import de.hdodenhof.circleimageview.CircleImageView


/**
 * A adapter class for products list items.
 */
// TODO Step 6: Add the parameter as products fragment as we cannot call the delete function of products fragment on the delete button click.
// START
open class MyProductsListAdapter(
    private val context: Context,
    private var list: ArrayList<Product>,
    private val fragment: ProductsFragment
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
                R.layout.item_list_layout,
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

            GlideLoader(context).loadImage(model.productImage, holder.productImageView)

            holder.itemName.text = model.productTitle
            holder.itemPrice.text = "Rs. ${model.productPrice}"

            // TODO Step 4: Assigning the click event to the delete button.
            // START
            holder.deleteProduct.setOnClickListener {

                // TODO Step 8: Now let's call the delete function of the ProductsFragment.
                // START
                fragment.deleteProduct(model.productId)
                // END
            }

            holder.itemLinearLayout.setOnClickListener {
                val intent = Intent(context, ProductsDetailsActivity::class.java)
                intent.putExtra("productDetails", model)
                intent.putExtra("editable", true)
                context.startActivity(intent)
            }
            // END
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
        val itemLinearLayout : LinearLayout  = itemView.findViewById(R.id.mainLinearLayout)
        val productImageView : CircleImageView = itemView.findViewById(R.id.iv_item_image)
        val itemName : TextView = itemView.findViewById(R.id.tv_item_name)
        val itemPrice : TextView = itemView.findViewById(R.id.tv_item_price)
        val deleteProduct : ImageButton = itemView.findViewById(R.id.ib_delete_product)
    }
}