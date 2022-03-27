package com.ddwan.coffeeshop.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ddwan.coffeeshop.R
import com.ddwan.coffeeshop.adapter.BillAdapter
import com.ddwan.coffeeshop.model.BillInfo
import kotlinx.android.synthetic.main.activity_bill.*

class BillActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bill)
        val list = arrayListOf<BillInfo>(BillInfo(0, 0, 0, 10000, 2),
            BillInfo(1, 1, 1, 20000, 1),
            BillInfo(2, 2, 2, 30000, 2),
            BillInfo(3, 3, 3, 40000, 4),
            BillInfo(4, 4, 4, 50000, 5),
            BillInfo(5, 5, 5, 60000, 6),
            BillInfo(0, 0, 0, 10000, 2),
            BillInfo(1, 1, 1, 20000, 1),
            BillInfo(2, 2, 2, 30000, 2),
            BillInfo(3, 3, 3, 40000, 4),
            BillInfo(4, 4, 4, 50000, 5),
            BillInfo(5, 5, 5, 60000, 6))

        val adapter = BillAdapter(list)
        recyclerView_Bill.layoutManager = LinearLayoutManager(this)
        recyclerView_Bill.setHasFixedSize(true)
        recyclerView_Bill.adapter = adapter
        btnPrevious.setOnClickListener {
            finish()
        }
    }
}