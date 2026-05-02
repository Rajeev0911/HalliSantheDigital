package com.halliSanthe.app.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.halliSanthe.app.R
import com.halliSanthe.app.models.Product

class ProductDetailActivity : AppCompatActivity() {

    private lateinit var ivProductImage: ImageView
    private lateinit var chipCategory: Chip
    private lateinit var tvName: TextView
    private lateinit var tvPrice: TextView
    private lateinit var tvDescription: TextView
    private lateinit var tvSellerName: TextView
    private lateinit var tvSellerPhone: TextView
    private lateinit var btnContact: Button

    private var product: Product? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        ivProductImage = findViewById(R.id.iv_product_image)
        chipCategory = findViewById(R.id.chip_category)
        tvName = findViewById(R.id.tv_product_name)
        tvPrice = findViewById(R.id.tv_product_price)
        tvDescription = findViewById(R.id.tv_description)
        tvSellerName = findViewById(R.id.tv_seller_name)
        tvSellerPhone = findViewById(R.id.tv_seller_phone)
        btnContact = findViewById(R.id.btn_contact_seller)

        product = intent.getSerializableExtra("product") as? Product

        if (product != null) {
            populateDetails()
        } else {
            finish()
        }

        btnContact.setOnClickListener {
            contactSeller()
        }
    }

    private fun populateDetails() {
        product?.let {
            tvName.text = it.name
            tvPrice.text = "₹ ${it.price}"
            tvDescription.text = it.description
            chipCategory.text = it.category
            tvSellerName.text = it.sellerName
            tvSellerPhone.text = it.sellerPhone

            Glide.with(this)
                .load(it.imageUrl)
                .placeholder(R.drawable.placeholder_image)
                .into(ivProductImage)
        }
    }

    private fun contactSeller() {
        val phone = product?.sellerPhone ?: return
        val productName = product?.name ?: "Product"
        
        // WhatsApp URL
        val message = "Hi, I am interested in your product: $productName on Halli-Santhe Digital."
        val url = "https://api.whatsapp.com/send?phone=+91$phone&text=${Uri.encode(message)}"
        
        try {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        } catch (e: Exception) {
            // Fallback to dialer if WhatsApp is not installed or fail
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:+91$phone")
            startActivity(intent)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
