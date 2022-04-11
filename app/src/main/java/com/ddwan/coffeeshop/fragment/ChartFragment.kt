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
import com.ddwan.coffeeshop.Application.Companion.sdfDay
import com.ddwan.coffeeshop.R
import com.ddwan.coffeeshop.model.LoadingDialog
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class ChartFragment : Fragment() {


    private val listBillID = ArrayList<ArrayList<String>>()
    private val listDay = ArrayList<String>()
    private val listPrice = ArrayList<Int>()
    private val dialogLoad by lazy { LoadingDialog(requireActivity()) }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chart, container, false)
        loadData(view)
        return view
    }

    fun initChart(view: View) {
        val barchart: BarChart = view.findViewById(R.id.chart)
        val list = arrayListOf<BarEntry>(
            BarEntry(0f, listPrice[0].toFloat()),
            BarEntry(1f, listPrice[1].toFloat()),
            BarEntry(2f, listPrice[2].toFloat()),
            BarEntry(3f, listPrice[3].toFloat()),
            BarEntry(4f, listPrice[4].toFloat()),
            BarEntry(5f, listPrice[5].toFloat()),
            BarEntry(6f, listPrice[6].toFloat()))

        val barDataset = BarDataSet(list, "Doanh thu")
        barDataset.colors =
            mutableListOf(Color.BLUE, Color.CYAN, Color.RED, Color.GRAY, Color.GREEN)
        barDataset.valueTextColor = Color.BLACK
        barDataset.valueTextSize = 16f
        val barData = BarData(barDataset)
        barchart.setFitBars(true)
        barchart.data = barData
        barchart.animateY(2000)
        dialogLoad.stopLoadingDialog()
    }


    private fun loadData(view: View) {
        dialogLoad.startLoadingDialog()
        returnsTheLast7Days()
        var checkLast = false
        firebaseDB.reference.child("Bill")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        var i = 0
                        for (item in snapshot.children) {
                            i++
                            if (i == snapshot.childrenCount.toInt()) checkLast = true
                            if ((item.child("Status").value as Boolean)) {
                                val time = sdf.parse(item.child("Date_Check_Out").value.toString())
                                when (sdfDay.format(time)) {
                                    listDay[0] -> {
                                        returnTheMoneyOfOneBill(item.key.toString(),
                                            0,
                                            view,
                                            checkLast)
                                    }
                                    listDay[1] -> {
                                        returnTheMoneyOfOneBill(item.key.toString(),
                                            1,
                                            view,
                                            checkLast)
                                    }
                                    listDay[2] -> {
                                        returnTheMoneyOfOneBill(item.key.toString(),
                                            2,
                                            view,
                                            checkLast)
                                    }
                                    listDay[3] -> {
                                        returnTheMoneyOfOneBill(item.key.toString(),
                                            3,
                                            view,
                                            checkLast)
                                    }
                                    listDay[4] -> {
                                        returnTheMoneyOfOneBill(item.key.toString(),
                                            4,
                                            view,
                                            checkLast)
                                    }
                                    listDay[5] -> {
                                        returnTheMoneyOfOneBill(item.key.toString(),
                                            5,
                                            view,
                                            checkLast)
                                    }
                                    listDay[6] -> {
                                        returnTheMoneyOfOneBill(item.key.toString(),
                                            6,
                                            view,
                                            checkLast)
                                    }
                                }
                            } else {
                                if (checkLast) initChart(view)
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    fun returnTheMoneyOfOneBill(billID: String, day: Int, view: View, checkLast: Boolean) {
        firebaseDB.reference.child("BillInfo")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (item in snapshot.children) {
                            if (item.child("Bill_ID").value.toString() == billID) {
                                listPrice[day] += item.child("Price").value.toString()
                                    .toInt() * item.child("Count").value.toString().toInt()
                            }
                        }
                    }
                    if (checkLast) initChart(view)
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
    }


    private fun returnsTheLast7Days() {
        listPrice.add(0)
        for (i in 1..6) {
            addDay(-i)
            listBillID.add(ArrayList())
            listPrice.add(0)
            Log.d("check $i", listDay[i - 1])
        }
        listDay.add(sdfDay.format(Calendar.getInstance().time))
    }

    private fun addDay(i: Int) {
        val now = Calendar.getInstance().time
        val calendar = Calendar.getInstance()
        calendar.time = now
        calendar.add(Calendar.DAY_OF_YEAR, i)
        val newDate = calendar.time
        listDay.add(sdfDay.format(newDate))
    }

}