package com.ddwan.coffeeshop.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ddwan.coffeeshop.Application.Companion.accountLogin
import com.ddwan.coffeeshop.Application.Companion.firebaseDB
import com.ddwan.coffeeshop.Application.Companion.listFood
import com.ddwan.coffeeshop.Application.Companion.sdf
import com.ddwan.coffeeshop.R
import com.ddwan.coffeeshop.adapter.FoodAdapter
import com.ddwan.coffeeshop.model.Food
import com.ddwan.coffeeshop.model.LoadingDialog
import com.ddwan.coffeeshop.viewmodel.MyViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_menu.*
import kotlinx.android.synthetic.main.activity_menu.btnPrevious
import kotlinx.android.synthetic.main.custom_add_food.view.*
import kotlinx.android.synthetic.main.custom_editext_dialog.view.cancel
import kotlinx.android.synthetic.main.custom_editext_dialog.view.oke
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class MenuActivity : AppCompatActivity() {

    private val adapter by lazy { FoodAdapter(listFood, this, true) }
    private val adapterAddFood by lazy { FoodAdapter(listFood, this, false) }
    private val dialogLoad by lazy { LoadingDialog(this) }
    val model by lazy {
        ViewModelProvider(this).get(MyViewModel::class.java)
    }
    var tableID = ""
    var checkEmpty = true
    var listP = ArrayList<Food>()
    val handle = Handler()
    val run = Runnable {
        val text = searchView.text
        listFood.clear()
        for (item in listP) {
            if (item.foodName.uppercase().contains(text.toString().uppercase())) {
                listFood.add(item)
            }
        }
        if (tableID == "null")
            adapter.notifyDataSetChanged()
        else
            adapterAddFood.notifyDataSetChanged()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        tableID = intent.getStringExtra("TableID").toString()
        checkEmpty = intent.getBooleanExtra("Status", true)
        if (tableID != "null") {
            addFood.visibility = View.GONE
            Log.d("checkkkkk", tableID)
        } else {
            addFood.setOnClickListener {
                val popupMenu = PopupMenu(this, it)
                popupMenu.menuInflater.inflate(R.menu.add_food_or_category, popupMenu.menu)
                popupMenu.setOnMenuItemClickListener { i ->
                    selectedItemMenu(i.itemId)
                    false
                }
                popupMenu.show()
            }
        }
        initRecycler()
        btnPrevious.setOnClickListener {
            backActivity()
        }
        listP.addAll(listFood)
        searchView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                handle.removeCallbacks(run)
                handle.postDelayed(run, 1000)
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })
    }

    private fun backActivity() {
        if (tableID != "null") {
            val returnIntent = Intent()
            returnIntent.putExtra("Status", checkEmpty)
            setResult(Activity.RESULT_OK, returnIntent)
            finish()
            overridePendingTransition(R.anim.left_to_right,
                R.anim.left_to_right_out)
        } else {
            finish()
            overridePendingTransition(R.anim.left_to_right,
                R.anim.left_to_right_out)
        }
    }

    private fun setupAdapter() {
        adapter.setCallBack {
            val bundle = Bundle()
            bundle.putSerializable("food", listFood[it])
            bundle.putBoolean("check", true)
            val intent = Intent(this, FoodActivity::class.java)
            intent.putExtras(bundle)
            startActivity(intent)
            overridePendingTransition(R.anim.right_to_left,
                R.anim.right_to_left_out)
        }
        adapter.setCallBack2 {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("Bạn có chắc muốn xóa ${listFood[it].foodName} ?")
                .setPositiveButton("Có") { _, _ ->
                    deleteFood(listFood[it].foodId, it)
                }
                .setNegativeButton("Không") { _, _ -> }.show()
        }
        adapter.setCallBackLoad {
            dialogLoad.stopLoadingDialog()
        }
    }

    private fun initRecycler() {
        if (tableID == "null") {
            setupAdapter()
            recyclerViewFood.adapter = adapter
        } else {
            adapterAddFood.setCallBack {
                createDialogAddFood(listFood[it])
            }
            adapterAddFood.setCallBackLoad {
                dialogLoad.stopLoadingDialog()
            }
            recyclerViewFood.adapter = adapterAddFood
        }
        recyclerViewFood.layoutManager = LinearLayoutManager(this)
        recyclerViewFood.setHasFixedSize(true)
    }

    private fun deleteFood(id: String, it: Int) {
        firebaseDB.reference.child("Food").child(id)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        snapshot.ref.removeValue().addOnCompleteListener { i ->
                            if (i.isSuccessful) {
                                FirebaseStorage.getInstance().reference.child(id).delete()
                                listFood.removeAt(it)
                                adapter.notifyItemRemoved(it)
                                Toast.makeText(this@MenuActivity,
                                    "Xoá thành công !",
                                    Toast.LENGTH_SHORT)
                                    .show()
                            } else
                                Toast.makeText(this@MenuActivity,
                                    "Không thể xóa !",
                                    Toast.LENGTH_SHORT)
                                    .show()
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    private fun selectedItemMenu(id: Int) {
        when (id) {
            R.id.itemAddFood -> {
                startActivity(Intent(this, FoodActivity::class.java))
                overridePendingTransition(R.anim.right_to_left,
                    R.anim.right_to_left_out)
            }
            R.id.itemAddCategory -> {
                startActivity(Intent(this, CategoryActivity::class.java))
                overridePendingTransition(R.anim.right_to_left,
                    R.anim.right_to_left_out)
            }
        }
    }

    private fun createDialogAddFood(food: Food) {
        val viewDialog = View.inflate(this, R.layout.custom_add_food, null)
        val builder = AlertDialog.Builder(this)
        builder.setView(viewDialog)
        val dialog = builder.create()
        dialog.show()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        viewDialog.cancel.setOnClickListener {
            dialog.dismiss()
        }
        viewDialog.oke.setOnClickListener {
            dialog.dismiss()
            if (viewDialog.edit_count.text.isEmpty()) {
                Toast.makeText(this,
                    "Số lượng không được để trống",
                    Toast.LENGTH_SHORT).show()
            } else {
                dialogLoad.startLoadingDialog()
                addFoodToBill(food, viewDialog.edit_count.text.toString().toInt())
            }
        }
    }

    private fun addFoodToBill(food: Food, count: Int) {
        if (checkEmpty) {
            val hashMap = HashMap<String, Any>()
            hashMap["Date_Check_In"] = sdf.format(Calendar.getInstance().time)
            hashMap["Date_Check_Out"] = "NONE"
            hashMap["Table_ID"] = tableID
            hashMap["User_ID"] = accountLogin.userId
            hashMap["Status"] = false
            val billID = model.randomID()
            firebaseDB.reference.child("Bill").child(billID).updateChildren(hashMap)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        firebaseDB.reference.child("Table").child(tableID).child("Status")
                            .setValue(false)
                        firebaseDB.reference.child("Table").child(tableID).child("Description")
                            .setValue("Chưa Thanh Toán")
                        addBillInfo(model.returnBillInfo(food.foodId, food.price, count, billID),
                            model.randomID())
                    } else {
                        Toast.makeText(this,
                            "Thêm thành món ăn không thành công ",
                            Toast.LENGTH_SHORT).show()
                        dialogLoad.stopLoadingDialog()
                    }
                }
        } else {
            addBillInfoWithTableID(food, count)
        }
    }


    private fun addBillInfoWithTableID(food: Food, count: Int) {
        firebaseDB.reference.child("Bill")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (bill in snapshot.children) {
                            if (bill.child("Table_ID").value.toString() == tableID
                                && !(bill.child("Status").value as Boolean)
                            ) {
                                checkFoodExists(food, bill.key.toString(), count)
                                return
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    private fun addBillInfo(hashMap: HashMap<String, Any>, id: String) {
        firebaseDB.reference.child("BillInfo").child(id).updateChildren(hashMap)
            .addOnCompleteListener {
                dialogLoad.stopLoadingDialog()
                if (it.isSuccessful) {
                    checkEmpty = false
                    Toast.makeText(this, "Thêm thành món ăn thành công ", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun checkFoodExists(food: Food, billID: String, count: Int) {
        firebaseDB.reference.child("BillInfo")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        var count1 = 0
                        var billInfoID = ""
                        for (billInfo in snapshot.children) {
                            if (billInfo.child("Bill_ID").value.toString() == billID &&
                                billInfo.child("Food_ID").value.toString() == food.foodId
                            ) {
                                count1 = billInfo.child("Count").value.toString().toInt()
                                billInfoID = billInfo.key.toString()
                                break
                            }
                        }
                        if (count1 == 0) {
                            addBillInfo(model.returnBillInfo(food.foodId,
                                food.price,
                                count,
                                billID), model.randomID())
                        } else {
                            addBillInfo(model.returnBillInfo(food.foodId,
                                food.price,
                                count + count1,
                                billID), billInfoID)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    override fun onBackPressed() {
        backActivity()
        super.onBackPressed()
    }

    override fun onResume() {
        super.onResume()
        model.loadDataFood(dialogLoad, tableID, adapter, adapterAddFood)
    }

}