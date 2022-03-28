package com.ddwan.coffeeshop.adapter

import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ddwan.coffeeshop.R
import com.ddwan.coffeeshop.model.BillInfo

class BillAdapter(var list: ArrayList<BillInfo>) : RecyclerView.Adapter<BillAdapter.ViewHolder>() {
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

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var image: ImageView = itemView.findViewById(R.id.imageFood)
        private var name: TextView = itemView.findViewById(R.id.nameFood)
        private var price: TextView = itemView.findViewById(R.id.price)
        private var count: EditText = itemView.findViewById(R.id.countFood)
        private var imagePlus: ImageView = itemView.findViewById(R.id.add)
        private var imageMinus: ImageView = itemView.findViewById(R.id.minus)

        @SuppressLint("SetTextI18n")
        fun setData() {
            var i = list[adapterPosition].count
            name.text = list[adapterPosition].foodId.toString()
            count.setText(i.toString())
            price.text =
                (list[adapterPosition].price * list[adapterPosition].count).toString() + "đ"
            imagePlus.setOnClickListener {
                i++
                count.setText(i.toString())
            }
            imageMinus.setOnClickListener {
                if (i - 1 != 0) {
                    i--
                    count.setText(i.toString())
                }
            }
            count.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun afterTextChanged(p0: Editable?) {
                    price.text = (list[adapterPosition].price * count.text.toString()
                        .toInt()).toString() + "đ"
                }
            })

        }
    }
}