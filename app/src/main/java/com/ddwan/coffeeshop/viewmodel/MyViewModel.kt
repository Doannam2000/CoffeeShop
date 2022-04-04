package com.ddwan.coffeeshop.viewmodel

import android.content.Context
import android.widget.ImageView
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide
import com.ddwan.coffeeshop.Application
import com.ddwan.coffeeshop.R
import com.ddwan.coffeeshop.model.Account
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class MyViewModel : ViewModel() {

    fun loadImage(context: Context, acc: Account, view: ImageView) {
        if (acc.imageUrl.trim() != "")
            Glide.with(context)
                .load(acc.imageUrl)
                .placeholder(R.drawable.logo)
                .into(view)
        else {
            try {
                Application.firebaseStore.reference.child(acc.id).downloadUrl.addOnSuccessListener { Uri ->
                    Glide.with(context)
                        .load(Uri.toString())
                        .placeholder(R.drawable.logo)
                        .into(view)
                    acc.imageUrl = Uri.toString()
                }
            } catch (e: Exception) {
            }

        }
    }
}