package com.ddwan.coffeeshop.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.ddwan.coffeeshop.Application
import com.ddwan.coffeeshop.Application.Companion.firebaseStore
import com.ddwan.coffeeshop.R
import com.ddwan.coffeeshop.model.Food

class FoodAdapter(var list: ArrayList<Food>,var context:Context) : RecyclerView.Adapter<FoodAdapter.ViewHolder>() {

    lateinit var itemClick: (position: Int) -> Unit
    fun setCallBack(click: (position: Int) -> Unit) {
        itemClick = click
    }

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
        private var layoutFood: CardView = itemView.findViewById(R.id.layout)
        private var image: ImageView = itemView.findViewById(R.id.imageFood)
        private var name: TextView = itemView.findViewById(R.id.nameFood)
        private var price: TextView = itemView.findViewById(R.id.price)
        private var description: TextView = itemView.findViewById(R.id.descriptionFood)

        @SuppressLint("SetTextI18n")
        fun setData() {
            name.text = list[adapterPosition].foodName
            price.text = list[adapterPosition].price + "đ"
            description.text = list[adapterPosition].description
            firebaseStore.reference.child(list[adapterPosition].foodId).downloadUrl.addOnSuccessListener { Uri ->
                Glide.with(context)
                    .load(Uri.toString())
                    .apply(RequestOptions().override(70, 70))
                    .into(image)
                list[adapterPosition].imageUrl = Uri.toString()
            }
        }
        init {
            layoutFood.setOnClickListener { itemClick.invoke(adapterPosition) }
        }
    }
}