package com.halliSanthe.app.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.halliSanthe.app.R
import com.halliSanthe.app.activities.ProductDetailActivity
import com.halliSanthe.app.models.Product

class ProductAdapter(
    private val context: Context,
    private val productList: MutableList<Product>,
    private val showDelete: Boolean = false,
    private val onDeleteClick: ((Product) -> Unit)? = null
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    private val productListFull = ArrayList(productList)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProductViewHolder {

        val view = LayoutInflater.from(context)
            .inflate(
                R.layout.item_product_card,
                parent,
                false
            )

        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ProductViewHolder,
        position: Int
    ) {

        val product = productList[position]

        holder.tvName.text = product.name
        holder.tvPrice.text = "₹ ${product.price}"
        holder.tvCategory.text = product.category

        if (product.imageUrl.length > 500) {
            // It's a Base64 string
            val imageBytes = android.util.Base64.decode(product.imageUrl, android.util.Base64.DEFAULT)
            Glide.with(context)
                .asBitmap()
                .load(imageBytes)
                .placeholder(R.drawable.placeholder_image)
                .centerCrop()
                .into(holder.ivProduct)
        } else {
            // It's a regular URL
            Glide.with(context)
                .load(product.imageUrl)
                .placeholder(R.drawable.placeholder_image)
                .centerCrop()
                .into(holder.ivProduct)
        }

        // Handle Delete Button
        if (showDelete) {
            holder.ivDelete.visibility = View.VISIBLE
            holder.ivDelete.setOnClickListener {
                onDeleteClick?.invoke(product)
            }
        } else {
            holder.ivDelete.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {

            val intent = Intent(
                context,
                ProductDetailActivity::class.java
            )

            intent.putExtra("product", product)

            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    fun filter(query: String) {

        productList.clear()

        if (query.isEmpty()) {

            productList.addAll(productListFull)

        } else {

            val lower = query.lowercase().trim()

            for (product in productListFull) {

                if (
                    product.name.lowercase().contains(lower) ||
                    product.category.lowercase().contains(lower)
                ) {

                    productList.add(product)
                }
            }
        }

        notifyDataSetChanged()
    }

    fun updateList(newList: List<Product>) {
        // Create a new list for the internal reference to avoid clearing issues
        productList.clear()
        productList.addAll(newList)
        
        // Always sync the full list for searching
        productListFull.clear()
        productListFull.addAll(newList)
        
        notifyDataSetChanged()
    }

    class ProductViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val ivProduct: ImageView =
            itemView.findViewById(R.id.iv_product)

        val ivDelete: ImageView =
            itemView.findViewById(R.id.iv_delete)

        val tvName: TextView =
            itemView.findViewById(R.id.tv_product_name)

        val tvPrice: TextView =
            itemView.findViewById(R.id.tv_product_price)

        val tvCategory: TextView =
            itemView.findViewById(R.id.tv_product_category)
    }
}