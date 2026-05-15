package com.halliSanthe.app.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.halliSanthe.app.R
import com.halliSanthe.app.models.Artisan

class ArtisanDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_artisan_detail)

        val artisan = intent.getSerializableExtra("artisan") as? Artisan ?: return

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""

        val ivHeader: ImageView = findViewById(R.id.iv_artisan_header)
        val tvHeaderInitials: TextView = findViewById(R.id.tv_header_initials)
        val tvName: TextView = findViewById(R.id.tv_detail_name)
        val tvSpecialty: TextView = findViewById(R.id.tv_detail_specialty)
        val tvBio: TextView = findViewById(R.id.tv_detail_bio)
        val chipAge: Chip = findViewById(R.id.chip_age)
        val btnCall: MaterialButton = findViewById(R.id.btn_call)
        val btnWhatsapp: MaterialButton = findViewById(R.id.btn_whatsapp)

        tvName.text = artisan.name
        tvSpecialty.text = artisan.specialty
        tvBio.text = artisan.bio.ifEmpty { 
            "${artisan.name} is a dedicated artisan from Karnataka with years of experience in ${artisan.specialty.lowercase()}. Their craft is passed down through generations, ensuring the highest quality of traditional art." 
        }
        chipAge.text = "Age: ${artisan.age}"

        if (artisan.imageRes != null) {
            ivHeader.setImageResource(artisan.imageRes)
            tvHeaderInitials.visibility = View.GONE
        } else {
            ivHeader.setImageResource(R.color.green_700)
            tvHeaderInitials.visibility = View.VISIBLE
            tvHeaderInitials.text = artisan.initials
        }

        btnCall.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:${artisan.phone}")
            startActivity(intent)
        }

        btnWhatsapp.setOnClickListener {
            try {
                val url = "https://api.whatsapp.com/send?phone=${artisan.phone}"
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(url)
                startActivity(i)
            } catch (e: Exception) {
                Toast.makeText(this, "WhatsApp not installed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
