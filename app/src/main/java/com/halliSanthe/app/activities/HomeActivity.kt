package com.halliSanthe.app.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.halliSanthe.app.R
import com.halliSanthe.app.fragments.BrowseFragment
import com.halliSanthe.app.fragments.HomeFragment
import com.halliSanthe.app.fragments.MyProductsFragment
import com.halliSanthe.app.fragments.ProfileFragment

class HomeActivity : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView
    private lateinit var fabUpload: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        bottomNav = findViewById(R.id.bottom_nav)
        fabUpload = findViewById(R.id.fab_upload)

        loadFragment(HomeFragment())

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    loadFragment(HomeFragment())
                    true
                }

                R.id.nav_browse -> {
                    loadFragment(BrowseFragment())
                    true
                }

                R.id.nav_my_products -> {
                    loadFragment(MyProductsFragment())
                    true
                }

                R.id.nav_profile -> {
                    loadFragment(ProfileFragment())
                    true
                }

                else -> false
            }
        }

        fabUpload.setOnClickListener {
            startActivity(Intent(this, UploadProductActivity::class.java))
        }
    }

    fun switchToProfile() {
        bottomNav.selectedItemId = R.id.nav_profile
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}