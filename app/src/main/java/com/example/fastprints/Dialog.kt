package com.example.fastprints

import android.app.AlertDialog
import android.content.Context
import android.widget.Toast

class Dialog {
    fun showConfirmDialog(title: String, message: String, toastText: String, context: Context, action: () -> Unit) {
        val alertDialog: AlertDialog.Builder = AlertDialog.Builder(context)
        alertDialog.setTitle(title)
        alertDialog.setMessage(message)
        alertDialog.setPositiveButton(
            "yes"
        ) { _, _ ->
            action()
            Toast.makeText(context, toastText, Toast.LENGTH_LONG).show()

        }
        alertDialog.setNegativeButton(
            "No"
        ) { _, _ ->}
        val alert: AlertDialog = alertDialog.create()
        alert.setCanceledOnTouchOutside(false)
        alert.show()
    }

    fun showConfirmDialog(title: String, message: String, toastText: String, context: Context, orderId: String, actionWithOrderId: (orderId: String) -> Unit){
        val alertDialog: AlertDialog.Builder = AlertDialog.Builder(context)
        alertDialog.setTitle(title)
        alertDialog.setMessage(message)
        alertDialog.setPositiveButton(
            "yes"
        ) { _, _ ->
            actionWithOrderId(orderId)
            Toast.makeText(context, toastText, Toast.LENGTH_LONG).show()
        }
        alertDialog.setNegativeButton(
            "No"
        ) { _, _ ->}
        val alert: AlertDialog = alertDialog.create()
        alert.setCanceledOnTouchOutside(false)
        alert.show()
    }

    fun showAlertDialog(title: String, message: String, context: Context) {
        val alertDialog: AlertDialog.Builder = AlertDialog.Builder(context)
        alertDialog.setTitle(title)
        alertDialog.setMessage(message)
        alertDialog.setPositiveButton(
            "OK"
        ) { _, _ ->}

        val alert: AlertDialog = alertDialog.create()
        alert.setCanceledOnTouchOutside(false)
        alert.show()
    }
}