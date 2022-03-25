package com.ddwan.coffeeshop.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ddwan.coffeeshop.R
import com.ddwan.coffeeshop.adapter.EmployeeAdapter
import com.ddwan.coffeeshop.model.Account


class EmployeeFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_employee, container, false)
        var list = arrayListOf<Account>(Account(0,
            "doanduynam2000@gmail.com",
            "admin",
            "Đoàn Duy Nam",
            "Hải Phòng",
            "0397482016",
            "admin",
            R.drawable.img.toString()),Account(1,
            "doanduynam2000@gmail.com",
            "admin",
            "Đoàn Duy Nam",
            "Hải Phòng",
            "0397482016",
            "admin",
            R.drawable.img.toString()),Account(2,
            "doanduynam2000@gmail.com",
            "admin",
            "Đoàn Duy Nam",
            "Hải Phòng",
            "0397482016",
            "admin",
            R.drawable.img.toString()),Account(3,
            "doanduynam2000@gmail.com",
            "admin",
            "Đoàn Duy Nam",
            "Hải Phòng",
            "0397482016",
            "admin",
            R.drawable.img.toString()),Account(4,
            "doanduynam2000@gmail.com",
            "admin",
            "Đoàn Duy Nam",
            "Hải Phòng",
            "0397482016",
            "admin",
            R.drawable.img.toString()),Account(5,
            "doanduynam2000@gmail.com",
            "admin",
            "Đoàn Duy Nam",
            "Hải Phòng",
            "0397482016",
            "admin",
            R.drawable.img.toString()),Account(6,
            "doanduynam2000@gmail.com",
            "admin",
            "Đoàn Duy Nam",
            "Hải Phòng",
            "0397482016",
            "admin",
            R.drawable.img.toString()),Account(7,
            "doanduynam2000@gmail.com",
            "admin",
            "Đoàn Duy Nam",
            "Hải Phòng",
            "0397482016",
            "admin",
            R.drawable.img.toString()),Account(8,
            "doanduynam2000@gmail.com",
            "admin",
            "Đoàn Duy Nam",
            "Hải Phòng",
            "0397482016",
            "admin",
            R.drawable.img.toString()),Account(9,
            "doanduynam2000@gmail.com",
            "admin",
            "Đoàn Duy Nam",
            "Hải Phòng",
            "0397482016",
            "admin",
            R.drawable.img.toString()),)
        val adapter = EmployeeAdapter(list)
        val recyclerViewEmployee: RecyclerView = view.findViewById(R.id.recyclerView_employee)
        recyclerViewEmployee.layoutManager = LinearLayoutManager(requireContext())
        recyclerViewEmployee.setHasFixedSize(true)
//        var dividerItemDecoration = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
//        recyclerViewEmployee.addItemDecoration(dividerItemDecoration)
        recyclerViewEmployee.adapter = adapter
        return view
    }

}