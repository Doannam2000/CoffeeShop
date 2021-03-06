package com.ddwan.coffeeshop.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.PopupMenu
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.ddwan.coffeeshop.Application
import com.ddwan.coffeeshop.Application.Companion.accountLogin
import com.ddwan.coffeeshop.Application.Companion.firebaseDB
import com.ddwan.coffeeshop.Application.Companion.mAuth
import com.ddwan.coffeeshop.R
import com.ddwan.coffeeshop.model.Users
import com.ddwan.coffeeshop.viewmodel.MyViewModel
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_account.*
import kotlinx.android.synthetic.main.activity_account.btnPrevious
import java.lang.Exception

class AccountActivity : AppCompatActivity() {
    val model by lazy {
        ViewModelProvider(this).get(MyViewModel::class.java)
    }
    var account = Users()
    var isChange = false
    var isEmployeeActivity = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)
        val bundle = intent.extras
        account = bundle!!.getSerializable("account") as Users
        isEmployeeActivity = bundle.getBoolean("EmployeeActivity", false)
        setView()
        btnPrevious.setOnClickListener {
            backActivity(false)
        }
        btnEdit.setOnClickListener {
            if (account.userId == accountLogin.userId) {
                nextEditActivity()
            } else {
                val popupMenu = PopupMenu(this, it)
                popupMenu.menuInflater.inflate(R.menu.menu_control, popupMenu.menu)
                popupMenu.setOnMenuItemClickListener { i ->
                    selectedItemMenu(i.itemId)
                    false
                }
                popupMenu.show()
            }

        }
    }


    private fun nextEditActivity() {
        val intent = Intent(this, EditProfileActivity::class.java)
        val bundle = Bundle()
        bundle.putSerializable("account", account)
        bundle.putBoolean("check", true)
        intent.putExtras(bundle)
        startActivityForResult(intent, 123)
        overridePendingTransition(
            R.anim.right_to_left,
            R.anim.right_to_left_out
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        isChange = true
        loadInfoUser()
    }

    private fun setView() {
        accountName.text = account.name
        accountAddress.text = account.address
        accountEmail.text = account.email
        accountGender.text = if (account.gender) "Nam" else "N???"
        accountPhone.text = account.phone
        accountRole.text = model.returnRoleName(account.roleId)
        try {
            model.loadImage(this, account.imageUrl, account.userId, accountImage)
        } catch (e: Exception) {

        }
    }

    private fun loadInfoUser() {
        firebaseDB.getReference("Users").child(account.userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    account.name = snapshot.child("Name").value.toString()
                    account.address =
                        snapshot.child("Address").value.toString()
                    account.phone =
                        snapshot.child("Phone_Number").value.toString()
                    account.roleId = snapshot.child("Role_ID").value.toString()
                    account.gender =
                        snapshot.child("Gender").value as Boolean
                    Application.firebaseStore.reference.child(account.userId).downloadUrl.addOnSuccessListener { Uri ->
                        account.imageUrl = Uri.toString()
                    }.addOnCompleteListener {
                        setView()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }

    private fun selectedItemMenu(id: Int) {
        when (id) {
            R.id.itemRepair -> {
                nextEditActivity()
            }
            R.id.iteMDelete -> {
                val builder = AlertDialog.Builder(this);
                builder.setMessage("B???n c?? ch???c mu???n x??a ${account.name} ?")
                    .setPositiveButton("C??") { _, _ ->
                        deleteAccount(account.userId)
                    }
                    .setNegativeButton("Kh??ng") { _, _ -> }.show()
            }
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
                        snapshot.ref.removeValue().addOnCompleteListener { j ->
                            if (j.isSuccessful) {
                                mAuth.signOut()
                                mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener {
                                    if(it.isSuccessful){
                                        val user = mAuth.currentUser
                                        val credential = EmailAuthProvider.getCredential(email,pass)
                                        user!!.reauthenticate(credential)
                                        user.delete()
                                    }
                                }

                                Toast.makeText(
                                    this@AccountActivity,
                                    "Xo?? th??nh c??ng !",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                isChange = true
                                backActivity(true)
                            } else
                                Toast.makeText(
                                    this@AccountActivity,
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

    override fun onBackPressed() {
        backActivity(false)
        super.onBackPressed()
    }

    private fun backActivity(deleted: Boolean) {
        if (isEmployeeActivity && isChange) {
            val returnIntent = Intent()
            returnIntent.putExtra("Change", deleted)
            setResult(Activity.RESULT_OK, returnIntent)
            finish()
            overridePendingTransition(
                R.anim.left_to_right,
                R.anim.left_to_right_out
            )
        } else {
            finish()
            overridePendingTransition(
                R.anim.left_to_right,
                R.anim.left_to_right_out
            )
        }
    }

}