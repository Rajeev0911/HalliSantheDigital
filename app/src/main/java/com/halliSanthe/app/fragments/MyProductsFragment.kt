package com.halliSanthe.app.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.halliSanthe.app.R
import com.halliSanthe.app.activities.UploadProductActivity
import com.halliSanthe.app.adapters.ProductAdapter
import com.halliSanthe.app.models.Product

class MyProductsFragment : Fragment() {

    private lateinit var rvMyProducts: RecyclerView
    private lateinit var adapter: ProductAdapter
    private var productList = ArrayList<Product>()
    private lateinit var progressBar: ProgressBar
    private lateinit var layoutEmpty: LinearLayout
    private lateinit var db: FirebaseFirestore
    private lateinit var mAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(
            R.layout.fragment_my_products,
            container,
            false
        )
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        db = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()

        rvMyProducts = view.findViewById(R.id.rv_my_products)
        progressBar = view.findViewById(R.id.progress_bar)
        layoutEmpty = view.findViewById(R.id.layout_empty)

        rvMyProducts.layoutManager =
            GridLayoutManager(requireContext(), 2)

        adapter = ProductAdapter(
            requireContext(),
            productList,
            showDelete = true,
            onDeleteClick = { product ->
                showDeleteConfirmation(product)
            },
            onEditClick = { product ->
                val intent = Intent(requireContext(), UploadProductActivity::class.java)
                intent.putExtra("product", product)
                startActivity(intent)
            }
        )

        rvMyProducts.adapter = adapter

        view.findViewById<View>(R.id.btn_empty_upload).setOnClickListener {
            startActivity(Intent(requireContext(), UploadProductActivity::class.java))
        }

        loadMyProducts()
    }

    private fun showDeleteConfirmation(product: Product) {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Delete Product")
            .setMessage("Are you sure you want to delete '${product.name}'?")
            .setPositiveButton("Delete") { _, _ ->
                deleteProduct(product)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteProduct(product: Product) {
        db.collection("products")
            .document(product.productId)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Product deleted", Toast.LENGTH_SHORT).show()
                loadMyProducts() // Refresh the list
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onResume() {
        super.onResume()
        loadMyProducts()
    }

    private fun loadMyProducts() {

        val uid = mAuth.currentUser?.uid ?: return

        progressBar.visibility = View.VISIBLE

        db.collection("products")
            .whereEqualTo("sellerId", uid)
            .orderBy(
                "timestamp",
                Query.Direction.DESCENDING
            )
            .get()
            .addOnSuccessListener { snapshot ->

                val loadedList = ArrayList<Product>()
                for (doc in snapshot.documents) {
                    try {
                        val product = doc.toObject(Product::class.java)
                        if (product != null) {
                            loadedList.add(product)
                        }
                    } catch (e: Exception) {
                        // Skip corrupted or incompatible documents
                    }
                }

                adapter.updateList(loadedList)

                progressBar.visibility = View.GONE

                if (adapter.itemCount == 0) {

                    layoutEmpty.visibility = View.VISIBLE
                    rvMyProducts.visibility = View.GONE

                } else {

                    layoutEmpty.visibility = View.GONE
                    rvMyProducts.visibility = View.VISIBLE
                }
            }
            .addOnFailureListener {

                progressBar.visibility = View.GONE

                Toast.makeText(
                    requireContext(),
                    "Failed to load products",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}