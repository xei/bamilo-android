package com.bamilo.android.appmodule.bamiloapp.utils.dialogfragments

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.bamilo.android.R


/**
 * Created by Farshid
 * since 10/8/2018.
 * contact farshidabazari@gmail.com
 */

class SSLErrorAlertDialog(var context: Context) {
    @SuppressLint("InflateParams")
    fun show(title: String?, message: String?, positiveButton: View.OnClickListener, cancelButton: View.OnClickListener) {

        val dialogBuilder = AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context)
        val dialogView = inflater.inflate(R.layout.dialog_ssl_error, null)

        dialogBuilder.setView(dialogView)

        val titleTextView = dialogView.findViewById(R.id.tvDialogTitle) as TextView
        val messageTextView = dialogView.findViewById(R.id.tvDialogMessage) as TextView

        title?.let { titleTextView.text = it }
        message?.let { messageTextView.text = it }

        var alertDialog: AlertDialog? = null
        dialogView.findViewById<View>(R.id.tvAccept).setOnClickListener {
            alertDialog?.dismiss()
            positiveButton.onClick(dialogView)
        }
        dialogView.findViewById<View>(R.id.tvCancel).setOnClickListener {
            alertDialog?.dismiss()
            cancelButton.onClick(dialogView)
        }

        alertDialog = dialogBuilder.create()
        alertDialog.show()
    }
}