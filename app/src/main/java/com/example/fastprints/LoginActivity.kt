package com.example.fastprints

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        //check if user already logged in
        val currentUser = auth.currentUser
        //if already logged in then start main activity, no need login again
        if(currentUser != null) {
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            //finish login activity
            finish()
        }else{
            login()
            registerText.setOnClickListener{
                startActivity(Intent(this@LoginActivity, RegistrationActivity::class.java))
            }
        }
    }
    //allow user login to app
    private fun login() {

        loginButton.setOnClickListener {

            if(TextUtils.isEmpty(emailInput.text.toString())){
                emailInput.error = "Please enter email"
                return@setOnClickListener
            }
            else if(TextUtils.isEmpty(passwordInput.text.toString())){
                passwordInput.error = "Please enter password"
                return@setOnClickListener
            }
            auth.signInWithEmailAndPassword(emailInput.text.toString(), passwordInput.text.toString())
                .addOnCompleteListener {
                    if(it.isSuccessful) {
                        //if login successful, go to main activity
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        //finish current login activity, since no use anymore
                        finish()
                    } else {
                        //if login fail, show error message in toast
                        Toast.makeText(this@LoginActivity, "Login failed, please try again! ", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }
}