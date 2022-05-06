package com.ddwan.coffeeshop.activities

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.ddwan.coffeeshop.Application
import com.ddwan.coffeeshop.Application.Companion.firebaseDB
import com.ddwan.coffeeshop.R
import com.ddwan.coffeeshop.adapter.PayAdapter
import com.ddwan.coffeeshop.model.Bill
import com.ddwan.coffeeshop.model.BillInfo
import com.ddwan.coffeeshop.model.LoadingDialog
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_bill_info.*

class BillInfoActivity : AppCompatActivity() {

    private val listBillInfo = java.util.ArrayList<BillInfo>()
    private val dialogLoad by lazy { LoadingDialog(this) }
    private val adapter by lazy { PayAdapter(listBillInfo, this, false) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bill_info)
        val bundle = intent.extras
        val bill = bundle!!.getSerializable("Bill") as Bill
        adapter.setCallBackLoad {
            dialogLoad.stopLoadingDialog()
        }
        recyclerViewFood.layoutManager = LinearLayoutManager(this)
        recyclerViewFood.setHasFixedSize(true)
        recyclerViewFood.adapter = adapter
        loadBillInfoFromBillID(bill.billId)
        btnPrevious.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.left_to_right,
                R.anim.left_to_right_out)
        }
    }

    private fun loadBillInfoFromBillID(billID: String) {
        dialogLoad.startLoadingDialog()
        firebaseDB.reference.child("BillInfo")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                @SuppressLint("NotifyDataSetChanged")
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (billInfo in snapshot.children) {
                            if (billInfo.child("Bill_ID").value.toString() == billID) {
                                val foodID = billInfo.child("Food_ID").value.toString()
                                listBillInfo.add(BillInfo(billInfo.key.toString(),
                                    billID,
                                    foodID,
                                    billInfo.child("Price").value.toString().toInt(),
                                    billInfo.child("Count").value.toString().toInt()))
                            }
                        }
                        returnReceipt(listBillInfo)
                        adapter.notifyDataSetChanged()
                    } else {
                        dialogLoad.stopLoadingDialog()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })

    }

    @SuppressLint("SetTextI18n")
    fun returnReceipt(list: ArrayList<BillInfo>) {
        var total = 0
        for (item in list) {
            total += item.count * item.price
        }
        txtTotal.text = Application.numberFormatter.format(total)
    }
}