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
import com.ddwan.coffeeshop.adapter.EmployeeAdapter
import com.ddwan.coffeeshop.adapter.FoodAdapter
import com.ddwan.coffeeshop.model.Account
import com.ddwan.coffeeshop.model.Food
import com.ddwan.coffeeshop.model.LoadingDialog
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
            try {
                Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.logo)
                    .into(view)
            } catch (e: Exception) {
            }
        else {
            firebaseStore.reference.child(id).downloadUrl.addOnSuccessListener { Uri ->
                try {
                    Glide.with(context)
                        .load(Uri.toString())
                        .placeholder(R.drawable.logo)
                        .into(view)
                } catch (e: Exception) {
                }
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

    fun returnBillInfo(
        foodId: String,
        price: Int,
        count: Int,
        billID: String,
    ): HashMap<String, Any> {
        val billInfo = HashMap<String, Any>()
        billInfo["Bill_ID"] = billID
        billInfo["Food_ID"] = foodId
        billInfo["Price"] = price
        billInfo["Count"] = count
        return billInfo
    }

    fun loadDataAccount(adapter: EmployeeAdapter?) {
        val listP = ArrayList<Account>()
        firebaseDB.getReference("Users")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (user in snapshot.children) {
                            val account = Account(user.key.toString(),
                                user.child("Email").value.toString(),
                                "",
                                user.child("Name").value.toString(),
                                user.child("Address").value.toString(),
                                user.child("Phone_Number").value.toString(),
                                user.child("Role").value.toString(),
                                user.child("Gender").value as Boolean,
                                "")
                            listP.add(account)
                        }
                        Application.listAccount.clear()
                        Application.listAccount.addAll(listP)
                        adapter?.notifyDataSetChanged()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    fun loadDataFood(
        dialogLoad: LoadingDialog?,
        tableID: String?,
        adapter: FoodAdapter?,
        adapterAddFood: FoodAdapter?,
    ) {
        Application.listFood.clear()
        dialogLoad?.startLoadingDialog()
        firebaseDB.getReference("Food")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (f in snapshot.children) {
                            val food = Food(f.key.toString(),
                                f.child("Name").value.toString(),
                                f.child("Category").value.toString(),
                                f.child("Price").value.toString().toInt(),
                                f.child("Description").value.toString(), "")
                            Application.listFood.add(food)
                        }
                        if (tableID == "null")
                            adapter?.notifyDataSetChanged()
                        else
                            adapterAddFood?.notifyDataSetChanged()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }
}