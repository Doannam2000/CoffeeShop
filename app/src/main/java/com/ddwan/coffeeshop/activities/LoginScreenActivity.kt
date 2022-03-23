package com.ddwan.coffeeshop.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.ddwan.coffeeshop.R
import kotlinx.android.synthetic.main.activity_login_screen.*
import android.content.pm.PackageManager
import android.net.Uri
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


class LoginScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_screen)
        btnLogin.setOnClickListener {
            if (email.text.toString().isEmpty() || password.text.toString().isEmpty())
                Toast.makeText(this, "Vui lòng điền đủ thông tin !", Toast.LENGTH_SHORT).show()
            else {
                if (email.text.toString().uppercase() == "ADMIN" && password.text.toString()
                        .uppercase() == "ADMIN"
                ) {
                    startActivity(Intent(this, MainActivity::class.java))
                    overridePendingTransition(R.anim.right_to_left, R.anim.right_to_left_out)
                    finish()
                } else
                    Toast.makeText(this, "Sai tài khoản hoặc mật khẩu", Toast.LENGTH_SHORT).show()
            }
        }
        help.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.CALL_PHONE),
                    101)
            } else {
                val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "0397482016"))
                startActivity(intent)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        if (requestCode == 101) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "0397482016"))
                startActivity(intent)
            } else {
                Toast.makeText(this, "Không đủ quyền để thực hiện !!!", Toast.LENGTH_SHORT).show()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

}