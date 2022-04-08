package com.ddwan.coffeeshop.fragment

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ddwan.coffeeshop.R
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry


class ChartFragment : Fragment() {

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
            BarEntry(2020f, 770f))
        val barDataset = BarDataSet(list,"Doanh thu")
        barDataset.colors = mutableListOf(Color.BLUE,Color.CYAN,Color.RED,Color.GRAY,Color.GREEN)
        barDataset.valueTextColor = Color.BLACK
        barDataset.valueTextSize = 16f

        val barData = BarData(barDataset)

        barchart.setFitBars(true)
        barchart.data = barData
        barchart.animateY(2000)


        return view
    }

    fun loadData(){

    }
}