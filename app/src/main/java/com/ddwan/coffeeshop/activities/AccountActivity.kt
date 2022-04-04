package com.ddwan.coffeeshop.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.ddwan.coffeeshop.Application
import com.ddwan.coffeeshop.R
import com.ddwan.coffeeshop.model.Account
import kotlinx.android.synthetic.main.activity_account.*

class AccountActivity : AppCompatActivity() {
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
        Glide.with(this).load(account.imageUrl).into(accountImage)
        btnPrevious.setOnClickListener {
            finish()
        }
        btnEdit.setOnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable("account", account)
            bundle.putBoolean("check", true)
            intent.putExtras(bundle)
            startActivity(intent)
        }

    }
}