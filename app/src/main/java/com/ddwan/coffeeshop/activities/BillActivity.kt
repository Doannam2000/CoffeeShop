package com.ddwan.coffeeshop.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ddwan.coffeeshop.R
import com.ddwan.coffeeshop.adapter.BillAdapter
import com.ddwan.coffeeshop.model.Bill
import com.ddwan.coffeeshop.model.BillInfo
import com.ddwan.coffeeshop.model.LoadingDialog
import com.ddwan.coffeeshop.viewmodel.MyViewModel
import kotlinx.android.synthetic.main.activity_bill.*

class BillActivity : AppCompatActivity() {

    private val dialog by lazy { LoadingDialog(this) }
    private var checkEmpty = false
    private var tableId = ""
    val model by lazy {
        ViewModelProvider(this).get(MyViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bill)
        checkEmpty = intent.getBooleanExtra("Status", true)
        tableId = intent.getStringExtra("TableID").toString()
        if (checkEmpty) {
            emptyTable.visibility = View.VISIBLE
            textTotal.visibility = View.GONE
            txtTotal.visibility = View.GONE
            btnPay.visibility = View.GONE
        } else {
            initRecyclerView()
        }
        btnPrevious.setOnClickListener {
            finish()
        }
        image_add_bill.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            intent.putExtra("TableID",tableId)
            intent.putExtra("Status",checkEmpty)
            startActivity(intent)
            overridePendingTransition(R.anim.right_to_left,
                R.anim.right_to_left_out)
        }
    }

    private fun initRecyclerView() {
        val list = ArrayList<BillInfo>()
        val adapter = BillAdapter(list)
        recyclerView_Bill.layoutManager = LinearLayoutManager(this)
        recyclerView_Bill.setHasFixedSize(true)
        recyclerView_Bill.adapter = adapter
    }
}