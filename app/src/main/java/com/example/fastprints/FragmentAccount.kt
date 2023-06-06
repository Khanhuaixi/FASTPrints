package com.example.fastprints

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class FragmentAccount : Fragment() {
    private lateinit var auth: FirebaseAuth
    private var databaseReference: DatabaseReference? = null
    private var database: FirebaseDatabase? = null
    private lateinit var emailText: TextView
    private lateinit var firstnameText: TextView
    private lateinit var lastnameText: TextView
    private lateinit var logoutButton: Button
    private lateinit var deleteAccountText: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_account, container, false)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        databaseReference = database?.reference!!.child(Constants.USER_PROFILE_DATABASE_PATH_UPLOADS)
        emailText = view.findViewById(R.id.emailText)
        firstnameText = view.findViewById(R.id.firstnameText)
        lastnameText = view.findViewById(R.id.lastnameText)
        logoutButton = view.findViewById(R.id.logOutButton)
        deleteAccountText = view.findViewById(R.id.deleteAccountText)

        logoutButton.setOnClickListener {
            //function to log out
            auth.signOut()
            //go back to login after log out
            startActivity(Intent(context, LoginActivity::class.java))
        }

        deleteAccountText.setOnClickListener {
            val title = "Delete Account"
            val message = "Are you sure you want to delete this account?"
            val toastText = "Deleting your Account"

            if (context != null) {
                Dialog().showConfirmDialog(
                    title,
                    message,
                    toastText,
                    requireContext(),
                    ::deleteUserAccount
                )
            }
        }

        //load user data from database
        loadProfile()
        // Inflate the layout for this fragment
        return view
    }

    //load user profile when this tab is clicked to show user data fetched from database
    @SuppressLint("SetTextI18n") //Suppress warning: Do not concatenate text displayed with `setText`. Use resource string with placeholders.
    private fun loadProfile() {

        //get current logged in user
        val user = auth.currentUser
        //get database reference of current logged in user
        val userReference = databaseReference?.child(user?.uid!!)

        emailText.text = "Email: " + user?.email

        //add value event listener to listen for data change in database using data reference
        userReference?.addValueEventListener(object : ValueEventListener {
            //detect data changed from database
            override fun onDataChange(snapshot: DataSnapshot) {
                firstnameText.text = "First name: " + snapshot.child("firstname").value.toString()
                lastnameText.text = "Last name: " + snapshot.child("lastname").value.toString()
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun deleteUserAccount() {
        //get current logged in user
        val currentUser = auth.currentUser!!

        //delete from user authentication
        currentUser.delete()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    //delete from realtime database
                    databaseReference = FirebaseDatabase.getInstance()
                        .getReference(Constants.USER_PROFILE_DATABASE_PATH_UPLOADS)
                    databaseReference!!.child((currentUser.uid)).removeValue()
                        .addOnSuccessListener {
                            Log.i(TAG, "User account deleted.")

                            //function to log out
                            auth.signOut()

                            //go back to login after log out
                            startActivity(Intent(context, LoginActivity::class.java))
                        }.addOnCanceledListener {
                        Toast.makeText(context, "Account Delete Not Successful", Toast.LENGTH_LONG).show()
                    }
                }
            }.addOnCanceledListener {
                Toast.makeText(context, "Account Delete Not Successful", Toast.LENGTH_LONG).show()
            }
    }

}