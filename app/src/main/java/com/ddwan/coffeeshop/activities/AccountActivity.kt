package com.ddwan.coffeeshop.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.ddwan.coffeeshop.Application
import com.ddwan.coffeeshop.R
import com.ddwan.coffeeshop.model.Account
import com.ddwan.coffeeshop.viewmodel.MyViewModel
import kotlinx.android.synthetic.main.activity_account.*
import kotlinx.android.synthetic.main.activity_account.btnPrevious
import kotlinx.android.synthetic.main.activity_edit_profile.*

class AccountActivity : AppCompatActivity() {
    val model by lazy {
        ViewModelProvider(this).get(MyViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)
        val bundle = intent.extras
        val account = bundle!!.getSerializable("account") as Account
        accountName.text = account.name
        accountAddress.text = account.address
        accountEmail.text = account.email
        accountGender.text = if (account.gender) "Nam" else "Nữ"
        accountPhone.text = account.phone
        accountRole.text = account.role
        model.loadImage(this, account, accountImage)
        btnPrevious.setOnClickListener {
            backToMain()
        }
        btnEdit.setOnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable("account", account)
            bundle.putBoolean("check", true)
            intent.putExtras(bundle)
            startActivity(intent)
            overridePendingTransition(R.anim.right_to_left,
                R.anim.right_to_left_out)
        }
    }

    override fun onBackPressed() {
        backToMain()
    }

    private fun backToMain(){
        startActivity(Intent(this,
            MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK))
        overridePendingTransition(R.anim.left_to_right,
            R.anim.left_to_right_out)
        finish()
    }
}