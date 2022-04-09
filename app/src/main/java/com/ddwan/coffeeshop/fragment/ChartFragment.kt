package com.ddwan.coffeeshop.fragment

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ddwan.coffeeshop.Application
import com.ddwan.coffeeshop.Application.Companion.firebaseDB
import com.ddwan.coffeeshop.Application.Companion.sdf
import com.ddwan.coffeeshop.R
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList


class ChartFragment : Fragment() {

    val listBillID = ArrayList<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_chart, container, false)
        val barchart: BarChart = view.findViewById(R.id.chart)
        val list = arrayListOf<BarEntry>(BarEntry(2016f, 560f),
            BarEntry(2017f, 202f),
            BarEntry(2018f, 300f),
            BarEntry(2019f, 1200f),
            BarEntry(2020f, 70f),
            BarEntry(2021f, 0f),
            BarEntry(2022f, 99f))
        val barDataset = BarDataSet(list, "Doanh thu")

        barDataset.colors =
            mutableListOf(Color.BLUE, Color.CYAN, Color.RED, Color.GRAY, Color.GREEN)
        barDataset.valueTextColor = Color.BLACK
        barDataset.valueTextSize = 16f

        val barData = BarData(barDataset)
        loadData()
        barchart.setFitBars(true)
        barchart.data = barData
        barchart.animateY(2000)
        return view
    }

    fun loadData() {
        firebaseDB.reference.child("Bill")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (item in snapshot.children) {
                            if((item.child("Status").value as Boolean)){
                                val time = sdf.parse(item.child("Date_Check_Out").value.toString())
                                Log.d("checkkkkkk", time.toString())
                                val now = Calendar.getInstance().time
                                if (now.time - time.time < 604800000L) {
                                    Log.d("checkkkkkkabc", item.key.toString())
                                }
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }
}