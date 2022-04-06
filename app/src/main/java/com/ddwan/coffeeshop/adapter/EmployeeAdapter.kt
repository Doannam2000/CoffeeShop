package com.ddwan.coffeeshop.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.ddwan.coffeeshop.Application
import com.ddwan.coffeeshop.Application.Companion.firebaseStore
import com.ddwan.coffeeshop.R
import com.ddwan.coffeeshop.model.Account

class EmployeeAdapter(var list: ArrayList<Account>, var context: Context) :
    RecyclerView.Adapter<EmployeeAdapter.ViewHolder>() {

    lateinit var itemClick: (position: Int) -> Unit
    fun setCallBack(click: (position: Int) -> Unit) {
        itemClick = click
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployeeAdapter.ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.employee_layout, parent, false)
        return ViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: EmployeeAdapter.ViewHolder, position: Int) {
        holder.setData()
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var employeeAccount: CardView = itemView.findViewById(R.id.employeeAccount)
        private var image: ImageView = itemView.findViewById(R.id.imageAccount)
        private var name: TextView = itemView.findViewById(R.id.name)
        private var role: TextView = itemView.findViewById(R.id.role)
        private var email: TextView = itemView.findViewById(R.id.email)

        @SuppressLint("SetTextI18n")
        fun setData() {
            name.text = list[adapterPosition].name
            role.text = list[adapterPosition].role
            email.text = list[adapterPosition].email
            firebaseStore.reference.child(list[adapterPosition].id).downloadUrl.addOnSuccessListener { Uri ->
                Glide.with(context)
                    .load(Uri.toString())
                    .apply(RequestOptions().override(70, 70))
                    .into(image)
                list[adapterPosition].imageUrl = Uri.toString()
            }
        }

        init {
            employeeAccount.setOnClickListener {
                itemClick.invoke(adapterPosition)
            }
        }
    }
}