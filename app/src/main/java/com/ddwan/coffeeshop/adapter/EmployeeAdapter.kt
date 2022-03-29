package com.ddwan.coffeeshop.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.ddwan.coffeeshop.R
import com.ddwan.coffeeshop.model.Account

class EmployeeAdapter(var list:ArrayList<Account>):RecyclerView.Adapter<EmployeeAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployeeAdapter.ViewHolder {
        var view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.employee_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: EmployeeAdapter.ViewHolder, position: Int) {
        holder.setData()
    }

    override fun getItemCount(): Int {
        return list.size
    }
    inner class ViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
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
            image.setImageResource(list[adapterPosition].imageUrl.toInt())
        }
    }
}