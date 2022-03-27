package com.ddwan.coffeeshop.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ddwan.coffeeshop.R
import com.ddwan.coffeeshop.model.BillInfo

class BillAdapter(var list:ArrayList<BillInfo>):RecyclerView.Adapter<BillAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillAdapter.ViewHolder {
        var view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_bill_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: BillAdapter.ViewHolder, position: Int) {
        holder.setData()
    }

    override fun getItemCount(): Int {
        return list.size
    }
    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        private var image: ImageView = itemView.findViewById(R.id.imageFood)
        private var name: TextView = itemView.findViewById(R.id.nameFood)
        private var price: TextView = itemView.findViewById(R.id.price)
        private var count: EditText = itemView.findViewById(R.id.countFood)
        @SuppressLint("SetTextI18n")
        fun setData() {
            name.text = list[adapterPosition].foodId.toString()
            count.setText(list[adapterPosition].count.toString())
            price.text = (list[adapterPosition].price*list[adapterPosition].count).toString() + "đ"
        }
    }
}