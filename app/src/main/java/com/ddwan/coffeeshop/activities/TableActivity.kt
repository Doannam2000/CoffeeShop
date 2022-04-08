package com.ddwan.coffeeshop.activities

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ddwan.coffeeshop.Application
import com.ddwan.coffeeshop.R
import com.ddwan.coffeeshop.adapter.TableAdapter
import com.ddwan.coffeeshop.model.LoadingDialog
import com.ddwan.coffeeshop.model.Table
import com.ddwan.coffeeshop.viewmodel.MyViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_table.*
import kotlinx.android.synthetic.main.custom_editext_dialog.view.*

class TableActivity : AppCompatActivity() {
    val model by lazy {
        ViewModelProvider(this).get(MyViewModel::class.java)
    }
    private val dialogLoad by lazy { LoadingDialog(this) }
    private val listEmpty = ArrayList<Table>()
    private val listLiveTable = ArrayList<Table>()
    private val adapterEmpty by lazy { TableAdapter(listEmpty) }
    private val adapterLiveTable by lazy { TableAdapter(listLiveTable) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_table)
        initRecyclerEmpty()
        //setup RecyclerView
        initRecyclerLive()
        //load data from firebase

        val btnAdd: ImageView = findViewById(R.id.image_add_table)
        btnAdd.setOnClickListener {
            createDialogAddTable()
        }
        btnPrevious.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.left_to_right,
                R.anim.left_to_right_out)
        }
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun initRecyclerEmpty() {
        adapterEmpty.setCallBack {
            val intent = Intent(this, BillActivity::class.java)
            intent.putExtra("TableID", listEmpty[it].tableId)
            intent.putExtra("TableName", listEmpty[it].tableName)
            intent.putExtra("Status", true)
            this.startActivity(intent)
        }
        val recyclerTableEmpty: RecyclerView = findViewById(R.id.recyclerView_empty_table)
        recyclerTableEmpty.layoutManager = GridLayoutManager(this, 3)
        recyclerTableEmpty.setHasFixedSize(true)
        recyclerTableEmpty.adapter = adapterEmpty
    }

    private fun initRecyclerLive() {
        adapterLiveTable.setCallBack {
            val intent = Intent(this, BillActivity::class.java)
            intent.putExtra("TableID", listLiveTable[it].tableId)
            intent.putExtra("TableName", listLiveTable[it].tableName)
            intent.putExtra("Status", false)
            this.startActivity(intent)
        }
        val recyclerLiveTable: RecyclerView = findViewById(R.id.recyclerView_live_table)
        recyclerLiveTable.layoutManager = GridLayoutManager(this, 3)
        recyclerLiveTable.setHasFixedSize(true)
        recyclerLiveTable.adapter = adapterLiveTable
    }

    private fun createDialogAddTable() {
        val viewDialog = View.inflate(this, R.layout.custom_editext_dialog, null)
        val builder = AlertDialog.Builder(this)
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
                Toast.makeText(this,
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
        listEmpty.clear()
        listLiveTable.clear()
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