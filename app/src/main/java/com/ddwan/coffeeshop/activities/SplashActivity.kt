package com.ddwan.coffeeshop.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.ddwan.coffeeshop.R

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Handler().postDelayed({
            startActivity(
                Intent(
                    this,
                    LoginScreenActivity::class.java
                )
            )
            overridePendingTransition(R.anim.right_to_left, R.anim.right_to_left_out)
            finish()
        }, 500)
    }
}