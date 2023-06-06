package com.example.fastprints

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.payment_make.*
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.properties.Delegates


class MakePayment : AppCompatActivity() {

    private lateinit var colourModeRadioGroup: RadioGroup
    private lateinit var colourModeRadioButton: RadioButton
    private lateinit var paperSizeRadioGroup: RadioGroup
    private lateinit var paperSizeRadioButton: RadioButton
    private lateinit var totalCopiesNumberButton: ElegantNumberButton
    private lateinit var cashOnDeliveryButton: Button
    private lateinit var cimbBankButton: Button
    private lateinit var publicBankButton: Button
    private lateinit var maybankButton: Button
    private lateinit var affinBankButton: Button
    private var unitPrice by Delegates.notNull<Double>()

    private lateinit var uri : Uri //hold file address
    private lateinit var storageReference : StorageReference
    private lateinit var databaseReference: DatabaseReference
    private var filename: String? = "Unknown"
    private var paymentMethod: String = "Unknown"

    private lateinit var auth: FirebaseAuth

    @SuppressLint("SetTextI18n") //Suppress: Do not concatenate text displayed with `setText`. Use resource string with placeholders.
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.payment_make)

        colourModeRadioGroup = findViewById(R.id.colourModeRadioGroup)
        colourModeRadioButton = findViewById(colourModeRadioGroup.checkedRadioButtonId)
        paperSizeRadioGroup = findViewById(R.id.paperSizeRadioGroup)
        paperSizeRadioButton = findViewById(paperSizeRadioGroup.checkedRadioButtonId)
        totalCopiesNumberButton = findViewById(R.id.totalCopiesNumberButton)
        cashOnDeliveryButton = findViewById(R.id.cashOnDeliveryButton)
        cimbBankButton = findViewById(R.id.cimbBankButton)
        publicBankButton = findViewById(R.id.publicBankButton)
        maybankButton = findViewById(R.id.maybankButton)
        affinBankButton = findViewById(R.id.affinBankButton)

        storageReference = FirebaseStorage.getInstance().getReference(Constants.STORAGE_PATH_UPLOADS)
        databaseReference = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_UPLOADS)

        //get current logged in user
        auth = FirebaseAuth.getInstance()

        //update total payment
        updateUnitPrice()
        var totalCopies: Int = Integer.parseInt(totalCopiesNumberButton.number.toString())
        lateinit var totalPrice: BigDecimal

        //detect change of checked radio button, update unit price
        colourModeRadioGroup.setOnCheckedChangeListener { _, selectedId ->
            // find the radiobutton by returned id, selectedId is the id of selected radio button
            colourModeRadioButton = findViewById<View>(selectedId) as RadioButton

            //update unitPrice
            updateUnitPrice()

            //alert user that the colour mode has changed
            Toast.makeText(this, "Colour Mode: " + colourModeRadioButton.text, Toast.LENGTH_SHORT)
                .show()

            //update total price and UI
            totalPrice = MathOperation().calculateTotalPayment(unitPrice, totalCopies)
            totalPayment.text = "RM $totalPrice"
        }

        //update checked radio button for paper size
        paperSizeRadioGroup.setOnCheckedChangeListener { _, selectedId ->
            paperSizeRadioButton = findViewById<View>(selectedId) as RadioButton
        }

        totalCopiesNumberButton.setOnValueChangeListener { _, _, newValue ->
            //update totalCopies with new value
            totalCopies = newValue

            //update total price and UI
            totalPrice = MathOperation().calculateTotalPayment(unitPrice, totalCopies)
            totalPayment.text = "RM $totalPrice"
        }


        cashOnDeliveryButton.setOnClickListener {
            paymentMethod = "Cash On Delivery"
            uploadFile(totalCopies)
        }

        cimbBankButton.setOnClickListener{
            paymentMethod = "CIMB Bank"
            uploadFile(totalCopies)
        }

        publicBankButton.setOnClickListener{
            paymentMethod = "Public Bank"
            uploadFile(totalCopies)
        }

        maybankButton.setOnClickListener{
            paymentMethod = "Maybank"
            uploadFile(totalCopies)
        }

        affinBankButton.setOnClickListener{
            paymentMethod = "Affin Bank"
            uploadFile(totalCopies)
        }

    }



    private fun updateUnitPrice()  {
        val blackAndWhite = getString(R.string.black_and_white)
        val coloured = getString(R.string.coloured)

        if (colourModeRadioButton.text == blackAndWhite)
            unitPrice = 1.0
        else if (colourModeRadioButton.text == coloured)
            unitPrice = 2.0
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun uploadFile(totalCopies: Int) {
        if(totalCopies != 0){
            //get current logged in user
            val user = auth.currentUser
            val orderId = databaseReference.push().key //uri as order id

            uri = Uri.parse(intent.getStringExtra("uri"))

            //get chosen file's filename from uri
            filename = getFilename(uri)
            //create storage bucket reference
            val mReference = storageReference.child(orderId.toString())

            try {
                //upload to STORAGE
                mReference.putFile(uri).addOnSuccessListener { //make sure uploaded successfully
                        taskSnapshot: UploadTask.TaskSnapshot? ->
                    taskSnapshot!!.storage.downloadUrl

                    val upload = UploadedFile(
                        filename!!,
                        taskSnapshot.metadata?.reference?.downloadUrl.toString(),
                        getCurrentDateTime(),
                        "Not Collected",
                        orderId.toString(),
                        user?.email.toString(),
                        totalPayment.text.toString(),
                        paymentMethod,
                        colourModeRadioButton.text.toString(),
                        paperSizeRadioButton.text.toString(),
                        totalCopies.toString()
                    )

                    //upload to FIREBASE DATABASE (REALTIME DATABASE)
                    if (orderId != null) {
                        databaseReference.child(orderId).setValue(upload)
                    }

                    //indicate uploaded and paid successfully
                    Toast.makeText(this, "Successfully Uploaded and Made Payment for ${filename}.", Toast.LENGTH_LONG).show()
                    //retrieve FCM registration token
                    retrieveFcmRegistrationToken()
                    //show order successful
                    startActivity(Intent(this@MakePayment, OrderSuccessful::class.java))
                    finish()
                }
                    .addOnFailureListener { exception ->
                        Toast.makeText(this, exception.message, Toast.LENGTH_LONG).show()
                    }
            } catch (e: Exception) {
                //in case connection failure
                Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show()
            }
        }else{
            Toast.makeText(this, "Number of Copies cannot be 0 when submit for printing", Toast.LENGTH_LONG).show()
        }
    }

    private fun getFilename(uri: Uri): String? {
        val cursor = this.contentResolver?.query(uri, null, null, null, null)
        var filename: String? = null

        cursor?.getColumnIndex(OpenableColumns.DISPLAY_NAME)?.let { nameIndex ->
            cursor.moveToFirst()
            filename = cursor.getString(nameIndex)
            cursor.close()
        }
        Log.i(TAG, "File name: $filename")
        return filename
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getCurrentDateTime(): String {
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
        val formatted = current.format(formatter)
        Log.i(TAG, "Current Date and Time: $formatted")

        return formatted
    }

    private fun retrieveFcmRegistrationToken(){
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.e(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and toast
            val msg = "FCM Registration Token: $token"
            //show FCM registration token in log
            Log.i(TAG, msg)
        })
    }
}