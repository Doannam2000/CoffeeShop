package com.ddwan.coffeeshop.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.ddwan.coffeeshop.R
import kotlinx.android.synthetic.main.activity_login_screen.*
import android.content.pm.PackageManager
import android.net.Uri
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.ddwan.coffeeshop.Application
import com.ddwan.coffeeshop.Application.Companion.accountLogin
import com.ddwan.coffeeshop.Application.Companion.mAuth
import com.ddwan.coffeeshop.model.LoadingDialog
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener


class LoginScreenActivity : AppCompatActivity() {
    private val dialog by lazy { LoadingDialog(this) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_screen)

        btnLogin.setOnClickListener {
            if (email.text.toString().isEmpty() || password.text.toString().isEmpty())
                Toast.makeText(this, "Vui lòng điền đủ thông tin !", Toast.LENGTH_SHORT).show()
            else {
                dialog.startLoadingDialog()
                login()
            }
        }
        help.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.CALL_PHONE),
                    101)
            } else {
                val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "0397482016"))
                startActivity(intent)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        if (requestCode == 101) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "0397482016"))
                startActivity(intent)
            } else {
                Toast.makeText(this, "Không đủ quyền để thực hiện !!!", Toast.LENGTH_SHORT).show()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onBackPressed() {
        val homeIntent = Intent(Intent.ACTION_MAIN)
        homeIntent.addCategory(Intent.CATEGORY_HOME)
        homeIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(homeIntent)
    }

    fun nextActivity() {
        val intent = Intent(this,
            MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        overridePendingTransition(R.anim.right_to_left,
            R.anim.right_to_left_out)
        dialog.stopLoadingDialog()
        finish()
    }


    private fun getInfoUser() {
        // get current user
        val user = mAuth.currentUser
        accountLogin.id = user!!.uid
        accountLogin.email = user.email.toString()
        accountLogin.password = password.text.toString()
        // get info user
        Application.firebaseDB.getReference("Users").child(user.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    accountLogin.name = snapshot.child("Name").value.toString()
                    accountLogin.address =
                        snapshot.child("Address").value.toString()
                    accountLogin.phone =
                        snapshot.child("Phone_Number").value.toString()
                    accountLogin.role = snapshot.child("Role").value.toString()
                    accountLogin.gender =
                        snapshot.child("Gender").value as Boolean
                    val imageRef = Application.firebaseStore.reference.child(accountLogin.id)
                    imageRef.downloadUrl.addOnSuccessListener { Uri ->
                        accountLogin.imageUrl = Uri.toString()
                    }.addOnCompleteListener {
                        progress_circular.visibility = View.GONE
                        nextActivity()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }

    private fun login() {
        mAuth.signInWithEmailAndPassword(email.text.toString(), password.text.toString())
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    getInfoUser()
                } else
                    Toast.makeText(this, "Sai tài khoản hoặc mật khẩu", Toast.LENGTH_SHORT)
                        .show()

            }
    }
}