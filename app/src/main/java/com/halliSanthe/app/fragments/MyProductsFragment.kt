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

        val fabAdd: FloatingActionButton =
            view.findViewById(R.id.fab_add_product)

        fabAdd.setOnClickListener {
            startActivity(
                Intent(
                    requireContext(),
                    UploadProductActivity::class.java
                )
            )
        }

        rvMyProducts.layoutManager =
            GridLayoutManager(requireContext(), 2)

        adapter = ProductAdapter(
            requireContext(),
            productList
        )

        rvMyProducts.adapter = adapter

        loadMyProducts()
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

                productList.clear()

                for (doc in snapshot.documents) {

                    val product =
                        doc.toObject(Product::class.java)

                    if (product != null) {
                        productList.add(product)
                    }
                }

                adapter.updateList(productList)

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