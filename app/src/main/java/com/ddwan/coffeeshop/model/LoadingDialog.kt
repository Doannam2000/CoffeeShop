package com.ddwan.coffeeshop.model

import android.app.Activity
import android.app.AlertDialog
import com.ddwan.coffeeshop.R

class LoadingDialog(activity: Activity) {
    private var activity: Activity = activity
    lateinit var dialog: AlertDialog
    fun startLoadingDialog(){
        val builder = AlertDialog.Builder(activity)
        val inflate = activity.layoutInflater
        builder.setView(inflate.inflate(R.layout.custom_dialog,null))
        builder.setCancelable(true)
        dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
    }
    fun stopLoadingDialog(){
        dialog.dismiss()
    }
}