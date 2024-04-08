package com.example.juiceshop.utils

import android.content.Context
import android.view.View
import androidx.appcompat.app.AlertDialog

object PopUpManager {

    fun showPopUp(context: Context, message: String, text1: String, text2: String?, onClickListener1: View.OnClickListener?, onClickListener2: View.OnClickListener?) {
        val alertDialogBuilder = AlertDialog.Builder(context)

//        alertDialogBuilder.setTitle(message)
        alertDialogBuilder.setMessage(message)
        alertDialogBuilder.setPositiveButton(text1) { dialog, _ ->
            onClickListener1
            dialog.dismiss()
        }
        alertDialogBuilder.setNegativeButton(text2) { dialog, _ ->
            onClickListener2
            dialog.dismiss()
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    fun showPopUp(context: Context, message: String, text1: String, onClickListener1: View.OnClickListener?) {
        val alertDialogBuilder = AlertDialog.Builder(context)

//        alertDialogBuilder.setTitle(message)
        alertDialogBuilder.setMessage(message)
        alertDialogBuilder.setPositiveButton(text1) { dialog, _ ->
            onClickListener1
            dialog.dismiss()
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }
}