package com.ddwan.coffeeshop.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.ddwan.coffeeshop.Application.Companion.accountLogin
import com.ddwan.coffeeshop.Application.Companion.firebaseDB
import com.ddwan.coffeeshop.Application.Companion.firebaseStore
import com.ddwan.coffeeshop.Application.Companion.listAccount
import com.ddwan.coffeeshop.Application.Companion.listRole
import com.ddwan.coffeeshop.Application.Companion.mAuth
import com.ddwan.coffeeshop.R
import com.ddwan.coffeeshop.model.Users
import com.ddwan.coffeeshop.model.LoadingDialog
import com.ddwan.coffeeshop.viewmodel.MyViewModel
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.custom_dialog_change_password.view.*
import kotlinx.android.synthetic.main.custom_editext_dialog.view.cancel
import kotlinx.android.synthetic.main.custom_editext_dialog.view.oke
import java.lang.Exception

class EditProfileActivity : AppCompatActivity() {

    private val dialog by lazy { LoadingDialog(this) }
    private lateinit var imageUrl: Uri
    private lateinit var firebaseUserID: String
    private var edit = false
    private var account = Users()
    private val model by lazy {
        ViewModelProvider(this).get(MyViewModel::class.java)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        val bundle = intent.extras
        if (bundle != null) {
            account = bundle.getSerializable("account") as Users
            edit = bundle.getBoolean("check")
        }
        val arrayAdapter = ArrayAdapter(this, R.layout.text_view_dropdown, getRole())
        edtRole.setAdapter(arrayAdapter)
        if (edit) {
            loadInfo()
            btnChangePassword.setOnClickListener {
                createDialogChangePass()
            }
            btnSave.setOnClickListener {
                if (checkTextChange())
                    insertDataUser(account.userId)
                else {
                    finish()
                    overridePendingTransition(
                        R.anim.left_to_right,
                        R.anim.left_to_right_out
                    )
                }
            }
            txtAccount.text = account.name
        } else
            btnSave.setOnClickListener {
                dialog.startLoadingDialog()
                registerUser()
            }

        btnImage.setOnClickListener {
            selectImage()
        }
        btnPrevious.setOnClickListener {
            finish()
            overridePendingTransition(
                R.anim.left_to_right,
                R.anim.left_to_right_out
            )
        }
    }

    private fun selectImage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, 100)
    }

    private fun uploadImage(id: String) {
        firebaseStore.reference.child(id).putFile(imageUrl).addOnCompleteListener {
            if (it.isSuccessful) {
                dialog.stopLoadingDialog()
                if (!edit) {
                    Toast.makeText(this, "Thực hiện thao tác thành công !", Toast.LENGTH_LONG)
                        .show()
                    finish()
                    overridePendingTransition(
                        R.anim.left_to_right,
                        R.anim.left_to_right_out
                    )
                } else {
                    Toast.makeText(this, "Đổi avatar thành công !", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }

    private fun updateImage(id: String) {
        try {
            FirebaseStorage.getInstance().reference.child(id).delete()
                .addOnCompleteListener {
                }
        } catch (e: Exception) {

        }
        uploadImage(id)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK) {
            imageUrl = data?.data!!
            avatar.setImageURI(imageUrl)
            if (edit) {
                dialog.startLoadingDialog()
                if (account.imageUrl.trim() == "")
                    uploadImage(account.userId)
                else
                    updateImage(account.userId)
            }
        }
    }

    private fun insertDataUser(id: String) {
        val hashMap = HashMap<String, Any>()
        hashMap["Name"] = edtName.text.toString()
        hashMap["Email"] = edtEmail.text.toString()
        hashMap["Phone_Number"] = edtPhone.text.toString()
        hashMap["Address"] = edtAddress.text.toString()
        hashMap["Password"] = edtPassword.text.toString()
        hashMap["Role_ID"] = returnRoleId(edtRole.text.toString())
        hashMap["Gender"] = radioMale.isChecked
        if (account.userId == accountLogin.userId) {
            updateDataUserLocal(accountLogin)
        }
        firebaseDB.reference.child("Users").child(id).updateChildren(hashMap)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    if ((!edit && !this::imageUrl.isInitialized) || edit) {
                        Toast.makeText(this, "Thực hiện thao tác thành công !", Toast.LENGTH_LONG)
                            .show()
                        val returnIntent = Intent()
                        setResult(Activity.RESULT_OK, returnIntent)
                        finish()
                        overridePendingTransition(
                            R.anim.left_to_right,
                            R.anim.left_to_right_out
                        )
                    }
                }
            }
    }

    private fun registerUser() {
        if (edtEmail.text.isEmpty() || edtName.text.isEmpty() || edtAddress.text.isEmpty()
            || edtPhone.text.isEmpty() || edtRole.text.isEmpty() || edtPassword.text.isEmpty()
        ) {
            Toast.makeText(this, "Thông tin không được để trống", Toast.LENGTH_SHORT).show()
        } else {
            if (checkEmail(edtEmail.text.toString()))
                mAuth.createUserWithEmailAndPassword(
                    edtEmail.text.toString(),
                    edtPassword.text.toString()
                ).addOnCompleteListener {
                    if (it.isSuccessful) {
                        firebaseUserID = mAuth.currentUser!!.uid
                        if (this::imageUrl.isInitialized) uploadImage(firebaseUserID)
                        insertDataUser(firebaseUserID)
                        mAuth.signOut()
                        mAuth.signInWithEmailAndPassword(accountLogin.email, accountLogin.password)
                    } else {
                        Toast.makeText(
                            this,
                            "Có lỗi xảy ra không thể tạo tài khoản ",
                            Toast.LENGTH_SHORT
                        ).show()
                        dialog.stopLoadingDialog()
                    }
                }
            else {
                Toast.makeText(
                    this,
                    "Tài khoản này đã tồn tại trong hệ thống !!",
                    Toast.LENGTH_SHORT
                ).show()
                dialog.stopLoadingDialog()
            }
        }
    }

    private fun checkEmail(email: String): Boolean {
        for (item in listAccount) {
            if (item.email == email)
                return false
        }
        return true
    }

    private fun loadInfo() {
        if (account.userId == accountLogin.userId)
            btnChangePassword.visibility = View.VISIBLE
        cardViewPassword.visibility = View.GONE
        edtEmail.setText(account.email)
        edtAddress.setText(account.address)
        edtName.setText(account.name)
        edtPhone.setText(account.phone)
        edtRole.setText(returnRoleName(account.roleId), false)
        if (account.gender)
            radioMale.isChecked = true
        else
            radioFemale.isChecked = true
        model.loadImage(this, account.imageUrl, account.userId, avatar)
        edtEmail.isEnabled = false
        cardViewEmail.setOnClickListener {
            Toast.makeText(
                this,
                "Không thể sửa email !!!",
                Toast.LENGTH_SHORT
            ).show()
        }
        if (model.returnRoleName(accountLogin.roleId) != "Quản lý" || accountLogin.userId == account.userId) {
            edtRole.isEnabled = false
            cardViewRole.setOnClickListener {
                Toast.makeText(
                    this,
                    "Không thể sửa chức vụ !!!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun checkTextChange(): Boolean {
        return edtAddress.text.toString() != accountLogin.address
                || edtName.text.toString() != accountLogin.name ||
                edtPhone.text.toString() != accountLogin.phone ||
                edtRole.text.toString() != accountLogin.roleId ||
                radioMale.isChecked != accountLogin.gender
    }

    private fun updateDataUserLocal(acc: Users) {
        acc.roleId = edtRole.text.toString()
        acc.name = edtName.text.toString()
        acc.phone = edtPhone.text.toString()
        acc.address = edtAddress.text.toString()
        acc.gender = radioMale.isChecked
    }

    private fun createDialogChangePass() {
        val viewDialog = View.inflate(this, R.layout.custom_dialog_change_password, null)
        val builder = AlertDialog.Builder(this)
        builder.setView(viewDialog)
        val dialog = builder.create()
        dialog.show()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        viewDialog.cancel.setOnClickListener {
            dialog.dismiss()
        }
        viewDialog.oke.setOnClickListener {
            if (viewDialog.edtCheckNewPass.text.isEmpty() ||
                viewDialog.edtCurrentPass.text.isEmpty() ||
                viewDialog.edtNewPass.text.isEmpty()
            ) {
                Toast.makeText(this, "Thông tin không được để trống", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (viewDialog.edtCurrentPass.text.toString() != accountLogin.password) {
                Toast.makeText(this, "Mật khẩu hiện tại không đúng", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (viewDialog.edtCurrentPass.text.toString() == viewDialog.edtNewPass.text.toString()) {
                Toast.makeText(
                    this,
                    "Mật khẩu mới không được trùng với mật khẩu hiện tại !",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            if (viewDialog.edtCheckNewPass.text.toString() != viewDialog.edtNewPass.text.toString()) {
                Toast.makeText(this, "Mật khẩu mới không khớp !", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            if (viewDialog.edtNewPass.text.toString().length < 6) {
                Toast.makeText(this, "Mật khẩu phải có 6 kí tự trở lên !", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            dialog.dismiss()
            val newPassword = viewDialog.edtCheckNewPass.text.toString()
            mAuth.currentUser!!.updatePassword(newPassword)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        firebaseDB.reference.child("Users").child(accountLogin.userId)
                            .child("Password").setValue(newPassword).addOnCompleteListener { i ->
                            if (i.isSuccessful) {
                                Toast.makeText(
                                    this,
                                    "Đổi mật khẩu thành công !",
                                    Toast.LENGTH_SHORT
                                ).show()
                                accountLogin.password = newPassword
                            }
                        }
                    } else {
                        Toast.makeText(
                            this,
                            "Đã xảy ra lỗi, xin thử lại sau !",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }

    override fun onBackPressed() {
        finish()
        overridePendingTransition(
            R.anim.left_to_right,
            R.anim.left_to_right_out
        )
        super.onBackPressed()
    }


    private fun getRole(): ArrayList<String> {
        val list = ArrayList<String>()
        for (item in listRole) {
            list.add(item.roleName)
        }
        return list
    }

    private fun returnRoleName(id: String): String {
        for (item in listRole) {
            if (item.roleId == id)
                return item.roleName
        }
        return ""
    }

    private fun returnRoleId(name: String): String {
        for (item in listRole) {
            if (item.roleName == name)
                return item.roleId
        }
        return ""
    }
}