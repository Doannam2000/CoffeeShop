package com.ddwan.coffeeshop.activities

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ddwan.coffeeshop.Application.Companion.firebaseDB
import com.ddwan.coffeeshop.Application.Companion.listEmpty
import com.ddwan.coffeeshop.Application.Companion.listLiveTable
import com.ddwan.coffeeshop.R
import com.ddwan.coffeeshop.adapter.TableAdapter
import com.ddwan.coffeeshop.model.LoadingDialog
import com.ddwan.coffeeshop.model.Table
import com.ddwan.coffeeshop.viewmodel.MyViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_table.*
import kotlinx.android.synthetic.main.custom_editext_dialog.view.*
import kotlinx.android.synthetic.main.header.*

class TableActivity : AppCompatActivity() {
    val model by lazy {
        ViewModelProvider(this).get(MyViewModel::class.java)
    }
    private val dialogLoad by lazy { LoadingDialog(this) }
    private val adapterEmpty by lazy { TableAdapter(listEmpty, true) }
    private val adapterLiveTable by lazy { TableAdapter(listLiveTable, false) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_table)
        initRecyclerEmpty()
        //setup RecyclerView
        initRecyclerLive()
        //load data from firebase

        val btnAdd: ImageView = findViewById(R.id.image_add_table)
        btnAdd.setOnClickListener {
            createDialogAddTable(true, null)
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

    override fun onBackPressed() {
        finish()
        overridePendingTransition(R.anim.left_to_right,
            R.anim.left_to_right_out)
        super.onBackPressed()
    }

    private fun initRecyclerEmpty() {
        adapterEmpty.setCallBack {
            val intent = Intent(this, PayActivity::class.java)
            intent.putExtra("TableID", listEmpty[it].tableId)
            intent.putExtra("TableName", listEmpty[it].tableName)
            intent.putExtra("Status", true)
            this.startActivity(intent)
            overridePendingTransition(R.anim.right_to_left,
                R.anim.right_to_left_out)
        }
        val recyclerTableEmpty: RecyclerView = findViewById(R.id.recyclerView_empty_table)
        recyclerTableEmpty.layoutManager = GridLayoutManager(this, 3)
        recyclerTableEmpty.setHasFixedSize(true)
        recyclerTableEmpty.adapter = adapterEmpty
    }

    private fun initRecyclerLive() {
        adapterLiveTable.setCallBack {
            val intent = Intent(this, PayActivity::class.java)
            intent.putExtra("TableID", listLiveTable[it].tableId)
            intent.putExtra("TableName", listLiveTable[it].tableName)
            intent.putExtra("Status", false)
            this.startActivity(intent)
            overridePendingTransition(R.anim.right_to_left,
                R.anim.right_to_left_out)
        }
        val recyclerLiveTable: RecyclerView = findViewById(R.id.recyclerView_live_table)
        recyclerLiveTable.layoutManager = GridLayoutManager(this, 3)
        recyclerLiveTable.setHasFixedSize(true)
        recyclerLiveTable.adapter = adapterLiveTable
    }

    private fun createDialogAddTable(check: Boolean, index: Int?) {
        val viewDialog = View.inflate(this, R.layout.custom_editext_dialog, null)
        val builder = AlertDialog.Builder(this)
        builder.setView(viewDialog)
        val dialog = builder.create()
        dialog.show()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        if (!check) viewDialog.name.setText(listEmpty[index!!].tableName)
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
                if (check)
                    addTable(viewDialog.name.text.toString())
                else {
                    if (viewDialog.name.text.toString() != listEmpty[index!!].tableName)
                        updateTable(index, viewDialog.name.text.toString())
                }
            }
        }
    }

    private fun addTable(name: String) {
        val hashMap = HashMap<String, Any>()
        val id = model.randomID()
        hashMap["Name"] = name
        hashMap["Description"] = "Trống"
        hashMap["Status"] = true
        firebaseDB.reference.child("Table")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        var check = true
                        for (item in snapshot.children) {
                            if (name.uppercase() == item.child("Name").value.toString()
                                    .uppercase()
                            ) {
                                check = false
                                break
                            }
                        }
                        if (check) {
                            firebaseDB.reference.child("Table").child(id)
                                .updateChildren(hashMap).addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        listEmpty.add(Table(id, name, "Trống", true))
                                        adapterEmpty.notifyItemInserted(listEmpty.size - 1)
                                    }
                                }
                        } else {
                            Toast.makeText(this@TableActivity,
                                "Tên bàn đã tồn tại",
                                Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

    }

    private fun loadData() {
        dialogLoad.startLoadingDialog()
        val list = ArrayList<Table>()
        firebaseDB.getReference("Table")
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

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            123 -> createDialogAddTable(false, item.groupId)
            124 -> confirmDelete(item.groupId)
        }
        return super.onContextItemSelected(item)
    }

    private fun checkName(name: String): Boolean {
        for (item in listEmpty) {
            if (name.uppercase() == item.tableName.uppercase()) {
                return false
            }
        }
        for (item in listLiveTable) {
            if (name.uppercase() == item.tableName.uppercase()) {
                return false
            }
        }
        return true
    }

    private fun updateTable(index: Int, name: String) {
        if (checkName(name)) {
            firebaseDB.reference.child("Table").child(listEmpty[index].tableId)
                .child("Name")
                .setValue(name)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        listEmpty[index].tableName = name
                        adapterEmpty.notifyItemChanged(index)
                        Toast.makeText(this@TableActivity,
                            "Thực hiện thao tác thành công !",
                            Toast.LENGTH_LONG)
                            .show()
                    }
                }
        } else {
            Toast.makeText(this@TableActivity,
                "Tên bàn đã tồn tại",
                Toast.LENGTH_SHORT).show()
        }
    }


    private fun confirmDelete(index: Int) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Bạn có chắc muốn xóa ${listEmpty[index].tableName} ?")
            .setPositiveButton("Có") { _, _ ->
                deleteTable(index)
            }
            .setNegativeButton("Không") { _, _ -> }.show()
    }

    private fun deleteTable(index: Int) {
        val id = listEmpty[index].tableId
        firebaseDB.reference.child("Table").child(id)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        snapshot.ref.removeValue().addOnCompleteListener { i ->
                            if (i.isSuccessful) {
                                FirebaseStorage.getInstance().reference.child(id).delete()
                                listEmpty.removeAt(index)
                                adapterEmpty.notifyItemRemoved(index)
                                Snackbar.make(tableActivity,
                                    "Xóa thành công ${listEmpty[index].tableName}",
                                    Snackbar.LENGTH_SHORT).show()
                            } else
                                Toast.makeText(this@TableActivity,
                                    "Không thể xóa !",
                                    Toast.LENGTH_SHORT)
                                    .show()
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }
}