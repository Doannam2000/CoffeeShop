package com.ddwan.coffeeshop
import android.annotation.SuppressLint
import com.ddwan.coffeeshop.model.Account
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat

class Application {
    companion object{
        @SuppressLint("SimpleDateFormat")
        val sdf = SimpleDateFormat("HH:mm:ss dd/MM/yyyy")
        val mAuth = FirebaseAuth.getInstance()
        val firebaseDB = FirebaseDatabase.getInstance()
        val firebaseStore = FirebaseStorage.getInstance()
        val accountLogin = Account()
    }

}