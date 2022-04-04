package com.ddwan.coffeeshop.viewmodel

import androidx.lifecycle.ViewModel
import com.ddwan.coffeeshop.model.Account
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class MyViewModel : ViewModel() {
    var userLogin = Account("", "", "", "", "", "", "", true, "")
    fun loadImage(){

    }
}