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
        if (model.returnRoleName(listRole[item.groupId].roleId) == "Qu???n l??")
            Toast.makeText(this, "Kh??ng th??? thao t??c v???i ch???c v??? Qu???n l??", Toast.LENGTH_SHORT)
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
                            "Th???c hi???n thao t??c th??nh c??ng !",
                            Toast.LENGTH_LONG
                        ).show()
                        adapter.notifyItemChanged(index)
                    }
                }
        } else {
            Toast.makeText(
                this@RoleActivity,
                "Lo???i ???? t???n t???i",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    private fun confirmDelete(index: Int) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Khi x??a ${listRole[index].roleName} th?? c??c t??i kho???n trong ch???c v??? n??y s??? b??? x??a\nB???n mu???n x??a ch??? ?")
            .setPositiveButton("C??") { _, _ ->
                deleteRole(index)
            }
            .setNegativeButton("Kh??ng") { _, _ -> }.show()
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
                                    "X??a th??nh c??ng ${listRole[index].roleName} !",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                listRole.removeAt(index)
                                adapter.notifyItemRemoved(index)
                            } else
                                Toast.makeText(
                                    this@RoleActivity,
                                    "Kh??ng th??? x??a !",
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
        viewDialog.txtTitle.text = "T??n ch???c v???"
        viewDialog.cancel.setOnClickListener {
            dialog.dismiss()
        }
        viewDialog.oke.setOnClickListener {
            dialog.dismiss()
            if (viewDialog.name.text.isEmpty()) {
                Toast.makeText(
                    this,
                    "T??n ch???c v??? kh??ng ???????c ????? tr???ng",
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
                            "Th???c hi???n thao t??c th??nh c??ng!",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }
        } else {
            Toast.makeText(
                this@RoleActivity,
                "Ch???c v??? n??y ???? t???n t???i",
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