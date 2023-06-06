package com.example.fastprints

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@SuppressLint("StaticFieldLeak")
private lateinit var context: Context

class CartRecyclerViewAdapter (private val uploads: MutableList<UploadedFile>) :
    RecyclerView.Adapter<CartRecyclerViewAdapter.CartRecyclerViewViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartRecyclerViewViewHolder {
        context = parent.context
        val v = LayoutInflater.from(context)
            .inflate(R.layout.elements, parent, false)

        return CartRecyclerViewViewHolder(v)
    }

    private lateinit var databaseReference: DatabaseReference
    private lateinit var storageReference: StorageReference

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: CartRecyclerViewAdapter.CartRecyclerViewViewHolder, position: Int) {
        val upload = uploads[position]

        //filename
        holder.filename.text = upload.getName()
        //order time
        holder.orderTime.text = "Order Date: ${upload.getOrderDate()}"
        //order Id
        holder.orderId.text = "Order Id: ${upload.getOrderId()}"
        //total payment
        holder.totalPayment.text = "Total Payment: ${upload.getTotalPayment()}"
        //payment method
        holder.paymentMethod.text = "Payment Method: ${upload.getPaymentMethod()}"
        //colour mode
        holder.colourMode.text = "Colour Mode: ${upload.getColourMode()}"
        //paper size
        holder.paperSize.text = "Paper Size: ${upload.getPaperSize()}"
        //total copies
        holder.totalCopies.text = "Total Copies: ${upload.getTotalCopies()}"

        //collected or not
        if(upload.getCollectDate() == "Not Collected"){
            holder.collect.text = "Collect"
            holder.cancel.visibility = View.VISIBLE
        }else{
            disableButton(holder.collect, "Collected")
            //show collect date and time
            holder.collectTime.text = "Collection Time: ${upload.getCollectDate()}"
            holder.collectTime.visibility = View.VISIBLE
            holder.cancel.visibility = View.INVISIBLE
        }

        //set listener for collect button
        holder.collect.setOnClickListener {
            Log.e(ContentValues.TAG, "Clicked button")
            //disable button
            disableButton(holder.collect, "Collected")
            //show collect date and time
            holder.collectTime.text = "Collection Time: ${getCurrentDateTime()}"
            holder.collectTime.visibility = View.VISIBLE
            //update collected
            updateData(getCurrentDateTime(), upload.getOrderId())
            //make cancel order invisible
            holder.cancel.visibility = View.INVISIBLE
        }

        holder.cancel.setOnClickListener {
            //only orders made within 30 minutes can be canceled
            val orderTime = holder.orderTime.text.toString().substringAfter(": ")
            val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
            val convertedOrderTime = LocalDateTime.parse(orderTime, formatter)
            val convertedCurrentTime = LocalDateTime.parse(getCurrentDateTime(), formatter)

            //title and message variables for dialogs
            lateinit var title: String
            lateinit var message: String
            lateinit var toastText: String

            //compare if current date time and original order time exceeds or equals 30 minutes
            if( convertedCurrentTime < convertedOrderTime.plusMinutes(30)){
                title = "Cancel Printing Order"
                message = "Are you sure you want to cancel this printing order?"
                toastText = "Printing Order Successfully Cancelled"
                Dialog().showConfirmDialog(title, message, toastText, context, upload.getOrderId(), ::deleteData)
            }else{
                title = "Unable to Cancel Order"
                message = "Orders after 30 minutes of the order time cannot be canceled. \n" +
                        "Therefore, this order cannot be canceled. Sorry for the inconvenience."
                Dialog().showAlertDialog(title, message, context)
            }
        }
    }

    override fun getItemCount(): Int {
        return uploads.size
    }

    inner class CartRecyclerViewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var filename: TextView = itemView.findViewById(R.id.name)
        var orderTime: TextView = itemView.findViewById(R.id.orderTime)
        var collect: Button = itemView.findViewById(R.id.collect)
        var collectTime: TextView = itemView.findViewById(R.id.collectTime)
        var orderId: TextView = itemView.findViewById(R.id.orderId)
        var totalPayment: TextView = itemView.findViewById(R.id.totalPaymentTextView)
        var paymentMethod: TextView = itemView.findViewById(R.id.paymentMethod)
        var cancel: TextView = itemView.findViewById(R.id.cancel)
        var colourMode: TextView = itemView.findViewById(R.id.colourMode)
        var paperSize: TextView = itemView.findViewById(R.id.paperSize)
        var totalCopies: TextView = itemView.findViewById(R.id.totalCopies)

        init {
            collectTime.visibility = View.INVISIBLE
        }
    }

    private fun disableButton(button: Button, newText: String) {
        button.text = newText
        button.isEnabled = false
        button.setTextColor(Color.WHITE)
        button.setBackgroundColor(Color.GRAY)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getCurrentDateTime(): String {
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")

        return current.format(formatter)
    }

    private fun updateData(collectDate: String, orderId: String) {//, name: String, orderDate: String, url: String) {
        databaseReference = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_UPLOADS)
        val uploadedFile = mapOf<String, Any?> (
            "collectDate" to collectDate,
        )
        databaseReference.child(orderId).updateChildren(uploadedFile)
    }

    private fun deleteData(orderId: String) {
        //delete from storage
        storageReference = FirebaseStorage.getInstance().getReference(Constants.STORAGE_PATH_UPLOADS)
        storageReference.child(orderId).delete()

        //delete from realtime database
        databaseReference = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_UPLOADS)
        databaseReference.child(orderId).removeValue()
    }

}