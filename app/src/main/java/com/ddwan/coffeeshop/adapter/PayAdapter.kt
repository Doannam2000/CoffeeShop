package com.ddwan.coffeeshop.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.ddwan.coffeeshop.Application.Companion.TYPE_DELETE
import com.ddwan.coffeeshop.Application.Companion.TYPE_MINUS
import com.ddwan.coffeeshop.Application.Companion.TYPE_PLUS
import com.ddwan.coffeeshop.Application.Companion.firebaseDB
import com.ddwan.coffeeshop.Application.Companion.firebaseStore
import com.ddwan.coffeeshop.Application.Companion.numberFormatter
import com.ddwan.coffeeshop.R
import com.ddwan.coffeeshop.model.BillInfo
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class PayAdapter(
    var list: ArrayList<BillInfo>,
    var context: Context,
) :
    RecyclerView.Adapter<PayAdapter.ViewHolder>() {

    lateinit var itemClick: (position: Int, type: Int) -> Unit
    fun setCallBack(click: (position: Int, type: Int) -> Unit) {
        itemClick = click
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PayAdapter.ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_food_in_pay_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: PayAdapter.ViewHolder, position: Int) {
        holder.setData()
    }

    override fun getItemCount(): Int {
        return list.size
    }

    @SuppressLint("SetTextI18n")
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var image: ImageView = itemView.findViewById(R.id.imageFood)
        private var name: TextView = itemView.findViewById(R.id.nameFood)
        private var price: TextView = itemView.findViewById(R.id.price)
        private var count: TextView = itemView.findViewById(R.id.countFood)
        private var imagePlus: ImageView = itemView.findViewById(R.id.add)
        private var imageMinus: ImageView = itemView.findViewById(R.id.minus)
        private var imageDelete: ImageView = itemView.findViewById(R.id.deleteFood)

        @SuppressLint("SetTextI18n")
        fun setData() {
            firebaseDB.reference.child("Food").child(list[adapterPosition].foodId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        name.text = snapshot.child("Name").value.toString()
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }
                })
            count.text = list[adapterPosition].count.toString()
            price.text =
                numberFormatter.format((list[adapterPosition].price * list[adapterPosition].count))
            firebaseStore.reference.child(list[adapterPosition].foodId).downloadUrl.addOnSuccessListener { Uri ->
                try {
                    Glide.with(context)
                        .load(Uri.toString())
                        .apply(RequestOptions().override(70, 70))
                        .into(image)
                } catch (e: Exception) {
                }
            }

        }

        init {
            imagePlus.setOnClickListener {
                itemClick.invoke(adapterPosition, TYPE_PLUS)
                list[adapterPosition].count++
                count.setText(list[adapterPosition].count.toString())
                price.text =
                    numberFormatter.format(list[adapterPosition].price * list[adapterPosition].count)
                itemClick.invoke(adapterPosition, TYPE_MINUS)
            }
            imageMinus.setOnClickListener {
                list[adapterPosition].count--
                count.setText(list[adapterPosition].count.toString())
                price.text =
                    numberFormatter.format(list[adapterPosition].price * list[adapterPosition].count)
                itemClick.invoke(adapterPosition, TYPE_MINUS)
            }
            imageDelete.setOnClickListener { itemClick.invoke(adapterPosition, TYPE_DELETE) }
        }
    }


}