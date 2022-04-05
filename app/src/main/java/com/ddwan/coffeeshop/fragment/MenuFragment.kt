package com.ddwan.coffeeshop.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ddwan.coffeeshop.Application
import com.ddwan.coffeeshop.R
import com.ddwan.coffeeshop.activities.AccountActivity
import com.ddwan.coffeeshop.activities.FoodActivity
import com.ddwan.coffeeshop.adapter.FoodAdapter
import com.ddwan.coffeeshop.model.Food
import com.ddwan.coffeeshop.model.LoadingDialog
import com.ddwan.coffeeshop.model.Table
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_menu.view.*

class MenuFragment : Fragment() {

    val list = ArrayList<Food>()
    private val adapter by lazy { FoodAdapter(list,requireContext()) }
    private val dialogLoad by lazy { LoadingDialog(requireActivity()) }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.fragment_menu, container, false)
        view.addFood.setOnClickListener {
            startActivity(Intent(requireActivity(), FoodActivity::class.java))
            activity?.overridePendingTransition(R.anim.right_to_left,
                R.anim.right_to_left_out)
        }
        initRecycler(view)
        return view
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun loadData() {
        list.clear()
        dialogLoad.startLoadingDialog()
        Application.firebaseDB.getReference("Food")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (f in snapshot.children) {
                            val food = Food(f.key.toString(),
                                f.child("Name").value.toString(),
                                f.child("Category").value.toString(),
                                f.child("Price").value.toString(),
                                f.child("Description").value.toString(), "")
                            list.add(food)
                        }
                        dialogLoad.stopLoadingDialog()
                        adapter.notifyDataSetChanged()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    private fun initRecycler(view: View) {
        adapter.setCallBack {
            val bundle = Bundle()
            bundle.putSerializable("food", list[it])
            bundle.putBoolean("check", true)
            val intent = Intent(requireContext(), FoodActivity::class.java)
            intent.putExtras(bundle)
            startActivity(intent)
            activity?.overridePendingTransition(R.anim.right_to_left,
                R.anim.right_to_left_out)
        }
        var recyclerViewFood: RecyclerView = view.findViewById(R.id.recyclerViewFood)
        recyclerViewFood.layoutManager = LinearLayoutManager(requireContext())
        recyclerViewFood.setHasFixedSize(true)
        recyclerViewFood.adapter = adapter
    }
}