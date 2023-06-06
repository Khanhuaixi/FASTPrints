package com.example.fastprints

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class FragmentCart : Fragment() {

    //Firebase user authentication
    private lateinit var auth: FirebaseAuth

    //recyclerview object
    private lateinit var recyclerView: RecyclerView

    //adapter object
    private lateinit var adapter: RecyclerView.Adapter<*>

    //database reference
    private lateinit var databaseReference: DatabaseReference

    //list to hold all the uploaded images
    private lateinit var uploads: MutableList<UploadedFile>

    @SuppressLint("SetTextI18n") //Suppress: Do not concatenate text displayed with `setText`. Use resource string with placeholders.
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_cart, container, false)
        recyclerView = view.findViewById(R.id.RecyclerView)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        uploads = ArrayList()
        auth = FirebaseAuth.getInstance()

        //get reference of specified file path using instance of database
        databaseReference = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_UPLOADS)
        //add event listener to fetch values from database through database reference
        databaseReference.addValueEventListener(object : ValueEventListener {
            //detect and respond to data change
            override fun onDataChange(snapshot: DataSnapshot) {
                uploads = ArrayList()

                for (postSnapshot in snapshot.children) {
                    //convert function into lambda
                    val oneFile = postSnapshot.getValue(UploadedFile::class.java)
                    if (oneFile != null) {
                        //only retrieve logged in user's order
                        val user = auth.currentUser
                        if (oneFile.getEmail() == user?.email) {
                            //add each file (item) into ArrayList
                            uploads.add(oneFile)
                        }
                    }
                }
                //use 'uploads' arraylist as data in adapter
                //creating adapter
                adapter = CartRecyclerViewAdapter(uploads)

                //adding adapter to recyclerView
                recyclerView.adapter = adapter
            }
            //when process to fetch values is canceled
            override fun onCancelled(error: DatabaseError) {

            }
        })
        return view
    }
}