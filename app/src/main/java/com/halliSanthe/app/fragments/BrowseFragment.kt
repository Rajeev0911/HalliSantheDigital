package com.halliSanthe.app.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.halliSanthe.app.R
import com.halliSanthe.app.adapters.ProductAdapter
import com.halliSanthe.app.models.Product

class BrowseFragment : Fragment() {

    private lateinit var rvProducts: RecyclerView
    private lateinit var adapter: ProductAdapter
    private var productList = ArrayList<Product>()
    private lateinit var progressBar: ProgressBar
    private lateinit var layoutEmpty: LinearLayout
    private lateinit var chipGroup: ChipGroup
    private lateinit var etSearch: TextInputEditText
    private lateinit var db: FirebaseFirestore

    private val categories = listOf(
        "All",
        "Handicrafts",
        "Pottery",
        "Textiles",
        "Toys",
        "Spices & Food",
        "Jewellery",
        "Woodwork",
        "Paintings",
        "Vegetables"
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(
            R.layout.fragment_browse,
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

        rvProducts = view.findViewById(R.id.rv_browse_products)
        progressBar = view.findViewById(R.id.progress_bar)
        layoutEmpty = view.findViewById(R.id.layout_empty)
        chipGroup = view.findViewById(R.id.chip_group_browse)
        etSearch = view.findViewById(R.id.et_search)

        rvProducts.layoutManager =
            GridLayoutManager(requireContext(), 2)

        adapter = ProductAdapter(
            requireContext(),
            productList
        )

        rvProducts.adapter = adapter

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.filter(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        buildChips()

        loadProducts("All")
    }

    private fun buildChips() {

        chipGroup.removeAllViews()

        for (cat in categories) {

            val chip = Chip(requireContext())

            chip.text = cat
            chip.isCheckable = true
            chip.isCheckedIconVisible = false

            chip.setChipBackgroundColorResource(
                R.color.chip_background_selector
            )

            chip.setTextColor(
                resources.getColorStateList(
                    R.color.chip_text_selector,
                    null
                )
            )

            chip.setChipStrokeColorResource(R.color.green_500)
            chip.chipStrokeWidth = 1f

            if (cat == "All") {
                chip.isChecked = true
            }

            chip.setOnClickListener {
                loadProducts(cat)
            }

            chipGroup.addView(chip)
        }
    }

    private fun loadProducts(category: String) {

        progressBar.visibility = View.VISIBLE
        layoutEmpty.visibility = View.GONE

        var query = db.collection("products")
            .orderBy(
                "timestamp",
                Query.Direction.DESCENDING
            )

        if (category != "All") {
            query = query.whereEqualTo(
                "category",
                category
            )
        }

        query.get()
            .addOnSuccessListener { snapshot ->

                val loadedList = ArrayList<Product>()
                for (doc in snapshot.documents) {
                    val product = doc.toObject(Product::class.java)
                    if (product != null) {
                        loadedList.add(product)
                    }
                }

                adapter.updateList(loadedList)

                progressBar.visibility = View.GONE

                if (adapter.itemCount == 0) {

                    layoutEmpty.visibility = View.VISIBLE
                    rvProducts.visibility = View.GONE

                } else {

                    layoutEmpty.visibility = View.GONE
                    rvProducts.visibility = View.VISIBLE
                }
            }
            .addOnFailureListener {

                progressBar.visibility = View.GONE
            }
    }
}