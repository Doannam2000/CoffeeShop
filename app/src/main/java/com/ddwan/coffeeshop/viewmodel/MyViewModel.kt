package com.ddwan.coffeeshop.viewmodel

import android.content.Context
import android.widget.ImageView
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide
import com.ddwan.coffeeshop.Application
import com.ddwan.coffeeshop.Application.Companion.firebaseDB
import com.ddwan.coffeeshop.Application.Companion.firebaseStore
import com.ddwan.coffeeshop.R
import com.ddwan.coffeeshop.adapter.EmployeeAdapter
import com.ddwan.coffeeshop.adapter.FoodAdapter
import com.ddwan.coffeeshop.model.Category
import com.ddwan.coffeeshop.model.Users
import com.ddwan.coffeeshop.model.Food
import com.ddwan.coffeeshop.model.LoadingDialog
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
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
        val listP = ArrayList<Users>()
        firebaseDB.getReference("Users")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (user in snapshot.children) {
                            val account = Users(user.key.toString(),
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
                                f.child("Category_ID").value.toString(),
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

    fun loadDataCategory() {
        Application.listCategory.clear()
        firebaseDB.getReference("Category")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (tb in snapshot.children) {
                            val category = Category(
                                tb.key.toString(),
                                tb.child("Category_Name").value.toString()
                            )
                            Application.listCategory.add(category)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

}