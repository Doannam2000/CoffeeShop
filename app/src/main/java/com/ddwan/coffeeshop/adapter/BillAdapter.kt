package com.ddwan.coffeeshop.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.ddwan.coffeeshop.Application
import com.ddwan.coffeeshop.Application.Companion.firebaseDB
import com.ddwan.coffeeshop.Application.Companion.firebaseStore
import com.ddwan.coffeeshop.R
import com.ddwan.coffeeshop.model.BillInfo
import com.ddwan.coffeeshop.model.Food
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class BillAdapter(
    var list: ArrayList<BillInfo>,
    var context: Context,
) :
    RecyclerView.Adapter<BillAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillAdapter.ViewHolder {
        val view: View =
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
            firebaseDB.reference.child("Food").child(list[adapterPosition].foodId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        name.text = snapshot.child("Name").value.toString()
                    }
                    override fun onCancelled(error: DatabaseError) {
                    }
                })
            val i = list[adapterPosition].count
            count.setText(i.toString())
            price.text =
                (list[adapterPosition].price * i).toString() + "đ"
            firebaseStore.reference.child(list[adapterPosition].foodId).downloadUrl.addOnSuccessListener { Uri ->
                Glide.with(context)
                    .load(Uri.toString())
                    .apply(RequestOptions().override(70, 70))
                    .into(image)
            }
        }
    }

}