package com.ddwan.coffeeshop.adapter

import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ddwan.coffeeshop.R
import com.ddwan.coffeeshop.model.Table

class TableAdapter(var list: ArrayList<Table>,val type: Boolean) :
    RecyclerView.Adapter<TableAdapter.ViewHolder>() {

    lateinit var itemClick: (position: Int) -> Unit
    fun setCallBack(click: (position: Int) -> Unit) {
        itemClick = click
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TableAdapter.ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.table_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: TableAdapter.ViewHolder, position: Int) {
        holder.setData()
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnCreateContextMenuListener {
        var layoutTable: LinearLayout = itemView.findViewById(R.id.linearTable)
        var name: TextView = itemView.findViewById(R.id.nameTable)
        var price: TextView = itemView.findViewById(R.id.description)
        fun setData() {
            name.text = list[adapterPosition].tableName
            price.text = list[adapterPosition].description
        }

        init {
            layoutTable.setOnClickListener {
                itemClick.invoke(adapterPosition)
            }
            if(type)
                layoutTable.setOnCreateContextMenuListener(this)
        }

        override fun onCreateContextMenu(
            p0: ContextMenu?,
            p1: View?,
            p2: ContextMenu.ContextMenuInfo?,
        ) {
            p0?.add(adapterPosition, 123, 0, "Sửa")
            p0?.add(adapterPosition, 124, 1, "Xóa")
        }
    }

}