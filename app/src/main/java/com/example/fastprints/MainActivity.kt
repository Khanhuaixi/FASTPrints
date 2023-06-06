package com.example.fastprints

import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.os.Bundle
import android.view.WindowManager
import androidx.fragment.app.Fragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //check if is from OrderSuccessful page
        val paid: Boolean = intent.getBooleanExtra("paid", false)
        //this line hide status bar
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        val navigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        supportFragmentManager.beginTransaction().replace(R.id.body_container, FragmentHome())
            .commit()

        //check if paid
        if (paid) {
            navigationView.selectedItemId = R.id.nav_cart
            supportFragmentManager.beginTransaction().replace(R.id.body_container, FragmentCart())
                .commit()
        } else {
            navigationView.selectedItemId = R.id.nav_home
        }

        navigationView.setOnNavigationItemSelectedListener { item ->
            var fragment: Fragment? = null
            when (item.itemId) {
                R.id.nav_home -> fragment = FragmentHome()
                R.id.nav_print -> fragment = FragmentPrint()
                R.id.nav_cart -> fragment = FragmentCart()
                R.id.nav_account -> fragment = FragmentAccount()
            }
            supportFragmentManager.beginTransaction().replace(R.id.body_container, fragment!!)
                .commit()
            true
        }
    }
}