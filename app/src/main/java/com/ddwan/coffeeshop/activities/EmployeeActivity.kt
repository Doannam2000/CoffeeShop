package com.ddwan.coffeeshop.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ddwan.coffeeshop.Application
import com.ddwan.coffeeshop.R
import com.ddwan.coffeeshop.adapter.EmployeeAdapter
import com.ddwan.coffeeshop.model.Account
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_employee.*

class EmployeeActivity : AppCompatActivity() {
    var list = ArrayList<Account>()
    val adapter by lazy { EmployeeAdapter(list, this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_employee)
        adapter.setCallBack {
            val bundle = Bundle()
            bundle.putSerializable("account", list[it])
            val intent = Intent(this, AccountActivity::class.java)
            intent.putExtras(bundle)
            startActivity(intent)
            overridePendingTransition(R.anim.right_to_left,
                R.anim.right_to_left_out)
        }
        val recyclerViewEmployee: RecyclerView = findViewById(R.id.recyclerView_employee)
        recyclerViewEmployee.layoutManager = LinearLayoutManager(this)
        recyclerViewEmployee.setHasFixedSize(true)
        recyclerViewEmployee.adapter = adapter
        addAccount.setOnClickListener {
            startActivity(Intent(this, EditProfileActivity::class.java))
        }
        btnPrevious.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.left_to_right,
                R.anim.left_to_right_out)
        }
    }

    private fun loadData() {
        val listP = ArrayList<Account>()
        Application.firebaseDB.getReference("Users")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (user in snapshot.children) {
                            val account = Account(user.key.toString(),
                                user.child("Email").value.toString(),
                                "",
                                user.child("Name").value.toString(),
                                user.child("Address").value.toString(),
                                user.child("Phone_Number").value.toString(),
                                user.child("Role").value.toString(),
                                user.child("Gender").value as Boolean,
                                "")
                            listP.add(account)
                        }
                        list.clear()
                        list.addAll(listP)
                        adapter.notifyDataSetChanged()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }
}