package com.ddwan.coffeeshop.activities

import android.content.Intent
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
import com.ddwan.coffeeshop.Application.Companion.mAuth
import com.ddwan.coffeeshop.R
import com.ddwan.coffeeshop.model.Account
import com.ddwan.coffeeshop.model.LoadingDialog
import com.ddwan.coffeeshop.viewmodel.MyViewModel
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_edit_profile.*

class EditProfileActivity : AppCompatActivity() {

    private val dialog by lazy { LoadingDialog(this) }
    private lateinit var imageUrl: Uri
    private lateinit var firebaseUserID: String
    var edit = false
    var account = Account()
    val model by lazy {
        ViewModelProvider(this).get(MyViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        val bundle = intent.extras
        if (bundle != null) {
            account = bundle.getSerializable("account") as Account
            edit = bundle.getBoolean("check")
        }
        val role = resources.getStringArray(R.array.Role)
        val arrayAdapter = ArrayAdapter(this, R.layout.text_view_dropdown, role)
        edtRole.setAdapter(arrayAdapter)
        if (edit) {
            loadInfo()
            btnChangePassword.setOnClickListener {
                Toast.makeText(this, "Hi", Toast.LENGTH_LONG).show()
            }
            btnSave.setOnClickListener {
                if (checkTextChange())
                    insertDataUser(account.id)
                else {
                    finish()
                    overridePendingTransition(R.anim.left_to_right,
                        R.anim.left_to_right_out)
                }
            }
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
            overridePendingTransition(R.anim.left_to_right,
                R.anim.left_to_right_out)
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
                    overridePendingTransition(R.anim.left_to_right,
                        R.anim.left_to_right_out)
                } else {
                    Toast.makeText(this, "Đổi avatar thành công !", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }

    private fun updateImage(id: String) {
        FirebaseStorage.getInstance().reference.child(id).delete()
            .addOnCompleteListener {
                if (it.isSuccessful)
                    uploadImage(id)
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK) {
            imageUrl = data?.data!!
            avatar.setImageURI(imageUrl)
            if (edit) {
                dialog.startLoadingDialog()
                if (account.imageUrl.trim() == "")
                    uploadImage(account.id)
                else
                    updateImage(account.id)
            }
        }
    }

    private fun insertDataUser(id: String) {
        val hashMap = HashMap<String, Any>()
        hashMap["Name"] = edtName.text.toString()
        hashMap["Email"] = edtEmail.text.toString()
        hashMap["Phone_Number"] = edtPhone.text.toString()
        hashMap["Address"] = edtAddress.text.toString()
        hashMap["Role"] = edtRole.text.toString()
        hashMap["Gender"] = radioMale.isChecked
        if (account.id == accountLogin.id) {
            updateDataUserLocal(accountLogin)
        }
        firebaseDB.reference.child("Users").child(id).updateChildren(hashMap)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    if ((!edit && !this::imageUrl.isInitialized) || edit) {
                        Toast.makeText(this, "Thực hiện thao tác thành công !", Toast.LENGTH_LONG)
                            .show()
                        finish()
                        overridePendingTransition(R.anim.left_to_right,
                            R.anim.left_to_right_out)
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
            mAuth.createUserWithEmailAndPassword(edtEmail.text.toString(),
                edtPassword.text.toString()).addOnCompleteListener { it ->
                if (it.isSuccessful) {
                    firebaseUserID = mAuth.currentUser!!.uid
                    if (this::imageUrl.isInitialized) uploadImage(firebaseUserID)
                    insertDataUser(firebaseUserID)
                }
            }
        }
    }

    private fun loadInfo() {
        if (account.id == accountLogin.id)
            btnChangePassword.visibility = View.VISIBLE
        cardViewPassword.visibility = View.GONE
        edtEmail.setText(account.email)
        edtAddress.setText(account.address)
        edtName.setText(account.name)
        edtPhone.setText(account.phone)
        edtRole.setText(account.role)
        if (account.gender) {
            radioMale.isChecked = true
        } else
            radioFemale.isChecked = true
        model.loadImage(this, account.imageUrl, account.id, avatar)
        edtEmail.isEnabled = false
        cardViewEmail.setOnClickListener {
            Toast.makeText(this,
                "Không thể sửa email !!!",
                Toast.LENGTH_SHORT).show()
        }
        if (accountLogin.role != "Quản lý" || accountLogin.id == account.id) {
            edtRole.isEnabled = false
            cardViewRole.setOnClickListener {
                Toast.makeText(this,
                    "Không thể sửa chức vụ !!!",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkTextChange(): Boolean {
        return edtAddress.text.toString() != accountLogin.address
                || edtName.text.toString() != accountLogin.name ||
                edtPhone.text.toString() != accountLogin.phone ||
                edtRole.text.toString() != accountLogin.role ||
                radioMale.isChecked != accountLogin.gender
    }

    private fun updateDataUserLocal(acc: Account) {
        acc.role = edtRole.text.toString()
        acc.name = edtName.text.toString()
        acc.phone = edtPhone.text.toString()
        acc.address = edtAddress.text.toString()
        acc.gender = radioMale.isChecked
    }

}