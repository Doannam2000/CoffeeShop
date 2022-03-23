package com.ddwan.coffeeshop.activities

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.ddwan.coffeeshop.R

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        ActivityCompat.requestPermissions(this,
            arrayOf(android.Manifest.permission.CALL_PHONE),
            101)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        if (requestCode == 101) {
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
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

}