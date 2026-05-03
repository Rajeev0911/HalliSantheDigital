package com.halliSanthe.app.activities

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.halliSanthe.app.R
import com.halliSanthe.app.adapters.ProductAdapter
import com.halliSanthe.app.models.Product
import com.halliSanthe.app.utils.FavoritesManager

class FavoritesActivity : AppCompatActivity() {

    private lateinit var rvFavorites: RecyclerView
    private lateinit var adapter: ProductAdapter
    private var productList = ArrayList<Product>()
    private lateinit var progressBar: ProgressBar
    private lateinit var layoutEmpty: LinearLayout
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        db = FirebaseFirestore.getInstance()

        rvFavorites = findViewById(R.id.rv_favorites)
        progressBar = findViewById(R.id.progress_bar)
        layoutEmpty = findViewById(R.id.layout_empty)

        rvFavorites.layoutManager = GridLayoutManager(this, 2)
        adapter = ProductAdapter(this, productList)
        rvFavorites.adapter = adapter

        loadFavorites()
    }

    private fun loadFavorites() {
        val favIds = FavoritesManager.getFavorites(this)
        if (favIds.isEmpty()) {
            layoutEmpty.visibility = View.VISIBLE
            return
        }

        progressBar.visibility = View.VISIBLE
        layoutEmpty.visibility = View.GONE

        // Firestore 'in' query has a limit of 10 items. 
        // For a simple app, we will just fetch all favorites.
        db.collection("products")
            .get()
            .addOnSuccessListener { snapshot ->
                val filteredList = ArrayList<Product>()
                for (doc in snapshot.documents) {
                    try {
                        val product = doc.toObject(Product::class.java)
                        if (product != null && favIds.contains(product.productId)) {
                            filteredList.add(product)
                        }
                    } catch (e: Exception) {
                        // Skip corrupted or incompatible documents to prevent crash
                    }
                }
                productList.clear()
                productList.addAll(filteredList)
                adapter.updateList(productList)
                progressBar.visibility = View.GONE
                if (productList.isEmpty()) {
                    layoutEmpty.visibility = View.VISIBLE
                }
            }
            .addOnFailureListener {
                progressBar.visibility = View.GONE
                layoutEmpty.visibility = View.VISIBLE
            }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
