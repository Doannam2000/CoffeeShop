package com.ddwan.coffeeshop.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ddwan.coffeeshop.R
import com.ddwan.coffeeshop.adapter.FoodAdapter
import com.ddwan.coffeeshop.model.Food
import com.ddwan.coffeeshop.sql.SQLHelper
import kotlinx.android.synthetic.main.fragment_menu.view.*

class MenuFragment : Fragment() {


    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.fragment_menu, container, false)
        val sqlHelper = SQLHelper(requireContext())
        var list = sqlHelper.getAllFromFood()
        var adapter = FoodAdapter(list)
        var recyclerViewFood: RecyclerView = view.findViewById(R.id.recyclerViewFood)
        recyclerViewFood.layoutManager = GridLayoutManager(requireContext(), 2)
        recyclerViewFood.setHasFixedSize(true)
        recyclerViewFood.adapter = adapter
        view.add.setOnClickListener {
            sqlHelper.insertFood(Food(0, "Cafe "+list.size, 1, 10000))
            list = sqlHelper.getAllFromFood()
            adapter.notifyDataSetChanged()
        }
        return view
    }
}