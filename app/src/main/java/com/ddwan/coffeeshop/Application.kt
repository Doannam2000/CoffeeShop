package com.ddwan.coffeeshop
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class Application {
    companion object{
        val mAuth = FirebaseAuth.getInstance()
        val firebaseDB = FirebaseDatabase.getInstance()
    }
}