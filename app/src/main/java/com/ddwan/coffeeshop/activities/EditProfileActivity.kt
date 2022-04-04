package com.ddwan.coffeeshop.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.ddwan.coffeeshop.Application.Companion.accountLogin
import com.ddwan.coffeeshop.Application.Companion.firebaseDB
import com.ddwan.coffeeshop.Application.Companion.mAuth
import com.ddwan.coffeeshop.R
import com.ddwan.coffeeshop.model.Account
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_edit_profile.*

class EditProfileActivity : AppCompatActivity() {

    private lateinit var imageUrl: Uri
    private lateinit var firebaseUserID: String
    var edit = false
    var account = Account()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        val bundle = intent.extras
        if (bundle != null) {
            account = bundle.getSerializable("account") as Account
            edit = bundle.getBoolean("check")
        }
        if (edit) {
            loadInfo()
            btnChangePassword.setOnClickListener {
                Toast.makeText(this, "Hi", Toast.LENGTH_LONG).show()
            }
            btnSave.setOnClickListener {
                if (this::imageUrl.isInitialized)
                    updateImage(account.id)
                if (checkTextChange())
                    insertDataUser(account.id)
                else
                    Toast.makeText(this, "Không có thông tin nào thay đổi !!!", Toast.LENGTH_SHORT)
                        .show()
            }
        } else
            btnSave.setOnClickListener { registerUser() }
        btnImage.setOnClickListener { selectImage() }
        btnPrevious.setOnClickListener { finish() }
    }

    private fun selectImage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, 100)
    }

    private fun uploadImage(id: String) {
        val storageReference = FirebaseStorage.getInstance().getReference(id)
        storageReference.putFile(imageUrl)
    }

    private fun updateImage(id: String) {
        FirebaseStorage.getInstance().reference.child(id).delete()
            .addOnSuccessListener {
                uploadImage(id)
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK) {
            imageUrl = data?.data!!
            avatar.setImageURI(imageUrl)
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
        if (id == accountLogin.id) {
            accountLogin.role = edtRole.text.toString()
            accountLogin.name = edtName.text.toString()
            accountLogin.phone = edtPhone.text.toString()
            accountLogin.address = edtAddress.text.toString()
            accountLogin.gender = radioMale.isChecked
        }
        firebaseDB.reference.child("Users").child(id).updateChildren(hashMap)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    finish()
                    Toast.makeText(this, "Thực hiện thao tác thành công !", Toast.LENGTH_LONG)
                        .show()
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
                    if (this::imageUrl.isInitialized) {
                        uploadImage(firebaseUserID)
                    }
                    insertDataUser(firebaseUserID)
                } else {

                }
            }
        }
    }

    private fun loadInfo() {
        if(account.id == accountLogin.id)
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
        Glide.with(this)
            .load(account.imageUrl)
            .placeholder(R.drawable.logo)
            .into(avatar)
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
}