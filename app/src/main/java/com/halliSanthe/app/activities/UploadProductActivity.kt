package com.halliSanthe.app.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.halliSanthe.app.R
import com.halliSanthe.app.models.Product
import com.halliSanthe.app.utils.GeminiHelper
import com.halliSanthe.app.utils.ImageCompressHelper
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID

class UploadProductActivity : AppCompatActivity() {

    private lateinit var ivProductImage: ImageView
    private lateinit var etName: TextInputEditText
    private lateinit var etPrice: TextInputEditText
    private lateinit var etDescription: TextInputEditText
    private lateinit var spinnerCategory: AutoCompleteTextView
    private lateinit var btnUpload: Button
    private lateinit var btnAiGenerate: MaterialButton
    private lateinit var progressBar: ProgressBar
    private lateinit var layoutPlaceholder: View

    private var selectedImageUri: Uri? = null
    private var sellerName = "Artisan"

    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var storageRef: StorageReference

    private val categories = listOf(
        "Handicrafts",
        "Pottery",
        "Textiles",
        "Toys",
        "Spices & Food",
        "Jewellery",
        "Woodwork",
        "Paintings",
        "Vegetables",
        "Other"
    )

    private val imagePickerLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            if (result.resultCode == RESULT_OK && result.data != null) {
                selectedImageUri = result.data?.data

                Glide.with(this)
                    .load(selectedImageUri)
                    .centerCrop()
                    .into(ivProductImage)

                layoutPlaceholder.visibility = View.GONE
            }
        }

    private val permissionLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->

            if (granted) {
                openImagePicker()
            } else {
                Toast.makeText(
                    this,
                    "Permission needed to pick image",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        storageRef = FirebaseStorage.getInstance().reference.child("products")

        val toolbar: Toolbar = findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "Upload Product"
        }

        ivProductImage = findViewById(R.id.iv_product_image)
        etName = findViewById(R.id.et_product_name)
        etPrice = findViewById(R.id.et_product_price)
        etDescription = findViewById(R.id.et_product_description)
        spinnerCategory = findViewById(R.id.spinner_category)
        btnUpload = findViewById(R.id.btn_upload)
        btnAiGenerate = findViewById(R.id.btn_ai_generate)
        progressBar = findViewById(R.id.progress_bar)
        layoutPlaceholder = findViewById(R.id.layout_placeholder)

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            categories
        )

        spinnerCategory.setAdapter(adapter)

        loadSellerName()

        ivProductImage.setOnClickListener {
            checkPermissionAndPickImage()
        }

        layoutPlaceholder.setOnClickListener {
            checkPermissionAndPickImage()
        }

        spinnerCategory.setOnClickListener {
            spinnerCategory.showDropDown()
        }

        btnUpload.setOnClickListener {
            validateAndUpload()
        }

        btnAiGenerate.setOnClickListener {
            generateAiDescription()
        }
    }

    private fun generateAiDescription() {
        val name = etName.text?.toString()?.trim() ?: ""
        val category = spinnerCategory.text.toString().trim()

        if (TextUtils.isEmpty(name)) {
            etName.error = "Enter product name first"
            return
        }

        if (TextUtils.isEmpty(category)) {
            Toast.makeText(this, "Select a category first", Toast.LENGTH_SHORT).show()
            return
        }

        btnAiGenerate.isEnabled = false
        btnAiGenerate.text = "Generating..."

        lifecycleScope.launch {
            val description = GeminiHelper.generateDescription(name, category)
            
            btnAiGenerate.isEnabled = true
            btnAiGenerate.text = "✨ Magic Description"

            if (description != null) {
                etDescription.setText(description)
                Toast.makeText(this@UploadProductActivity, "AI Description Generated!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@UploadProductActivity, "AI Generation failed. Try again.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadSellerName() {

        val uid = mAuth.currentUser?.uid ?: return

        db.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { doc ->

                if (doc.exists() && doc.getString("name") != null) {
                    sellerName = doc.getString("name")!!
                }
            }
    }

    private fun checkPermissionAndPickImage() {

        val permission =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Manifest.permission.READ_MEDIA_IMAGES
            } else {
                Manifest.permission.READ_EXTERNAL_STORAGE
            }

        if (
            ContextCompat.checkSelfPermission(
                this,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            openImagePicker()

        } else {

            permissionLauncher.launch(permission)
        }
    }

    private fun openImagePicker() {

        val intent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )

        imagePickerLauncher.launch(intent)
    }

    private fun validateAndUpload() {

        val name =
            etName.text?.toString()?.trim() ?: ""

        val price =
            etPrice.text?.toString()?.trim() ?: ""

        val desc =
            etDescription.text?.toString()?.trim() ?: ""

        val category =
            spinnerCategory.text.toString().trim()

        if (TextUtils.isEmpty(name)) {
            etName.error = "Enter product name"
            return
        }

        if (TextUtils.isEmpty(price)) {
            etPrice.error = "Enter price"
            return
        }

        if (TextUtils.isEmpty(category)) {
            Toast.makeText(
                this,
                "Select a category",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        if (selectedImageUri == null) {
            Toast.makeText(
                this,
                "Please select a product image",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        uploadProduct(name, price, desc, category)
    }

    private fun uploadProduct(
        name: String,
        price: String,
        desc: String,
        category: String
    ) {

        showLoading(true)

        val compressedFile: File? =
            ImageCompressHelper.compressImage(
                this,
                selectedImageUri
            )

        if (compressedFile == null) {

            Toast.makeText(
                this,
                "Image compression failed",
                Toast.LENGTH_SHORT
            ).show()

            showLoading(false)

            return
        }

        val imageFileName =
            "product_${UUID.randomUUID()}.jpg"

        val imageRef =
            storageRef.child(imageFileName)

        imageRef.putFile(Uri.fromFile(compressedFile))
            .addOnSuccessListener {

                imageRef.downloadUrl
                    .addOnSuccessListener { downloadUri ->

                        val uid =
                            mAuth.currentUser?.uid ?: ""

                        val phone =
                            mAuth.currentUser?.phoneNumber ?: ""

                        val pid =
                            UUID.randomUUID().toString()

                        val product = Product(
                            pid,
                            name,
                            price,
                            category,
                            desc,
                            downloadUri.toString(),
                            uid,
                            sellerName,
                            phone
                        )

                        db.collection("products")
                            .document(pid)
                            .set(product)
                            .addOnSuccessListener {

                                showLoading(false)

                                Toast.makeText(
                                    this,
                                    "Product uploaded successfully!",
                                    Toast.LENGTH_SHORT
                                ).show()

                                finish()
                            }
                            .addOnFailureListener { e ->

                                showLoading(false)

                                Toast.makeText(
                                    this,
                                    "Failed to save product: ${e.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
            }
            .addOnFailureListener { e ->

                showLoading(false)

                Toast.makeText(
                    this,
                    "Image upload failed: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun showLoading(show: Boolean) {

        progressBar.visibility =
            if (show) View.VISIBLE else View.GONE

        btnUpload.isEnabled = !show
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}