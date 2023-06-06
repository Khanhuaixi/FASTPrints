package com.example.fastprints

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.annotation.RequiresApi
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


class FragmentPrint : Fragment() {

    private lateinit var uri : Uri //hold file address
    private lateinit var storageReference : StorageReference
    private lateinit var databaseReference: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_print, container, false)

        storageReference = FirebaseStorage.getInstance().getReference(Constants.STORAGE_PATH_UPLOADS)
        databaseReference = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_UPLOADS)
        val uploadButton = view.findViewById<Button>(R.id.uploadButton)
        uploadButton.setOnClickListener{
            browseDocuments()
        }
        return view
    }

    private fun browseDocuments() {

        val mimeTypes =
            arrayOf(
                "application/msword","application/vnd.openxmlformats-officedocument.wordprocessingml.document",// .doc & .docx
                "application/vnd.ms-powerpoint","application/vnd.openxmlformats-officedocument.presentationml.presentation", "application/vnd.google-apps.presentation", // .ppt & .pptx
                "application/vnd.ms-excel","application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xls & .xlsx
                "text/plain", //txt file
                "application/pdf", //pdf file
                "image/jpeg") //images

        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.type = if (mimeTypes.size == 1)  mimeTypes[0] else "*/*"
            if (mimeTypes.isNotEmpty()) {
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            }
        } else {
            var mimeTypesStr = ""
            for ( mimeType in mimeTypes) {
                mimeTypesStr += "$mimeType|"
            }
            intent.type = mimeTypesStr.substring(0,mimeTypesStr.length - 1)
        }
        startActivityForResult(Intent.createChooser(intent,"Choose File"), Constants.REQUEST_CODE_DOC)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            if (requestCode == Constants.REQUEST_CODE_DOC) {
                //proceed to upload to database
                uri = data!!.data!!

                //make payment first only upload
                //only left browse document here
                makePayment()
            }
        }
    }

    private fun makePayment(){
        activity?.let{
            val intent = Intent (it, MakePayment::class.java)
            intent.putExtra("uri", uri.toString())
            it.startActivity(intent)
        }
    }

}