package com.mqtt

import android.content.Context
import android.view.Gravity
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*

fun getStringFromDate(date: Date = Date(), pattern:String = "dd.MM.yyyy - hh:mm:ss"): String {
    return SimpleDateFormat(pattern).format(date)
}

fun printToast(context: Context, msg:String)
{
    Toast.makeText(context,msg, Toast.LENGTH_LONG).apply {
        setGravity(Gravity.CENTER,0,0)
    }.show()
}