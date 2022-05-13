package com.ddwan.coffeeshop
import com.ddwan.coffeeshop.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class Application {
    companion object{
        val sdf = SimpleDateFormat("HH:mm:ss dd/MM/yyyy")
        val sdfDay = SimpleDateFormat("dd/MM/yyyy")
        val sdfMonth = SimpleDateFormat("MM/yyyy")
        val mAuth = FirebaseAuth.getInstance()
        val firebaseDB = FirebaseDatabase.getInstance()
        val firebaseStore = FirebaseStorage.getInstance()
        val accountLogin = Users()
        const val TYPE_PLUS = 1
        const val TYPE_MINUS = 2
        const val TYPE_DELETE = 3
        var numberFormatter: NumberFormat = NumberFormat.getCurrencyInstance()
        val listAccount = ArrayList<Users>()
        val listFood = ArrayList<Food>()
        val listEmpty = ArrayList<Table>()
        val listLiveTable = ArrayList<Table>()
        val listCategory = ArrayList<Category>()
    }
    init {
        numberFormatter.maximumFractionDigits = 0
        numberFormatter.currency = Currency.getInstance("VND")
    }

}