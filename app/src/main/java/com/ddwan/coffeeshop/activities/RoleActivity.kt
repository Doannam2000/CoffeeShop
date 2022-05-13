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
import com.ddwan.coffeeshop.Application.Companion.listRole
import com.ddwan.coffeeshop.Application.Companion.mAuth
import com.ddwan.coffeeshop.R
import com.ddwan.coffeeshop.adapter.RoleAdapter
import com.ddwan.coffeeshop.model.LoadingDialog
import com.ddwan.coffeeshop.model.Role
import com.ddwan.coffeeshop.viewmodel.MyViewModel
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_role.*
import kotlinx.android.synthetic.main.custom_editext_dialog.view.*
import java.lang.Exception

class RoleActivity : AppCompatActivity() {
    val model by lazy {
        ViewModelProvider(this).get(MyViewModel::class.java)
    }
    private val dialogLoad by lazy { LoadingDialog(this) }
    private val adapter by lazy { RoleAdapter(listRole) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_role)
        recyclerViewRole.layoutManager = LinearLayoutManager(this)
        recyclerViewRole.setHasFixedSize(true)
        recyclerViewRole.adapter = adapter
        addRole.setOnClickListener {
            createDialogAddRole(true, null)
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
        if (model.returnRoleName(listRole[item.groupId].roleId) == "Quản lý")
            Toast.makeText(this, "Không thể thao tác với chức vụ Quản lý", Toast.LENGTH_SHORT)
                .show()
        else
            when (item.itemId) {
                127 -> createDialogAddRole(false, item.groupId)
                128 -> confirmDelete(item.groupId)
            }
        return super.onContextItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun loadData() {
        dialogLoad.startLoadingDialog()
        listRole.clear()
        firebaseDB.getReference("Role")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (tb in snapshot.children) {
                            val role = Role(
                                tb.key.toString(),
                                tb.child("Role_Name").value.toString()
                            )
                            listRole.add(role)
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
        for (item in listRole) {
            if (name.uppercase() == item.roleName.uppercase()) {
                return false
            }
        }
        return true
    }

    private fun updateRole(index: Int, name: String) {
        if (checkName(name)) {
            firebaseDB.reference.child("Role").child(listRole[index].roleId)
                .child("Role_Name")
                .setValue(name)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        listRole[index].roleName = name
                        Toast.makeText(
                            this@RoleActivity,
                            "Thực hiện thao tác thành công !",
                            Toast.LENGTH_LONG
                        )
                            .show()
                    }
                }
        } else {
            Toast.makeText(
                this@RoleActivity,
                "Loại đã tồn tại",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    private fun confirmDelete(index: Int) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Khi xóa ${listRole[index].roleName} thì các tài khoản trong chức vụ này sẽ bị xóa\nBạn muốn xóa chứ ?")
            .setPositiveButton("Có") { _, _ ->
                deleteRole(index)
            }
            .setNegativeButton("Không") { _, _ -> }.show()
    }

    private fun deleteRole(index: Int) {
        val id = listRole[index].roleId
        firebaseDB.reference.child("Users")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (item in snapshot.children) {
                            if (item.child("Role_ID").value.toString() == id) {
                                deleteAccount(item.key.toString())
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
        firebaseDB.reference.child("Role").child(id)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        snapshot.ref.removeValue().addOnCompleteListener { i ->
                            if (i.isSuccessful) {
                                FirebaseStorage.getInstance().reference.child(id).delete()
                                Toast.makeText(
                                    this@RoleActivity,
                                    "Xóa thành công ${listRole[index].roleName} !",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                listRole.removeAt(index)
                                adapter.notifyItemRemoved(index)
                            } else
                                Toast.makeText(
                                    this@RoleActivity,
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

    private fun createDialogAddRole(check: Boolean, index: Int?) {
        val viewDialog = View.inflate(this, R.layout.custom_editext_dialog, null)
        val builder = AlertDialog.Builder(this)
        builder.setView(viewDialog)
        val dialog = builder.create()
        dialog.show()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        if (!check) viewDialog.name.setText(listRole[index!!].roleName)
        viewDialog.txtTitle.text = "Tên chức vụ"
        viewDialog.cancel.setOnClickListener {
            dialog.dismiss()
        }
        viewDialog.oke.setOnClickListener {
            dialog.dismiss()
            if (viewDialog.name.text.isEmpty()) {
                Toast.makeText(
                    this,
                    "Tên chức vụ không được để trống",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                if (check)
                    addRole(viewDialog.name.text.toString())
                else {
                    if (viewDialog.name.text.toString() != listRole[index!!].roleName)
                        updateRole(index, viewDialog.name.text.toString())
                }
            }
        }
    }

    private fun addRole(name: String) {
        val hashMap = HashMap<String, Any>()
        val id = model.randomID()
        hashMap["Role_Name"] = name
        if (checkName(name)) {
            firebaseDB.reference.child("Role").child(id)
                .updateChildren(hashMap).addOnCompleteListener {
                    if (it.isSuccessful) {
                        listRole.add(Role(id, name))
                        adapter.notifyDataSetChanged()
                        Toast.makeText(
                            this@RoleActivity,
                            "Thực hiện thao tác thành công!",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }
        } else {
            Toast.makeText(
                this@RoleActivity,
                "Chức vụ này đã tồn tại",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun deleteAccount(id: String) {
        firebaseDB.reference.child("Users").child(id)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        try {
                            FirebaseStorage.getInstance().reference.child(id).delete()
                        } catch (e: Exception) {

                        }
                        val email = snapshot.child("Email").value.toString()
                        val pass = snapshot.child("Password").value.toString()
                        snapshot.ref.removeValue().addOnCompleteListener { i ->
                            if (i.isSuccessful) {
                                mAuth.signOut()
                                mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener {
                                    if(it.isSuccessful){
                                        val user = mAuth.currentUser
                                        val credential = EmailAuthProvider.getCredential(email,pass)
                                        user!!.reauthenticate(credential)
                                        user.delete()
                                    }
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