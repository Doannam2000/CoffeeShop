package com.ddwan.coffeeshop.fragment

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.ddwan.coffeeshop.Application.Companion.firebaseDB
import com.ddwan.coffeeshop.Application.Companion.sdf
import com.ddwan.coffeeshop.Application.Companion.sdfDay
import com.ddwan.coffeeshop.R
import com.ddwan.coffeeshop.activities.BillActivity
import com.ddwan.coffeeshop.model.Bill
import com.ddwan.coffeeshop.model.LoadingDialog
import com.ddwan.coffeeshop.model.WriteFile
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.kal.rackmonthpicker.RackMonthPicker
import kotlinx.android.synthetic.main.fragment_chart.view.*
import java.time.LocalDate
import java.time.YearMonth
import java.util.*
import kotlin.collections.ArrayList


class ChartFragment : Fragment() {

    private val listBillDay = ArrayList<Bill>()
    private val listMoneyOfBill = ArrayList<Int>()
    private val listDay = ArrayList<String>()
    private val listPrice = ArrayList<Int>()
    private val dialogLoad by lazy { LoadingDialog(requireActivity()) }
    lateinit var viewAll: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chart, container, false)
        val cal = Calendar.getInstance()
        view.txtDay.text = sdfDay.format(cal.time)
        view.txtDay.setOnClickListener {
            val year = cal.get(Calendar.YEAR)
            val month = cal.get(Calendar.MONTH)
            val day = cal.get(Calendar.DAY_OF_MONTH)
            val datePicker =
                DatePickerDialog(requireContext(), { _, y, monthOfYear, dayOfMonth ->
                    cal.set(Calendar.YEAR, y)
                    cal.set(Calendar.MONTH, monthOfYear)
                    cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    val intent = Intent(requireActivity(), BillActivity::class.java)
                    intent.putExtra("Date", sdfDay.format(cal.time))
                    startActivity(intent)
                    requireActivity().overridePendingTransition(
                        R.anim.right_to_left,
                        R.anim.right_to_left_out
                    )
                }, year, month, day)
            datePicker.datePicker.maxDate = cal.timeInMillis
            datePicker.show()
        }
        view.imageOutput.setOnClickListener { it ->
            val popupMenu = PopupMenu(requireContext(), it)
            popupMenu.menuInflater.inflate(R.menu.export, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { menuItem ->
                selectedItem(menuItem)
                false
            }
            popupMenu.show()
        }
        viewAll = view
        return view
    }

    // tao bieu do
    fun initChart(view: View) {
        val barchart: BarChart = view.findViewById(R.id.chart)
        val list = arrayListOf(
            BarEntry(0f, listPrice[0].toFloat()),
            BarEntry(1f, listPrice[1].toFloat()),
            BarEntry(2f, listPrice[2].toFloat()),
            BarEntry(3f, listPrice[3].toFloat()),
            BarEntry(4f, listPrice[4].toFloat()),
            BarEntry(5f, listPrice[5].toFloat()),
            BarEntry(6f, listPrice[6].toFloat())
        )
        val barDataset = BarDataSet(list, "")
        barDataset.colors =
            mutableListOf(
                Color.LTGRAY,
                Color.CYAN,
                Color.RED,
                Color.GRAY,
                Color.GREEN,
                Color.MAGENTA,
                Color.YELLOW
            )
        barDataset.valueTextColor = Color.BLACK
        barDataset.valueTextSize = 10f
        val barData = BarData(barDataset)

        barchart.setFitBars(false)
        barchart.xAxis.setDrawGridLines(false)
        barchart.xAxis.setDrawAxisLine(false)
        barchart.axisRight.isEnabled = false
        barchart.legend.isEnabled = false
        barchart.description.isEnabled = false

        barchart.data = barData
        barchart.animateY(2000)

        val xAxis: XAxis = barchart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM_INSIDE
        xAxis.valueFormatter = MyAxisFormatter()
        xAxis.setDrawLabels(true)
        xAxis.granularity = 1f
        xAxis.labelRotationAngle = +90f
        dialogLoad.stopLoadingDialog()
    }

    // lay du lieu bieu do
    private fun loadDataLastSevenDays(view: View) {
        dialogLoad.startLoadingDialog()
        returnsTheLast7Days()
        var checkLast = false
        firebaseDB.reference.child("Bill")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for ((i, item) in snapshot.children.reversed().withIndex()) {
                            if (i == snapshot.childrenCount.toInt() - 1) checkLast = true
                            if (item.child("Status").value as Boolean) {
                                val time = sdf.parse(item.child("Date_Check_Out").value.toString())
                                val index = listDay.indexOf(sdfDay.format(time))
                                returnTheMoneyOfOneBill(
                                    item.key.toString(),
                                    index,
                                    view,
                                    checkLast
                                )
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

    override fun onResume() {
        super.onResume()
        if (this::viewAll.isInitialized)
            loadDataLastSevenDays(viewAll)
    }

    fun returnTheMoneyOfOneBill(billID: String, day: Int, view: View, checkLast: Boolean) {
        firebaseDB.reference.child("BillInfo")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        if (day != -1)
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
        for (i in 6 downTo 0) {
            addDay(-i)
            listPrice.add(0)
        }
    }

    private fun addDay(i: Int) {
        val now = Calendar.getInstance().time
        val calendar = Calendar.getInstance()
        calendar.time = now
        calendar.add(Calendar.DAY_OF_YEAR, i)
        val newDate = calendar.time
        listDay.add(sdfDay.format(newDate))
    }

    inner class MyAxisFormatter : IndexAxisValueFormatter() {
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            val index = value.toInt()
            return if (index < listDay.size) {
                listDay[index]
            } else {
                ""
            }
        }
    }


    private fun selectedItem(it: MenuItem) {
        val cal = Calendar.getInstance()
        when (it.itemId) {
            R.id.itemDay -> {
                val year = cal.get(Calendar.YEAR)
                val month = cal.get(Calendar.MONTH)
                val day = cal.get(Calendar.DAY_OF_MONTH)
                val datePicker =
                    DatePickerDialog(requireContext(), { _, y, monthOfYear, dayOfMonth ->
                        cal.set(Calendar.YEAR, y)
                        cal.set(Calendar.MONTH, monthOfYear)
                        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                        loadDataDay(sdfDay.format(cal.time), -1, -1, true)
                    }, year, month, day)
                datePicker.datePicker.maxDate = cal.timeInMillis
                datePicker.show()
            }
            R.id.itemMonth -> {
                RackMonthPicker(requireActivity()).setLocale(Locale.getDefault())
                    .setPositiveButton { month, _, _, year, _ ->
                        loadDataDay("", month, year, false)
                    }
                    .setNegativeButton {
                        it.dismiss()
                    }
                    .show()
            }
        }
    }

    // Xuat bao cao 1 ngay
    private fun loadDataDay(date: String, month: Int, year: Int, checkDay: Boolean) {
        dialogLoad.startLoadingDialog()
        clearDay()
        firebaseDB.reference.child("Bill")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (item in snapshot.children.reversed()) {
                            if ((item.child("Status").value as Boolean)) {
                                val string = item.child("Date_Check_Out").value.toString()
                                val time = sdf.parse(string)
                                if ((sdfDay.format(time)
                                        .equals(date) && checkDay) || (!checkDay && compareMonth(
                                        month,
                                        year,
                                        sdf.parse(string)
                                    ))
                                ) {
                                    listBillDay.add(
                                        Bill(
                                            item.key.toString(),
                                            item.child("Date_Check_In").value.toString(),
                                            item.child("Date_Check_Out").value.toString(),
                                            item.child("Table_ID").value.toString(),
                                            item.child("Status").value as Boolean
                                        )
                                    )
                                }
                            }
                        }
                        if (listBillDay.isEmpty()) {
                            dialogLoad.stopLoadingDialog()
                            Toast.makeText(
                                requireContext(),
                                "Không có dữ liệu của ${if (month == -1) date else "${month}/${year}"}",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else
                            for ((i, item) in listBillDay.withIndex()) {
                                if (i == (listBillDay.size - 1))
                                    totalPriceAndExport(item.billId, true)
                                else
                                    totalPriceAndExport(item.billId, false)
                            }
                    } else {
                        dialogLoad.stopLoadingDialog()
                        Toast.makeText(
                            requireContext(),
                            "Không có dữ liệu của ${if (month == -1) date else "${month}/${year}"}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    fun totalPriceAndExport(billID: String, check: Boolean) {
        firebaseDB.reference.child("BillInfo")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        var price = 0
                        for (item in snapshot.children) {
                            if (item.child("Bill_ID").value.toString() == billID) {
                                price += item.child("Price").value.toString()
                                    .toInt() * item.child("Count").value.toString().toInt()
                            }
                        }
                        listMoneyOfBill.add(price)
                    }
                    if (check) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            if (WriteFile(requireContext()).writeFile(listBillDay, listMoneyOfBill))
                                Toast.makeText(
                                    requireContext(),
                                    "Lưu file thành công !!!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            else
                                Toast.makeText(
                                    requireContext(),
                                    "Có lỗi gì đó xin thử lại sau !!!",
                                    Toast.LENGTH_SHORT
                                ).show()
                        } else
                            Toast.makeText(
                                requireContext(),
                                "Chúng tôi chưa hỗ trợ bản android này !!!",
                                Toast.LENGTH_SHORT
                            ).show()
                        dialogLoad.stopLoadingDialog()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
    }

    // xuat bao cao 1 thang

    private fun clearDay() {
        listBillDay.clear()
        listMoneyOfBill.clear()
    }

    fun compareMonth(month: Int, year: Int, time: Date): Boolean {
        val cal = Calendar.getInstance()
        cal.time = time
        if (month == cal.get(Calendar.MONTH) + 1 && year == cal.get(Calendar.YEAR))
            return true
        return false
    }
}