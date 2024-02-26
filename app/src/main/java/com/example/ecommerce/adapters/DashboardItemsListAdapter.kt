package com.example.ecommerce.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommerce.R
import com.example.ecommerce.activities.ProductsDetailsActivity
import com.example.ecommerce.model.Product
import com.example.ecommerce.utils.GlideLoader

/**
 * A adapter class for dashboard items list.
 */
open class DashboardItemsListAdapter(
    private val context: Context,
    private var list: ArrayList<Product>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    /**
     * Inflates the item views which is designed in xml layout file
     *
     * create a new
     * {@link ViewHolder} and initializes some private fields to be used by RecyclerView.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_dashboard_layout,
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
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is MyViewHolder) {

            GlideLoader(context).loadImage(model.productImage, holder.productImageView)

            holder.tv_dashboard_item_title.text = model.productTitle
            holder.tv_dashboard_item_price.text = "Rs. ${model.productPrice}"

            holder.mainRelLayout.setOnClickListener {
                val intent = Intent(context, ProductsDetailsActivity::class.java)
                intent.putExtra("productDetails", model)
                intent.putExtra("editable", false)
                context.startActivity(intent)
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
        val productImageView : ImageView = itemView.findViewById(R.id.iv_dashboard_item_image)
        val mainRelLayout : RelativeLayout = itemView.findViewById(R.id.mainRelLayout)
        val tv_dashboard_item_title : TextView = itemView.findViewById(R.id.tv_dashboard_item_title)
        val tv_dashboard_item_price : TextView = itemView.findViewById(R.id.tv_dashboard_item_price)
    }
}