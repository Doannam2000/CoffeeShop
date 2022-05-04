package com.ddwan.coffeeshop.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.ddwan.coffeeshop.Application.Companion.firebaseDB
import com.ddwan.coffeeshop.Application.Companion.listBill
import com.ddwan.coffeeshop.Application.Companion.sdf
import com.ddwan.coffeeshop.Application.Companion.sdfDay
import com.ddwan.coffeeshop.R
import com.ddwan.coffeeshop.adapter.BillAdapter
import com.ddwan.coffeeshop.model.Bill
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_bill.*

class BillActivity : AppCompatActivity() {


    private val adapter by lazy { BillAdapter(listBill) }
    var date = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bill)
        date = intent.extras!!.getString("Date").toString()
        txtDay.text = date
        btnPrevious.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.left_to_right,
                R.anim.left_to_right_out)
        }
        recyclerBill.layoutManager = LinearLayoutManager(this)
        recyclerBill.setHasFixedSize(true)
        recyclerBill.adapter = adapter
        loadData()
    }

    private fun loadData() {
        firebaseDB.reference.child("Bill")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (item in snapshot.children.reversed()) {
                            if ((item.child("Status").value as Boolean)) {
                                val string = item.child("Date_Check_Out").value.toString()
                                val time = sdf.parse(string)
                                if (sdfDay.format(time).equals(date)) {
                                    listBill.add(Bill(item.key.toString(),
                                        item.child("Date_Check_In").value.toString(),
                                        item.child("Date_Check_Out").value.toString(),
                                        item.child("Table_ID").value.toString(),
                                        item.child("Status").value as Boolean))
                                }
                            }
                        }
                        adapter.notifyDataSetChanged()
                        if (listBill.isEmpty()) {
                            emptyBill.visibility = View.VISIBLE
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    override fun onBackPressed() {
        finish()
        overridePendingTransition(R.anim.left_to_right,
            R.anim.left_to_right_out)
        super.onBackPressed()
    }
}