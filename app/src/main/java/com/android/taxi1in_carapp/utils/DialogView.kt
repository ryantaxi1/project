package com.taxi1.utils

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import com.android.taxi1in_carapp.databinding.DialogViewBinding

class  DialogView(val context: Context) {
    private var dialog: AlertDialog? = null
    var isListenerInvoked = false
    var dialogView: DialogViewBinding = DialogViewBinding.inflate(LayoutInflater.from(context), null, false)

    fun setTitle(text: String) {
        dialogView.tvTitle.text = text
    }

    fun setMessage(text: String) {
        dialogView.tvMessage?.text = text
    }

    fun show() {
        if(dialog!=null && dialog!!.isShowing) dialog?.dismiss()
        dialog = AlertDialog.Builder(context)
            .setView(dialogView.root).create()
        dialog?.setCancelable(false)
        dialog?.setOnDismissListener {
            if(!isListenerInvoked) {
                try {
                    buttonListener?.onNegativeButtonClick(dialog!!)
                } catch (e: Exception) {
                }
            }
        }
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        try {
            dialog?.show()
            isListenerInvoked = false
        } catch (e: Exception) {
            dialog = null
        }
    }

    var buttonListener : ButtonListener? = null
    fun setListener(listener: ButtonListener) {
        this@DialogView.buttonListener = listener
        dialogView.tvLater.setOnClickListener {
            isListenerInvoked = true
            dialog?.let { it1 -> listener.onNegativeButtonClick(it1) }
        }
        dialogView.tvDialogButton.setOnClickListener {
            isListenerInvoked = true
            dialog?.let { it1 -> listener.onPositiveButtonClick(it1) }
        }
    }

    fun setPositiveButtonText(text: String) {
        dialogView.tvDialogButton.text = text
    }

    fun setNegativeButtonText(text: String) {
        dialogView.tvLater.text = text
        if(text.isEmpty())
            dialogView.tvLater.visibility =  GONE
        else
            dialogView.tvLater.visibility = VISIBLE
    }

    fun onDestroyView() {
        if(dialog?.isShowing == true) dialog?.dismiss()
    }

    fun isShowing():Boolean {
        return dialog?.isShowing ?: false
    }

    interface ButtonListener {
        fun onPositiveButtonClick(dialog: AlertDialog)
        fun onNegativeButtonClick(dialog: AlertDialog)
    }
}