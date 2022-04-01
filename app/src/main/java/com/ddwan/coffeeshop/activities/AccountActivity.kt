package com.ddwan.coffeeshop.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ddwan.coffeeshop.R
import kotlinx.android.synthetic.main.activity_account.*

class AccountActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)
        btnPrevious.setOnClickListener {
            finish()
        }
    }
}