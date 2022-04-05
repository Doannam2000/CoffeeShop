package com.ddwan.coffeeshop.viewmodel

import android.content.Context
import android.view.View
import android.widget.ImageView
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide
import com.ddwan.coffeeshop.Application
import com.ddwan.coffeeshop.Application.Companion.firebaseDB
import com.ddwan.coffeeshop.Application.Companion.firebaseStore
import com.ddwan.coffeeshop.R
import com.ddwan.coffeeshop.model.Account
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login_screen.*
import kotlin.random.Random

class MyViewModel : ViewModel() {

    fun loadImage(context: Context, imageUrl: String, id: String, view: ImageView) {
        if (imageUrl.trim() != "")
            Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.logo)
                .into(view)
        else {
            try {
                firebaseStore.reference.child(id).downloadUrl.addOnSuccessListener { Uri ->
                    Glide.with(context)
                        .load(Uri.toString())
                        .placeholder(R.drawable.logo)
                        .into(view)
                }
            } catch (e: Exception) {
            }

        }
    }

    fun randomID(): String {
        var k = ""
        for (i in 0..12) {
            k += Random.nextInt(1, 100).toString()
        }
        return k
    }

}