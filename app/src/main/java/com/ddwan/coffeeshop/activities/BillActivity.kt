package com.ddwan.coffeeshop.activities

import android.R.attr
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ddwan.coffeeshop.Application.Companion.firebaseDB
import com.ddwan.coffeeshop.R
import com.ddwan.coffeeshop.adapter.BillAdapter
import com.ddwan.coffeeshop.model.*
import com.ddwan.coffeeshop.viewmodel.MyViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_bill.*


class BillActivity : AppCompatActivity() {

    private val dialogLoad by lazy { LoadingDialog(this) }
    private var checkEmpty = true
    private var tableId = ""
    val model by lazy {
        ViewModelProvider(this).get(MyViewModel::class.java)
    }
    private var list = ArrayList<BillInfo>()
    private val adapter by lazy { BillAdapter(list, this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bill)
        checkEmpty = intent.getBooleanExtra("Status", true)
        tableId = intent.getStringExtra("TableID").toString()
        tvNameTable.text = intent.getStringExtra("TableName")
        if (checkEmpty) {
            emptyTable.visibility = View.VISIBLE
            textTotal.visibility = View.GONE
            txtTotal.visibility = View.GONE
            btnPay.visibility = View.GONE
        } else {
            initRecyclerView()
            loadDataTableFromDB()
        }
        image_add_bill.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            intent.putExtra("TableID", tableId)
            intent.putExtra("Status", checkEmpty)
            startActivityForResult(intent, 101)
            overridePendingTransition(R.anim.right_to_left,
                R.anim.right_to_left_out)
        }
        btnPrevious.setOnClickListener {
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode === 101 && resultCode === RESULT_OK) {
            checkEmpty = data?.getBooleanExtra("Status", true)!!
            if (!checkEmpty) {
                emptyTable.visibility = View.GONE
                textTotal.visibility = View.VISIBLE
                txtTotal.visibility = View.VISIBLE
                btnPay.visibility = View.VISIBLE
                initRecyclerView()
                loadDataTableFromDB()
            }
        }
    }

    private fun initRecyclerView() {
        recyclerView_Bill.layoutManager = LinearLayoutManager(this)
        recyclerView_Bill.setHasFixedSize(true)
        recyclerView_Bill.adapter = adapter
    }

    private fun loadDataTableFromDB() {
        list.clear()
        dialogLoad.startLoadingDialog()
        firebaseDB.reference.child("Bill")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (bill in snapshot.children) {
                            if (bill.child("Table_ID").value.toString() == tableId
                                && !(bill.child("Status").value as Boolean)
                            ) {
                                loadBillInfoFromBillID(bill.key.toString())
                                return
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    private fun loadBillInfoFromBillID(billID: String) {
        firebaseDB.reference.child("BillInfo")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                @SuppressLint("NotifyDataSetChanged")
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (billInfo in snapshot.children) {
                            if (billInfo.child("Bill_ID").value.toString() == billID) {
                                val foodID = billInfo.child("Food_ID").value.toString()
                                list.add(BillInfo(billInfo.key.toString(),
                                    billID,
                                    foodID,
                                    billInfo.child("Price").value.toString().toInt(),
                                    billInfo.child("Count").value.toString().toInt()))
                            }
                        }
                        adapter.notifyDataSetChanged()
                    }
                    dialogLoad.stopLoadingDialog()
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })

    }

}