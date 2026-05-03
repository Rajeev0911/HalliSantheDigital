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
    private lateinit var btnCall: Button
    private lateinit var btnFavorite: android.view.View
    private lateinit var tvFavIcon: TextView
    private lateinit var btnShare: android.view.View

    private var product: Product? = null
    private var isFavorite = false

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
        btnCall = findViewById(R.id.btn_call_seller)
        btnFavorite = findViewById(R.id.btn_favorite)
        tvFavIcon = findViewById(R.id.tv_fav_icon)
        btnShare = findViewById(R.id.btn_share_product)

        product = intent.getSerializableExtra("product") as? Product

        if (product != null) {
            populateDetails()
        } else {
            finish()
        }

        btnContact.setOnClickListener {
            contactSeller()
        }

        btnCall.setOnClickListener {
            callSeller()
        }

        btnFavorite.setOnClickListener {
            toggleFavorite()
        }

        btnShare.setOnClickListener {
            shareProduct()
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

            // Set initial favorite state
            isFavorite = com.halliSanthe.app.utils.FavoritesManager.isFavorite(this, it.productId ?: "")
            updateFavoriteUI()

            if (it.imageUrl.length > 500) {
                val imageBytes = android.util.Base64.decode(it.imageUrl, android.util.Base64.DEFAULT)
                Glide.with(this)
                    .asBitmap()
                    .load(imageBytes)
                    .placeholder(R.drawable.placeholder_image)
                    .into(ivProductImage)
            } else {
                Glide.with(this)
                    .load(it.imageUrl)
                    .placeholder(R.drawable.placeholder_image)
                    .into(ivProductImage)
            }
        }
    }

    private fun contactSeller() {
        val phone = product?.sellerPhone ?: return
        val formattedPhone = getFormattedPhone(phone)
        val productName = product?.name ?: "Product"
        
        val message = "Hi, I am interested in your product: $productName on Halli-Santhe Digital."
        val url = "https://api.whatsapp.com/send?phone=$formattedPhone&text=${Uri.encode(message)}"
        
        try {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        } catch (e: Exception) {
            callSeller()
        }
    }

    private fun callSeller() {
        val phone = product?.sellerPhone ?: return
        val formattedPhone = getFormattedPhone(phone)
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:+$formattedPhone")
        startActivity(intent)
    }

    private fun getFormattedPhone(phone: String): String {
        var cleanPhone = phone.replace(" ", "").replace("-", "").replace("(", "").replace(")", "")
        if (cleanPhone.startsWith("+")) {
            cleanPhone = cleanPhone.substring(1)
        }
        // If it starts with 91 and is longer than a standard 10-digit number, remove the 91
        if (cleanPhone.startsWith("91") && cleanPhone.length > 10) {
            cleanPhone = cleanPhone.substring(2)
        }
        return "91$cleanPhone"
    }

    private fun shareProduct() {
        val productName = product?.name ?: "Product"
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Check out this handicraft!")
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Look at this beautiful $productName I found on Halli-Santhe Digital! Support our local artisans.")
        startActivity(Intent.createChooser(shareIntent, "Share via"))
    }

    private fun toggleFavorite() {
        val pid = product?.productId ?: return
        isFavorite = com.halliSanthe.app.utils.FavoritesManager.toggleFavorite(this, pid)
        updateFavoriteUI()
        
        if (isFavorite) {
            android.widget.Toast.makeText(this, "Added to Favorites!", android.widget.Toast.LENGTH_SHORT).show()
        } else {
            android.widget.Toast.makeText(this, "Removed from Favorites", android.widget.Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateFavoriteUI() {
        tvFavIcon.text = if (isFavorite) "❤️" else "🤍"
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
