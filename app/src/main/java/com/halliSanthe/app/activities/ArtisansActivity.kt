package com.halliSanthe.app.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.halliSanthe.app.R
import com.halliSanthe.app.adapters.ArtisanAdapter
import com.halliSanthe.app.models.Artisan

class ArtisansActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_artisans)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "All Artisans"

        val rvArtisans: RecyclerView = findViewById(R.id.rv_all_artisans)
        rvArtisans.layoutManager = LinearLayoutManager(this)

        val artisans = listOf(
            Artisan("Manjunath Hegde", "Pottery Master", R.drawable.iv_artisan_1, "Manjunath is a 3rd generation master potter from Channapatna.", 52, "+919876543210"),
            Artisan("Gowri Shankar", "Handloom Expert", R.drawable.iv_artisan_2, "Gowri specialized in Ilkal sarees and has been weaving for 25 years.", 48, "+919876543211"),
            Artisan("Basavaraj Patil", "Spices Artisan", R.drawable.iv_artisan_3, "Basavaraj processes organic spices using traditional stone-grinding methods.", 45, "+919876543212"),
            Artisan("Chennamma Devi", "Traditional Weaver", R.drawable.iv_artisan_4, "Expert in traditional hand-spun cotton textiles.", 60, "+919876543213"),
            Artisan("Siddharth Gowda", "Wood Carver", R.drawable.iv_artisan_5, "Master of sandalwood and rosewood carving.", 38, "+919876543214"),
            Artisan("Laxmi Narayan", "Mural Painter", null, "Famous for her vibrant rural life murals.", 42, "+919876543215"),
            Artisan("Puneeth Raj", "Metal Smith", R.drawable.iv_artisan_7, "Traditional bronze and brass idol maker.", 35, "+919876543216"),
            Artisan("Radhika Pandit", "Terracotta Artist", null, "Specializes in terracotta jewelry and home decor.", 29, "+919876543217"),
            Artisan("Darshan Kumar", "Bamboo Craft", R.drawable.iv_artisan_9, "Creating eco-friendly bamboo furniture and tools.", 44, "+919876543218"),
            Artisan("Amulya Gowda", "Silk Weaver", null, "Dedicated to reviving lost silk weaving patterns.", 31, "+919876543219"),
            Artisan("Ganesh Bhat", "Leather Work", null, "Traditional leather sandal and accessory maker.", 55, "+919876543220"),
            Artisan("Sharan Appa", "Stone Sculpture", null, "Specialist in intricate temple stone carvings.", 50, "+919876543221"),
            Artisan("Malashri Devi", "Jewelry Maker", null, "Creating traditional temple jewelry sets.", 47, "+919876543222"),
            Artisan("Yashwanth Rao", "Organic Farmer", null, "Expert in organic pest control and traditional seeds.", 58, "+919876543223"),
            Artisan("Kavitha Lokesh", "Basket Weaver", null, "Master of intricate basket and mat weaving.", 39, "+919876543224")
        )

        val adapter = ArtisanAdapter(artisans) { artisan ->
            val intent = android.content.Intent(this, ArtisanDetailActivity::class.java)
            intent.putExtra("artisan", artisan)
            startActivity(intent)
        }
        rvArtisans.adapter = adapter
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
