package com.example.fastprints

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class OrderSuccessful : AppCompatActivity() {

    private lateinit var goToCartButton: Button
    private var paid = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_successful)

        goToCartButton = findViewById(R.id.goToCartButton)

        goToCartButton.setOnClickListener{
            this.let{
                val intent = Intent (it, MainActivity::class.java)
                intent.putExtra("paid", paid)
                it.startActivity(intent)
            }
            finish()
        }
    }
}