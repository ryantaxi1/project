package com.taxi1.utils

import android.content.DialogInterface


interface CustomDialogInterface {
    fun positiveClick(dialog: DialogInterface)
    fun negativeClick(dialog: DialogInterface)
}