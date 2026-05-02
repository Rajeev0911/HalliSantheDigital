package com.halliSanthe.app.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.halliSanthe.app.R
import com.halliSanthe.app.activities.LoginActivity

class ProfileFragment : Fragment() {

    private lateinit var tvName: TextView
    private lateinit var tvPhone: TextView
    private lateinit var tvProductCount: TextView
    private lateinit var btnLogout: Button

    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(
            R.layout.fragment_profile,
            container,
            false
        )
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        tvName = view.findViewById(R.id.tv_profile_name)
        tvPhone = view.findViewById(R.id.tv_profile_phone)
        tvProductCount = view.findViewById(R.id.tv_product_count)
        btnLogout = view.findViewById(R.id.btn_logout)

        loadProfile()

        btnLogout.setOnClickListener {

            mAuth.signOut()

            val intent = Intent(
                requireContext(),
                LoginActivity::class.java
            )

            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intent)
        }
    }

    private fun loadProfile() {

        val currentUser = mAuth.currentUser ?: return

        val uid = currentUser.uid
        val phone = currentUser.phoneNumber

        tvPhone.text = phone ?: "N/A"

        db.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { doc ->

                if (doc.exists()) {

                    val name = doc.getString("name")

                    tvName.text = name ?: "Artisan"
                }
            }

        db.collection("products")
            .whereEqualTo("sellerId", uid)
            .get()
            .addOnSuccessListener { snap ->

                tvProductCount.text =
                    "${snap.size()} Products Listed"
            }
    }
}