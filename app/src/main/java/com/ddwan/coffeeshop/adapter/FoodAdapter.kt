package com.ddwan.coffeeshop.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.ddwan.coffeeshop.R
import com.ddwan.coffeeshop.model.Food

class FoodAdapter(var list: ArrayList<Food>) : RecyclerView.Adapter<FoodAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.food_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData()
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var layoutFood: CardView = itemView.findViewById(R.id.layout)
        var image: ImageView = itemView.findViewById(R.id.imageFood)
        var name: TextView = itemView.findViewById(R.id.nameFood)
        var price: TextView = itemView.findViewById(R.id.price)
        fun setData() {
            name.text = list[adapterPosition].foodName
            price.text = list[adapterPosition].price.toString()
        }
    }
}