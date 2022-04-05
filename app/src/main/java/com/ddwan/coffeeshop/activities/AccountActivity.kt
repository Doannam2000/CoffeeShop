package com.ddwan.coffeeshop.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.ddwan.coffeeshop.Application
import com.ddwan.coffeeshop.R
import com.ddwan.coffeeshop.model.Account
import com.ddwan.coffeeshop.viewmodel.MyViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_account.*
import kotlinx.android.synthetic.main.activity_account.btnPrevious

class AccountActivity : AppCompatActivity() {
    val model by lazy {
        ViewModelProvider(this).get(MyViewModel::class.java)
    }
    var account = Account()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)
        val bundle = intent.extras
        account = bundle!!.getSerializable("account") as Account
        setView()
        btnPrevious.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.left_to_right,
                R.anim.left_to_right_out)
        }
        btnEdit.setOnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable("account", account)
            bundle.putBoolean("check", true)
            intent.putExtras(bundle)
            startActivityForResult(intent, 123)
            overridePendingTransition(R.anim.right_to_left,
                R.anim.right_to_left_out)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        loadInfoUser()
    }

    private fun setView() {
        accountName.text = account.name
        accountAddress.text = account.address
        accountEmail.text = account.email
        accountGender.text = if (account.gender) "Nam" else "Nữ"
        accountPhone.text = account.phone
        accountRole.text = account.role
        model.loadImage(this, account.imageUrl,account.id, accountImage)
    }

    private fun loadInfoUser() {
        Application.firebaseDB.getReference("Users").child(account.id)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    account.name = snapshot.child("Name").value.toString()
                    account.address =
                        snapshot.child("Address").value.toString()
                    account.phone =
                        snapshot.child("Phone_Number").value.toString()
                    account.role = snapshot.child("Role").value.toString()
                    account.gender =
                        snapshot.child("Gender").value as Boolean
                    Application.firebaseStore.reference.child(account.id).downloadUrl.addOnSuccessListener { Uri ->
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
}