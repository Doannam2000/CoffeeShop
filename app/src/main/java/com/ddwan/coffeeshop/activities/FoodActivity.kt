package com.ddwan.coffeeshop.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.ddwan.coffeeshop.Application.Companion.firebaseDB
import com.ddwan.coffeeshop.Application.Companion.firebaseStore
import com.ddwan.coffeeshop.R
import com.ddwan.coffeeshop.model.Food
import com.ddwan.coffeeshop.model.LoadingDialog
import com.ddwan.coffeeshop.viewmodel.MyViewModel
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_food.*
import kotlinx.android.synthetic.main.activity_food.btnPrevious
import kotlinx.android.synthetic.main.activity_food.btnSave

class FoodActivity : AppCompatActivity() {

    private val dialog by lazy { LoadingDialog(this) }
    private lateinit var imageUrl: Uri
    private var edit = false
    val model by lazy {
        ViewModelProvider(this).get(MyViewModel::class.java)
    }
    private var food = Food()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food)
        val bundle = intent.extras
        if (bundle != null) {
            food = bundle.getSerializable("food") as Food
            edit = bundle.getBoolean("check")
        }
        if (edit) {
            loadFood()
            btnSave.setOnClickListener {
                if (checkTextChange()) {
                    dialog.startLoadingDialog()
                    addDataFood(food.foodId)
                } else {
                    finish()
                    overridePendingTransition(R.anim.left_to_right,
                        R.anim.left_to_right_out)
                }
            }
        } else
            btnSave.setOnClickListener {
                dialog.startLoadingDialog()
                addDataFood(model.randomID())
            }

        btnPrevious.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.left_to_right,
                R.anim.left_to_right_out)
        }
        imageEdit.setOnClickListener { selectImage() }
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
                    Toast.makeText(this, "Thay đổi ảnh thành công !", Toast.LENGTH_LONG)
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
            imageViewFood.setImageURI(imageUrl)
            if (edit) {
                dialog.startLoadingDialog()
                if (food.imageUrl.trim() == "")
                    uploadImage(food.foodId)
                else
                    updateImage(food.foodId)
            }
        }
    }

    private fun infoFood(): HashMap<String, Any> {
        val hashMap = HashMap<String, Any>()
        hashMap["Name"] = edtNameFood.text.toString()
        hashMap["Category"] = if (radioDrink.isChecked) "Đồ uống" else "Đồ ăn"
        hashMap["Price"] = edtPrice.text.toString()
        hashMap["Description"] = edtDescription.text.toString()
        return hashMap
    }

    private fun addDataFood(id: String) {
        firebaseDB.reference.child("Food").child(id).updateChildren(infoFood())
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    if (this::imageUrl.isInitialized && !edit) uploadImage(id)
                    else {
                        Toast.makeText(this, "Thực hiện thao tác thành công !", Toast.LENGTH_LONG)
                            .show()
                        dialog.stopLoadingDialog()
                        finish()
                        overridePendingTransition(R.anim.left_to_right,
                            R.anim.left_to_right_out)
                    }
                }
            }
    }

    private fun loadFood() {
        model.loadImage(this, food.imageUrl, food.foodId, imageViewFood)
        edtNameFood.setText(food.foodName)
        if (food.category == "Đồ uống")
            radioDrink.isChecked = true
        else
            radioFood.isChecked = true
        edtPrice.setText(food.price.toString())
        edtDescription.setText(food.description)
    }

    private fun checkTextChange(): Boolean {
        val string = if (radioDrink.isChecked) "Đồ uống" else "Đồ ăn"
        return edtNameFood.text.toString() != food.foodName ||
                string != food.category ||
                edtPrice.text.toString().toInt() != food.price ||
                edtDescription.text.toString() != food.description
    }

    override fun onBackPressed() {
        finish()
        overridePendingTransition(R.anim.left_to_right,
            R.anim.left_to_right_out)
        super.onBackPressed()
    }
}