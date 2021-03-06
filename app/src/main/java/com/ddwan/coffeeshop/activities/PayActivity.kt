package com.ddwan.coffeeshop.activities

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ddwan.coffeeshop.Application
import com.ddwan.coffeeshop.Application.Companion.TYPE_DELETE
import com.ddwan.coffeeshop.Application.Companion.TYPE_MINUS
import com.ddwan.coffeeshop.Application.Companion.TYPE_PLUS
import com.ddwan.coffeeshop.Application.Companion.firebaseDB
import com.ddwan.coffeeshop.Application.Companion.numberFormatter
import com.ddwan.coffeeshop.R
import com.ddwan.coffeeshop.adapter.PayAdapter
import com.ddwan.coffeeshop.model.*
import com.ddwan.coffeeshop.viewmodel.MyViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_pay.*
import java.util.*
import kotlin.collections.ArrayList


class PayActivity : AppCompatActivity() {

    private val dialogLoad by lazy { LoadingDialog(this) }
    private var checkEmpty = true
    private var tableId = ""
    val model by lazy {
        ViewModelProvider(this).get(MyViewModel::class.java)
    }
    val listBillInfo = ArrayList<BillInfo>()
    private val adapter by lazy { PayAdapter(listBillInfo, this, true) }
    private var changeInfo = false
    private var listItemDelete = ArrayList<BillInfo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pay)
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
            if (changeInfo) {
                val builder = AlertDialog.Builder(this)
                builder.setMessage("B???n c?? mu???n l??u th??ng tin ???? thay ?????i kh??ng ?")
                    .setPositiveButton("C??") { _, _ ->
                        updateDB(false)
                    }
                    .setNegativeButton("Kh??ng") { _, _ ->
                        finish()
                        overridePendingTransition(R.anim.left_to_right,
                            R.anim.left_to_right_out)
                    }.show()
            } else {
                finish()
                overridePendingTransition(R.anim.left_to_right,
                    R.anim.left_to_right_out)
            }
        }
        btnPay.setOnClickListener {
            updateDB(true)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101 && resultCode == RESULT_OK) {
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

    @SuppressLint("SetTextI18n")
    private fun initRecyclerView() {
        adapter.setCallBack { position, type ->
            when (type) {
                TYPE_MINUS, TYPE_PLUS -> {
                    returnReceipt(listBillInfo)
                    changeInfo = true
                }
                TYPE_DELETE -> {
                    val builder = AlertDialog.Builder(this)
                    builder.setMessage("B???n c?? ch???c mu???n x??a ?")
                        .setPositiveButton("C??") { _, _ ->
                            listItemDelete.add(listBillInfo[position])
                            listBillInfo.removeAt(position)
                            adapter.notifyItemRemoved(position)
                            returnReceipt(listBillInfo)
                            changeInfo = true
                            if (listBillInfo.size == 0) {
                                checkEmpty = true
                                emptyTable.visibility = View.VISIBLE
                                textTotal.visibility = View.GONE
                                txtTotal.visibility = View.GONE
                                btnPay.visibility = View.GONE
                            }
                        }
                        .setNegativeButton("Kh??ng") { _, _ -> }.show()
                }
            }
        }
        recyclerView_Bill.visibility = View.INVISIBLE
        adapter.setCallBackLoad {
            recyclerView_Bill.visibility = View.VISIBLE
            dialogLoad.stopLoadingDialog()
        }
        recyclerView_Bill.layoutManager = LinearLayoutManager(this)
        recyclerView_Bill.setHasFixedSize(true)
        recyclerView_Bill.adapter = adapter
    }


    private fun loadDataTableFromDB() {
        listBillInfo.clear()
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
                                listBillInfo.add(BillInfo(billInfo.key.toString(),
                                    billID,
                                    foodID,
                                    billInfo.child("Price").value.toString().toInt(),
                                    billInfo.child("Count").value.toString().toInt()))
                            }
                        }
                        returnReceipt(listBillInfo)
                        adapter.notifyDataSetChanged()
                    } else
                        dialogLoad.stopLoadingDialog()
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
        txtTotal.text = numberFormatter.format(total)
    }

    private fun updateDB(pay: Boolean) {
        if (changeInfo) {
            for (item in listBillInfo) {
                firebaseDB.reference.child("BillInfo").child(item.billInfoId)
                    .updateChildren(model.returnBillInfo(item.foodId,
                        item.price,
                        item.count,
                        item.billId))
            }
            for (item in listItemDelete) {
                firebaseDB.reference.child("BillInfo").child(item.billInfoId)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                snapshot.ref.removeValue()
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {

                        }
                    })
            }
            if (listBillInfo.size == 0) {
                updateTableToEmpty(listItemDelete[0].billId, true)
            } else {
                finish()
                overridePendingTransition(R.anim.left_to_right,
                    R.anim.left_to_right_out)
            }
        }

        if (pay) {
            firebaseDB.reference.child("Bill").child(listBillInfo[0].billId).child("Date_Check_Out")
                .setValue(Application.sdf.format(Calendar.getInstance().time))
            firebaseDB.reference.child("Bill").child(listBillInfo[0].billId).child("Status")
                .setValue(true)
            updateTableToEmpty(listBillInfo[0].billId, false)
        }
    }

    private fun updateTableToEmpty(billID: String, deleteBill: Boolean) {
        firebaseDB.reference.child("Bill").child(billID)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    firebaseDB.reference.child("Table")
                        .child(snapshot.child("Table_ID").value.toString()).child("Status")
                        .setValue(true)
                    firebaseDB.reference.child("Table")
                        .child(snapshot.child("Table_ID").value.toString()).child("Description")
                        .setValue("Tr???ng").addOnCompleteListener {
                            Toast.makeText(this@PayActivity,
                                "Th???c hi???n thao t??c th??nh c??ng",
                                Toast.LENGTH_SHORT).show()
                            finish()
                            overridePendingTransition(R.anim.left_to_right,
                                R.anim.left_to_right_out)
                        }
                    if (deleteBill) {
                        snapshot.ref.removeValue()
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