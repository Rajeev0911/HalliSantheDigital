package com.halliSanthe.app.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
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
    private lateinit var ivProfilePic: ImageView
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
        tvProductCount = view.findViewById(R.id.tv_product_count_val)
        ivProfilePic = view.findViewById(R.id.iv_profile_pic_main)
        btnLogout = view.findViewById(R.id.btn_logout)

        view.findViewById<View>(R.id.item_edit_profile).setOnClickListener {
            startActivity(Intent(requireContext(), com.halliSanthe.app.activities.EditProfileActivity::class.java))
        }

        view.findViewById<View>(R.id.item_help_support).setOnClickListener {
            showHelpDialog()
        }

        view.findViewById<View>(R.id.item_about_app).setOnClickListener {
            showAboutDialog()
        }

        view.findViewById<View>(R.id.item_my_favorites).setOnClickListener {
            startActivity(Intent(requireContext(), com.halliSanthe.app.activities.FavoritesActivity::class.java))
        }

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

    override fun onResume() {
        super.onResume()
        loadProfile()
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
                    val profilePic = doc.getString("profilePic")

                    tvName.text = name ?: "Artisan"
                    
                    if (!profilePic.isNullOrEmpty()) {
                        if (profilePic.length > 500) {
                            val imageBytes = android.util.Base64.decode(profilePic, android.util.Base64.DEFAULT)
                            com.bumptech.glide.Glide.with(this).asBitmap().load(imageBytes).centerCrop().into(ivProfilePic)
                        } else {
                            com.bumptech.glide.Glide.with(this).load(profilePic).centerCrop().into(ivProfilePic)
                        }
                    }
                }
            }

        db.collection("products")
            .whereEqualTo("sellerId", uid)
            .get()
            .addOnSuccessListener { snap ->
                tvProductCount.text = snap.size().toString()
            }
    }

    private fun showHelpDialog() {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Help & Support")
            .setMessage("For any issues regarding product uploads or app functionality, please contact our support team at support@hallisanthe.com or call us at +91 98765 43210.\n\nWe are here to support our rural artisans!")
            .setPositiveButton("OK", null)
            .show()
    }

    private fun showAboutDialog() {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("About Halli-Santhe")
            .setMessage("Halli-Santhe (Rural Market) is a digital platform dedicated to bringing the authentic craftsmanship of rural Indian artisans directly to your doorstep.\n\nOur mission is to empower local creators and preserve traditional arts by removing middlemen and creating a transparent marketplace.\n\nVersion 1.0.4")
            .setPositiveButton("OK", null)
            .show()
    }
}