package com.ddwan.coffeeshop.fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ddwan.coffeeshop.Application
import com.ddwan.coffeeshop.R
import com.ddwan.coffeeshop.activities.BillActivity
import com.ddwan.coffeeshop.adapter.TableAdapter
import com.ddwan.coffeeshop.model.LoadingDialog
import com.ddwan.coffeeshop.model.Table
import com.ddwan.coffeeshop.viewmodel.MyViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.custom_editext_dialog.view.*

class TableFragment : Fragment() {

    val model by lazy {
        ViewModelProvider(this).get(MyViewModel::class.java)
    }
    private val dialogLoad by lazy { LoadingDialog(requireActivity()) }
    private val listEmpty = ArrayList<Table>()
    private val listLiveTable = ArrayList<Table>()
    private val adapterEmpty by lazy { TableAdapter(listEmpty) }
    private val adapterLiveTable by lazy { TableAdapter(listLiveTable) }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_table, container, false)

        //setup RecyclerView Empty
        initRecyclerEmpty(view)
        //setup RecyclerView
        initRecyclerLive(view)
        //load data from firebase
        loadData()
        val btnAdd: ImageView = view.findViewById(R.id.image_add_table)
        btnAdd.setOnClickListener {
            createDialogAddTable()
        }
        return view
    }

    private fun initRecyclerEmpty(view: View) {
        adapterEmpty.setCallBack {
            val intent = Intent(requireContext(), BillActivity::class.java)
            intent.putExtra("TableID", listEmpty[it].tableId)
            intent.putExtra("TableName", listEmpty[it].tableName)
            intent.putExtra("Status", true)
            requireActivity().startActivity(intent)
        }
        val recyclerTableEmpty: RecyclerView = view.findViewById(R.id.recyclerView_empty_table)
        recyclerTableEmpty.layoutManager = GridLayoutManager(requireContext(), 3)
        recyclerTableEmpty.setHasFixedSize(true)
        recyclerTableEmpty.adapter = adapterEmpty
    }

    private fun initRecyclerLive(view: View) {
        adapterLiveTable.setCallBack {
            val intent = Intent(requireContext(), BillActivity::class.java)
            intent.putExtra("TableID", listLiveTable[it].tableId)
            intent.putExtra("TableName", listLiveTable[it].tableName)
            intent.putExtra("Status", false)
            requireActivity().startActivity(intent)
        }
        val recyclerLiveTable: RecyclerView = view.findViewById(R.id.recyclerView_live_table)
        recyclerLiveTable.layoutManager = GridLayoutManager(requireContext(), 3)
        recyclerLiveTable.setHasFixedSize(true)
        recyclerLiveTable.adapter = adapterLiveTable
    }

    private fun createDialogAddTable() {
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
            if (viewDialog.name.text.isEmpty()) {
                Toast.makeText(requireContext(),
                    "Tên bàn không được để trống",
                    Toast.LENGTH_SHORT).show()
            } else {
                addTable(viewDialog.name.text.toString())
            }
        }
    }

    private fun addTable(name: String) {
        val hashMap = HashMap<String, Any>()
        val id = model.randomID()
        hashMap["Name"] = name
        hashMap["Description"] = "Trống"
        hashMap["Status"] = true
        Application.firebaseDB.reference.child("Table").child(id)
            .updateChildren(hashMap).addOnCompleteListener {
                if (it.isSuccessful) {
                    listEmpty.add(Table(id, name, "Trống", true))
                    adapterEmpty.notifyDataSetChanged()
                }
            }
    }

    private fun loadData() {
        dialogLoad.startLoadingDialog()
        val list = ArrayList<Table>()
        Application.firebaseDB.getReference("Table")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (tb in snapshot.children) {
                            val table = Table(tb.key.toString(),
                                tb.child("Name").value.toString(),
                                tb.child("Description").value.toString(),
                                tb.child("Status").value as Boolean)
                            list.add(table)
                        }
                        splitData(list)
                    } else {
                        dialogLoad.stopLoadingDialog()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    @SuppressLint("NotifyDataSetChanged")
    fun splitData(list: ArrayList<Table>) {
        for (item in list) {
            if (item.status)
                listEmpty.add(item)
            else
                listLiveTable.add(item)
        }
        adapterEmpty.notifyDataSetChanged()
        adapterLiveTable.notifyDataSetChanged()
        dialogLoad.stopLoadingDialog()
    }
}