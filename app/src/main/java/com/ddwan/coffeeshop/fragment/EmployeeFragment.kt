package com.ddwan.coffeeshop.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ddwan.coffeeshop.Application.Companion.firebaseDB
import com.ddwan.coffeeshop.R
import com.ddwan.coffeeshop.activities.AccountActivity
import com.ddwan.coffeeshop.activities.EditProfileActivity
import com.ddwan.coffeeshop.adapter.EmployeeAdapter
import com.ddwan.coffeeshop.model.Account
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_employee.view.*

class EmployeeFragment : Fragment() {
    var list = ArrayList<Account>()
    val adapter by lazy { EmployeeAdapter(list, requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.fragment_employee, container, false)
        adapter.setCallBack {
            val bundle = Bundle()
            bundle.putSerializable("account", list[it])
            val intent = Intent(requireContext(), AccountActivity::class.java)
            intent.putExtras(bundle)
            startActivity(intent)
            activity?.overridePendingTransition(R.anim.right_to_left,
                R.anim.right_to_left_out)
        }
        val recyclerViewEmployee: RecyclerView = view.findViewById(R.id.recyclerView_employee)
        recyclerViewEmployee.layoutManager = LinearLayoutManager(requireContext())
        recyclerViewEmployee.setHasFixedSize(true)
        recyclerViewEmployee.adapter = adapter
        view.addAccount.setOnClickListener {
            startActivity(Intent(requireActivity(), EditProfileActivity::class.java))
        }
        return view
    }

    private fun loadData(){
        val listP = ArrayList<Account>()
        firebaseDB.getReference("Users")
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