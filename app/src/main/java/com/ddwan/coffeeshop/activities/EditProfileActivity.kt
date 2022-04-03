package com.ddwan.coffeeshop.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.ddwan.coffeeshop.Application.Companion.mAuth
import com.ddwan.coffeeshop.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_edit_profile.*

class EditProfileActivity : AppCompatActivity() {

    private lateinit var imageUrl: Uri
    private lateinit var firebaseUserID: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        btnSave.setOnClickListener { registerUser() }
        btnImage.setOnClickListener { selectImage() }
    }

    private fun selectImage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, 100)
    }

    private fun uploadImage(id: String) {
        val fileName = id + "_avatar"
        val storageReference = FirebaseStorage.getInstance().getReference("$fileName")
        storageReference.putFile(imageUrl).addOnSuccessListener {
            Toast.makeText(this, "upload success", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(this, "Upload fail", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK) {
            imageUrl = data?.data!!
            avatar.setImageURI(imageUrl)
        }
    }

    private fun updateUser(id: String) {
        val hashMap = HashMap<String, Any>()
        hashMap["Name"] = edtName.text.toString()
        hashMap["Phone_Number"] = edtPhone.text.toString()
        if (this::imageUrl.isInitialized) {
            hashMap["ImageUrl"] = id + "_avatar"
        } else
            hashMap["ImageUrl"] = "img.png"
        hashMap["Address"] = edtAddress.text.toString()
        hashMap["Role"] = edtRole.text.toString()
        hashMap["Gender"] = if (radioMale.isChecked) "Nam" else "Nữ"
        val refUser =
            FirebaseDatabase.getInstance().reference.child("Users").child(id)
        refUser.updateChildren(hashMap).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Update ok", Toast.LENGTH_LONG).show()
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
                    uploadImage(firebaseUserID)
                    updateUser(firebaseUserID)
                } else {
                    Log.d("checkkk", "k được")
                }
            }
        }
    }
}