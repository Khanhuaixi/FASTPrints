package com.example.fastprints

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_registration.*

class RegistrationActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private var databaseReference :  DatabaseReference? = null
    private var database: FirebaseDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        databaseReference = database?.reference!!.child(Constants.USER_PROFILE_DATABASE_PATH_UPLOADS)

        register()
    }

    private fun register() {
        registerButton.setOnClickListener {
            when {
                TextUtils.isEmpty(firstnameInput.text.toString()) -> {
                    firstnameInput.error = "Please enter first name"
                    return@setOnClickListener
                }
                TextUtils.isEmpty(lastnameInput.text.toString()) -> {
                    lastnameInput.error = "Please enter last name"
                    return@setOnClickListener
                }
                TextUtils.isEmpty(emailInput.text.toString()) -> {
                    emailInput.error = "Please enter email"
                    return@setOnClickListener
                }
                TextUtils.isEmpty(passwordInput.text.toString()) -> {
                    passwordInput.error = "Please enter password" //use property access syntax instead of setter method
                    return@setOnClickListener
                }
                else -> auth.createUserWithEmailAndPassword(
                    emailInput.text.toString(),
                    passwordInput.text.toString()
                )
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            //store user data in realtime database
                            val currentUser = auth.currentUser
                            val currentUSerDb = databaseReference?.child((currentUser?.uid!!))
                            currentUSerDb?.child("firstname")
                                ?.setValue(firstnameInput.text.toString())
                            currentUSerDb?.child("lastname")
                                ?.setValue(lastnameInput.text.toString())
                            currentUSerDb?.child("email")
                                ?.setValue(emailInput.text.toString())

                            Toast.makeText(
                                this@RegistrationActivity,
                                "Registered Successfully.",
                                Toast.LENGTH_LONG
                            ).show()
                            finish()

                        } else {
                            Toast.makeText(
                                this@RegistrationActivity,
                                "Registration failed, please try again!",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
            }
        }

        backToLoginButton.setOnClickListener {
            startActivity(Intent(this@RegistrationActivity, LoginActivity::class.java))
        }
    }
}