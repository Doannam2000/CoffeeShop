package com.ddwan.coffeeshop.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.ddwan.coffeeshop.R
import com.ddwan.coffeeshop.model.Table

class TableAdapter(var list:ArrayList<Table>): RecyclerView.Adapter<TableAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TableAdapter.ViewHolder {
        var view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.table_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: TableAdapter.ViewHolder, position: Int) {
        holder.setData()
    }

    override fun getItemCount(): Int {
        return list.size
    }
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var layoutFood: CardView = itemView.findViewById(R.id.cardViewTable)
        var image: ImageView = itemView.findViewById(R.id.imageTable)
        var name: TextView = itemView.findViewById(R.id.nameTable)
        var price: TextView = itemView.findViewById(R.id.description)
        fun setData() {
            name.text = list[adapterPosition].tableName
            price.text = list[adapterPosition].description
        }
    }
}