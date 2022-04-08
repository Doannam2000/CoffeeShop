package com.ddwan.coffeeshop
import android.annotation.SuppressLint
import com.ddwan.coffeeshop.model.Account
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.text.DecimalFormat
import java.text.SimpleDateFormat

class Application {
    companion object{
        @SuppressLint("SimpleDateFormat")
        val sdf = SimpleDateFormat("HH:mm:ss dd/MM/yyyy")
        val mAuth = FirebaseAuth.getInstance()
        val firebaseDB = FirebaseDatabase.getInstance()
        val firebaseStore = FirebaseStorage.getInstance()
        val accountLogin = Account()
        const val TYPE_PLUS = 1
        const val TYPE_MINUS = 2
        const val TYPE_DELETE = 3
        val numberFormatter = DecimalFormat("#,###")
    }

}