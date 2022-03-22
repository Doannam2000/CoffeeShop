package com.ddwan.coffeeshop.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.ddwan.coffeeshop.R
import com.ddwan.coffeeshop.fragment.HomeFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        navigationView.setNavigationItemSelectedListener {
            navigationItemSelected(it)
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
        val fragment: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragment.beginTransaction()
        fragmentTransaction.add(R.id.fragmentContainer, HomeFragment(), "homeFragment")
        fragmentTransaction.commit()
    }

    private fun navigationItemSelected(view: MenuItem) {
        when (view.itemId) {
            R.id.itemHome -> {
                Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show()
            }
            R.id.itemLogout -> {
                startActivity(Intent(this, LoginScreenActivity::class.java))
                overridePendingTransition(R.anim.left_to_right, R.anim.left_to_right_out)
                finish()
            }
            R.id.itemFacebook -> {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse("https://fb.com/DoanNamVBHP")
                startActivity(intent)
            }
            R.id.itemPhone -> {
                val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "0397482016"))
                startActivity(intent)
            }
            R.id.itemInfo -> {
                Toast.makeText(this,"Bản quyền thuộc về : Đoàn Duy Nam",Toast.LENGTH_LONG).show()
            }
        }
    }
}