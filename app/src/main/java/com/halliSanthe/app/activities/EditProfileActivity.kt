package com.halliSanthe.app.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.halliSanthe.app.R
import com.halliSanthe.app.utils.ImageCompressHelper

class EditProfileActivity : AppCompatActivity() {

    private lateinit var ivProfilePic: ImageView
    private lateinit var etName: TextInputEditText
    private lateinit var etBio: TextInputEditText
    private lateinit var btnSave: MaterialButton
    private lateinit var progressBar: ProgressBar

    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private var selectedImageUri: Uri? = null
    private var existingBase64Image: String? = null

    private val imagePickerLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                selectedImageUri = result.data?.data
                Glide.with(this).load(selectedImageUri).centerCrop().into(ivProfilePic)
            }
        }

    private val permissionLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) openImagePicker()
            else Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        ivProfilePic = findViewById(R.id.iv_profile_pic)
        etName = findViewById(R.id.et_profile_name)
        etBio = findViewById(R.id.et_profile_bio)
        btnSave = findViewById(R.id.btn_save_profile)
        progressBar = findViewById(R.id.progress_bar)

        loadCurrentData()

        findViewById<View>(R.id.fab_change_pic).setOnClickListener {
            checkPermissionAndPickImage()
        }

        btnSave.setOnClickListener {
            saveChanges()
        }
    }

    private fun loadCurrentData() {
        val uid = mAuth.currentUser?.uid ?: return
        db.collection("users").document(uid).get().addOnSuccessListener { doc ->
            if (doc.exists()) {
                etName.setText(doc.getString("name"))
                etBio.setText(doc.getString("bio"))
                existingBase64Image = doc.getString("profilePic")
                
                if (!existingBase64Image.isNullOrEmpty()) {
                    val imageBytes = android.util.Base64.decode(existingBase64Image, android.util.Base64.DEFAULT)
                    Glide.with(this).asBitmap().load(imageBytes).centerCrop().into(ivProfilePic)
                }
            }
        }
    }

    private fun checkPermissionAndPickImage() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            openImagePicker()
        } else {
            permissionLauncher.launch(permission)
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        imagePickerLauncher.launch(intent)
    }

    private fun saveChanges() {
        val name = etName.text.toString().trim()
        val bio = etBio.text.toString().trim()

        if (name.isEmpty()) {
            etName.error = "Name cannot be empty"
            return
        }

        progressBar.visibility = View.VISIBLE
        btnSave.isEnabled = false

        var base64Image = existingBase64Image
        if (selectedImageUri != null) {
            base64Image = ImageCompressHelper.compressToBase64(this, selectedImageUri)
        }

        val uid = mAuth.currentUser?.uid ?: return
        val updates = hashMapOf<String, Any>(
            "name" to name,
            "bio" to bio
        )
        if (base64Image != null) {
            updates["profilePic"] = base64Image
        }

        db.collection("users").document(uid).update(updates)
            .addOnSuccessListener {
                progressBar.visibility = View.GONE
                Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                progressBar.visibility = View.GONE
                btnSave.isEnabled = true
                Toast.makeText(this, "Update failed: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
