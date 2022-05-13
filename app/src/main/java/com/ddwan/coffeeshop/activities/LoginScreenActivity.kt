package com.ddwan.coffeeshop.activities

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.ddwan.coffeeshop.R
import kotlinx.android.synthetic.main.activity_login_screen.*
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Patterns
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.ddwan.coffeeshop.Application
import com.ddwan.coffeeshop.Application.Companion.accountLogin
import com.ddwan.coffeeshop.Application.Companion.firebaseDB
import com.ddwan.coffeeshop.Application.Companion.mAuth
import com.ddwan.coffeeshop.model.LoadingDialog
import com.ddwan.coffeeshop.viewmodel.MyViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.util.regex.Pattern


class LoginScreenActivity : AppCompatActivity() {
    private val dialog by lazy { LoadingDialog(this) }
    val model by lazy {
        ViewModelProvider(this).get(MyViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_screen)
        btnLogin.setOnClickListener {
            if (email.text.toString().isEmpty() || password.text.toString().isEmpty())
                Toast.makeText(this, "Vui lòng điền đủ thông tin !", Toast.LENGTH_SHORT).show()
            else if (!Patterns.EMAIL_ADDRESS.matcher(email.text.toString()).matches())
                Toast.makeText(this, "Định dạng không đúng !", Toast.LENGTH_SHORT).show()
            else {
                dialog.startLoadingDialog()
                login()
            }
        }
        txtGuest.setOnClickListener {
            startActivity(Intent(this,GuestActivity::class.java))
            overridePendingTransition(R.anim.right_to_left,
                R.anim.right_to_left_out)
        }
        forgotPass.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("Bạn muốn lấy lại mật khẩu tài khoản ${email.text.toString()} ?")
                .setPositiveButton("Có") { _, _ ->
                    when {
                        email.text.toString().isEmpty() -> Toast.makeText(this,
                            "Vui lòng điền email cần lấy lại mật khẩu !",
                            Toast.LENGTH_SHORT).show()
                        !Patterns.EMAIL_ADDRESS.matcher(email.text.toString())
                            .matches() -> Toast.makeText(
                            this,
                            "Định dạng không đúng !",
                            Toast.LENGTH_SHORT).show()
                        else -> mAuth.sendPasswordResetEmail(email.text.toString())
                            .addOnCompleteListener {
                                if (it.isSuccessful)
                                    Toast.makeText(this,
                                        "Mật khẩu đã được gửi đến email của bạn !",
                                        Toast.LENGTH_LONG).show()
                                else
                                    Toast.makeText(this,
                                        "Có lỗi, xin hãy thử lại !",
                                        Toast.LENGTH_LONG).show()
                            }
                    }
                }
                .setNegativeButton("Không") { _, _ -> }.show()

        }
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
        accountLogin.userId = user!!.uid
        accountLogin.email = user.email.toString()
        accountLogin.password = password.text.toString()
        // get info user
        firebaseDB.getReference("Users").child(user.uid)
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
                    val imageRef = Application.firebaseStore.reference.child(accountLogin.userId)
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
                    model.loadDataAccount(null)
                    model.loadDataFood(null,null,null,null)
                    getInfoUser()
                    if (Application.listCategory.isEmpty())
                        model.loadDataCategory()
                } else {
                    Toast.makeText(this, "Sai tài khoản hoặc mật khẩu", Toast.LENGTH_SHORT)
                        .show()
                    dialog.stopLoadingDialog()
                }
            }
    }
}