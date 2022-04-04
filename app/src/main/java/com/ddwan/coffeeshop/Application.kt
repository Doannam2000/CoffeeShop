package com.ddwan.coffeeshop
import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.ddwan.coffeeshop.model.Account
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class Application {
    companion object{
        val mAuth = FirebaseAuth.getInstance()
        val firebaseDB = FirebaseDatabase.getInstance()
        val firebaseStore = FirebaseStorage.getInstance()
        val accountLogin = Account()
    }
}