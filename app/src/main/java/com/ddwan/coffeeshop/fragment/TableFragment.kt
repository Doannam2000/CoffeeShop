package com.ddwan.coffeeshop.fragment

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ddwan.coffeeshop.R
import com.ddwan.coffeeshop.activities.BillActivity
import com.ddwan.coffeeshop.adapter.TableAdapter
import com.ddwan.coffeeshop.model.Table
import com.ddwan.coffeeshop.sql.SQLHelper
import kotlinx.android.synthetic.main.custom_editext_dialog.view.*
import kotlinx.android.synthetic.main.fragment_table.view.*

class TableFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_table, container, false)
        val sqlHelper = SQLHelper(requireContext())
        var listEmpty = arrayListOf<Table>(
            Table(0, "Bàn 1", "Trống", true),
            Table(2, "Bàn 3", "Trống", true),
            Table(4, "Bàn 5", "Trống", true),
            Table(6, "Bàn 7", "Trống", true),
            Table(8, "Bàn 9", "Trống", true))
        var listLiveTable = arrayListOf<Table>(Table(1, "Bàn 2", "Chưa thanh toán", true),
            Table(3, "Bàn 4", "Chưa thanh toán", true),
            Table(4, "Bàn 6", "Đã thanh toán", true),
            Table(5, "Bàn 8", "Chưa thanh toán", true),
            Table(9, "Bàn 10", "Chưa thanh toán", true))

        var adapterEmpty = TableAdapter(listEmpty)
        adapterEmpty.setCallBack {
            requireActivity().startActivity(Intent(requireContext(),BillActivity::class.java))
        }
        var recyclerTableEmpty: RecyclerView = view.findViewById(R.id.recyclerView_empty_table)
        recyclerTableEmpty.layoutManager = GridLayoutManager(requireContext(), 3)
        recyclerTableEmpty.setHasFixedSize(true)
        recyclerTableEmpty.adapter = adapterEmpty

        var adapterLiveTable = TableAdapter(listLiveTable)
        adapterLiveTable.setCallBack {
            requireActivity().startActivity(Intent(requireContext(),BillActivity::class.java))
        }
        var recyclerLiveTable: RecyclerView = view.findViewById(R.id.recyclerView_live_table)
        recyclerLiveTable.layoutManager = GridLayoutManager(requireContext(), 3)
        recyclerLiveTable.setHasFixedSize(true)
        recyclerLiveTable.adapter = adapterLiveTable

        var btnAdd:ImageView = view.findViewById(R.id.image_add_table)
        btnAdd.setOnClickListener {
            val viewDialog = View.inflate(requireContext(), R.layout.custom_editext_dialog, null)
            val builder = AlertDialog.Builder(requireContext())
            builder.setView(viewDialog)
            val dialog = builder.create()
            dialog.show()
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            viewDialog.cancel.setOnClickListener {
                dialog.dismiss()
            }
            viewDialog.oke.setOnClickListener {
                dialog.dismiss()
                if(viewDialog.name.text.isEmpty()){
                    Toast.makeText(requireContext(),"Tên bàn không được để trống",Toast.LENGTH_SHORT).show()
                }else{
                    listEmpty.add(Table(listEmpty.size+listLiveTable.size+1, viewDialog.name.text.toString(), "Trống", true))
                    adapterEmpty.notifyDataSetChanged()
                }
            }
        }
        return view
    }
}