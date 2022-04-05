package com.ddwan.coffeeshop.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ddwan.coffeeshop.R
import com.ddwan.coffeeshop.adapter.FoodAdapter
import com.ddwan.coffeeshop.model.Food
import com.ddwan.coffeeshop.sql.SQLHelper

class MenuFragment : Fragment() {


    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.fragment_menu, container, false)
        val sqlHelper = SQLHelper(requireContext())
        var list = arrayListOf<Food>(
            Food(0, "Cafe", 1, 25000, "Cafe không đường", R.drawable.cafe1.toString()),
            Food(1, "Cafe 1", 1, 125000, "Cafe không đường", R.drawable.cafe2.toString()),
            Food(2, "Cafe 2", 1, 35000, "Cafe không đường", R.drawable.cafe3.toString()),
            Food(3, "Cafe 3", 1, 55000, "Cafe không đường", R.drawable.trasua.toString()),
            Food(4, "Cafe 4", 1, 45000, "Cafe không đường", R.drawable.trasua2.toString()),
            Food(5, "Cafe 5", 1, 20000, "Cafe không đường", R.drawable.trasua.toString()),
            Food(0, "Cafe", 1, 25000, "Cafe không đường", R.drawable.cafe1.toString()),
            Food(1, "Cafe 1", 1, 125000, "Cafe không đường", R.drawable.cafe2.toString()),    Food(1, "Cafe 1", 1, 125000, "Cafe không đường", R.drawable.cafe2.toString()),
            Food(2, "Cafe 2", 1, 35000, "Cafe không đường", R.drawable.cafe3.toString()),
            Food(3, "Cafe 3", 1, 55000, "Cafe không đường", R.drawable.trasua.toString()),
            Food(4, "Cafe 4", 1, 45000, "Cafe không đường", R.drawable.trasua2.toString()),
            Food(5, "Cafe 5", 1, 20000, "Cafe không đường", R.drawable.trasua.toString()),
            Food(0, "Cafe", 1, 25000, "Cafe không đường", R.drawable.cafe1.toString()),
            Food(1, "Cafe 1", 1, 125000, "Cafe không đường", R.drawable.cafe2.toString()),
            Food(2, "Cafe 2", 1, 35000, "Cafe không đường", R.drawable.cafe3.toString())
        )
        var adapter = FoodAdapter(list)
        var recyclerViewFood: RecyclerView = view.findViewById(R.id.recyclerViewFood)
        recyclerViewFood.layoutManager = LinearLayoutManager(requireContext())
        recyclerViewFood.setHasFixedSize(true)
        recyclerViewFood.adapter = adapter
        return view
    }
}