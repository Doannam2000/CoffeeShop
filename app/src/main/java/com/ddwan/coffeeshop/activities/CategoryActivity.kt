package com.ddwan.coffeeshop.activities

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ddwan.coffeeshop.Application.Companion.firebaseDB
import com.ddwan.coffeeshop.Application.Companion.listCategory
import com.ddwan.coffeeshop.R
import com.ddwan.coffeeshop.adapter.CategoryAdapter
import com.ddwan.coffeeshop.model.Category
import com.ddwan.coffeeshop.model.LoadingDialog
import com.ddwan.coffeeshop.viewmodel.MyViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_category.*
import kotlinx.android.synthetic.main.custom_editext_dialog.view.*
import java.lang.Exception

class CategoryActivity : AppCompatActivity() {

    val model by lazy {
        ViewModelProvider(this).get(MyViewModel::class.java)
    }
    private val dialogLoad by lazy { LoadingDialog(this) }
    private val adapter by lazy { CategoryAdapter(listCategory) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)
        recyclerViewCategory.layoutManager = LinearLayoutManager(this)
        recyclerViewCategory.setHasFixedSize(true)
        recyclerViewCategory.adapter = adapter
        addCategory.setOnClickListener {
            createDialogAddCategory(true, null)
        }
        btnPrevious.setOnClickListener {
            finish()
            overridePendingTransition(
                R.anim.left_to_right,
                R.anim.left_to_right_out
            )
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            125 -> createDialogAddCategory(false, item.groupId)
            126 -> confirmDelete(item.groupId)
        }
        return super.onContextItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun loadData() {
        dialogLoad.startLoadingDialog()
        listCategory.clear()
        firebaseDB.getReference("Category")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (tb in snapshot.children) {
                            val category = Category(
                                tb.key.toString(),
                                tb.child("Category_Name").value.toString()
                            )
                            listCategory.add(category)
                        }
                        adapter.notifyDataSetChanged()
                        dialogLoad.stopLoadingDialog()
                    } else {
                        dialogLoad.stopLoadingDialog()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    private fun checkName(name: String): Boolean {
        for (item in listCategory) {
            if (name.uppercase() == item.categoryName.uppercase()) {
                return false
            }
        }
        return true
    }

    private fun updateCategory(index: Int, name: String) {
        if (checkName(name)) {
            firebaseDB.reference.child("Category").child(listCategory[index].categoryId)
                .child("Category_Name")
                .setValue(name)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        listCategory[index].categoryName = name
                        Toast.makeText(
                            this@CategoryActivity,
                            "Thực hiện thao tác thành công !",
                            Toast.LENGTH_LONG
                        ).show()
                        adapter.notifyItemChanged(index)
                    }
                }
        } else {
            Toast.makeText(
                this@CategoryActivity,
                "Loại đã tồn tại",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    private fun confirmDelete(index: Int) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Khi xóa ${listCategory[index].categoryName} thì các món ăn trong loại này sẽ bị xóa\nBạn muốn xóa chứ ?")
            .setPositiveButton("Có") { _, _ ->
                deleteCategory(index)
            }
            .setNegativeButton("Không") { _, _ -> }.show()
    }

    private fun deleteCategory(index: Int) {
        val id = listCategory[index].categoryId
        firebaseDB.reference.child("Food")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (item in snapshot.children) {
                            if (item.child("Category_ID").value.toString() == id) {
                                item.ref.removeValue()
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
        firebaseDB.reference.child("Category").child(id)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        snapshot.ref.removeValue().addOnCompleteListener { i ->
                            if (i.isSuccessful) {
                                Toast.makeText(
                                    this@CategoryActivity,
                                    "Xóa thành công ${listCategory[index].categoryName} !",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                listCategory.removeAt(index)
                                adapter.notifyItemRemoved(index)
                            } else
                                Toast.makeText(
                                    this@CategoryActivity,
                                    "Không thể xóa !",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    private fun createDialogAddCategory(check: Boolean, index: Int?) {
        val viewDialog = View.inflate(this, R.layout.custom_editext_dialog, null)
        val builder = AlertDialog.Builder(this)
        builder.setView(viewDialog)
        val dialog = builder.create()
        dialog.show()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        if (!check) viewDialog.name.setText(listCategory[index!!].categoryName)
        viewDialog.txtTitle.text = "Tên loại"
        viewDialog.cancel.setOnClickListener {
            dialog.dismiss()
        }
        viewDialog.oke.setOnClickListener {
            dialog.dismiss()
            if (viewDialog.name.text.isEmpty()) {
                Toast.makeText(
                    this,
                    "Tên loại không được để trống",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                if (check)
                    addCategory(viewDialog.name.text.toString())
                else {
                    if (viewDialog.name.text.toString() != listCategory[index!!].categoryName)
                        updateCategory(index, viewDialog.name.text.toString())
                }
            }
        }
    }

    private fun addCategory(name: String) {
        val hashMap = HashMap<String, Any>()
        val id = model.randomID()
        hashMap["Category_Name"] = name
        if (checkName(name)) {
            firebaseDB.reference.child("Category").child(id)
                .updateChildren(hashMap).addOnCompleteListener {
                    if (it.isSuccessful) {
                        listCategory.add(Category(id, name))
                        adapter.notifyDataSetChanged()
                        Toast.makeText(
                            this@CategoryActivity,
                            "Thực hiện thao tác thành công!",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }
        } else {
            Toast.makeText(
                this@CategoryActivity,
                "Loại này đã tồn tại",
                Toast.LENGTH_SHORT
            ).show()
        }

    }

}