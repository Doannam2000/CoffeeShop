package com.ddwan.coffeeshop.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.ddwan.coffeeshop.Application
import com.ddwan.coffeeshop.R
import com.ddwan.coffeeshop.activities.BillActivity
import com.ddwan.coffeeshop.model.Bill
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class BillAdapter(var list: ArrayList<Bill>) : RecyclerView.Adapter<BillAdapter.ViewHolder>() {

    lateinit var itemClick: (position: Int) -> Unit
    fun setCallBack(click: (position: Int) -> Unit) {
        itemClick = click
    }

    lateinit var loadFinish: () -> Unit
    fun setCallBackLoad(click: () -> Unit) {
        loadFinish = click
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillAdapter.ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_in_bill, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: BillAdapter.ViewHolder, position: Int) {
        holder.setData()
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var layout: CardView = itemView.findViewById(R.id.layoutBill)
        var billID: TextView = itemView.findViewById(R.id.billId)
        var tableName: TextView = itemView.findViewById(R.id.nameTableInBill)
        var price: TextView = itemView.findViewById(R.id.priceOneBill)
        var pay: TextView = itemView.findViewById(R.id.checkPay)

        @SuppressLint("SetTextI18n")
        fun setData() {
            billID.text = "Mã: ${list[adapterPosition].billId}"
            getNameTableFromID(tableName, list[adapterPosition].tableId)
            pay.text = if (list[adapterPosition].status) "Đã Thanh Toán" else "Chưa Thanh Toán"
            if (adapterPosition == list.size - 1)
                returnTheMoneyOfOneBill(list[adapterPosition].billId, price,true)
            else
                returnTheMoneyOfOneBill(list[adapterPosition].billId, price,false)
        }

        init {
            layout.setOnClickListener { itemClick.invoke(adapterPosition) }
        }
    }

    fun getNameTableFromID(view: TextView, id: String) {
        Application.firebaseDB.reference.child("Table").child(id)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        view.text = snapshot.child("Name").value.toString()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    fun returnTheMoneyOfOneBill(billID: String, view: TextView, check: Boolean) {
        Application.firebaseDB.reference.child("BillInfo")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        var price = 0
                        for (item in snapshot.children) {
                            if (item.child("Bill_ID").value.toString() == billID) {
                                price += item.child("Price").value.toString()
                                    .toInt() * item.child("Count").value.toString().toInt()
                            }
                        }
                        view.text = Application.numberFormatter.format(price)
                        if (check)
                            loadFinish.invoke()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
    }
}