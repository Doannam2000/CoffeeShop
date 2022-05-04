package com.ddwan.coffeeshop.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.bumptech.glide.Glide
import com.ddwan.coffeeshop.Application.Companion.accountLogin
import com.ddwan.coffeeshop.Application.Companion.firebaseStore
import com.ddwan.coffeeshop.R
import com.ddwan.coffeeshop.fragment.ChartFragment
import com.ddwan.coffeeshop.fragment.HomeFragment
import com.ddwan.coffeeshop.fragment.InfoFragment
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.header.*
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    private val fragment: FragmentManager = supportFragmentManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // setup navigation
        navMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        //setup fragment with navigation
        val fragmentTransaction: FragmentTransaction = fragment.beginTransaction()
        fragmentTransaction.add(R.id.fragmentContainerView, HomeFragment(), "HomeFragment").commit()
        fragmentTransaction.addToBackStack("HomeFragment")
        navigationView.setNavigationItemSelectedListener {
            val fragmentTransaction: FragmentTransaction = fragment.beginTransaction()
            val index = supportFragmentManager.backStackEntryCount - 1
            val backEntry = supportFragmentManager.getBackStackEntryAt(index)
            val tag = backEntry.name
            when (it.itemId) {
                R.id.homeFragment -> {
                    if (tag != "HomeFragment") {
                        fragmentTransaction.replace(R.id.fragmentContainerView,
                            HomeFragment(),
                            "FragmentHome").commit()
                        fragmentTransaction.addToBackStack("HomeFragment")
                    }
                    it.isChecked = true
                }
                R.id.chartFragment -> {
                    if (tag != "ChartFragment") {
                        fragmentTransaction.replace(R.id.fragmentContainerView, ChartFragment())
                            .commit()
                        fragmentTransaction.addToBackStack("ChartFragment")
                    }
                    it.isChecked = true
                }
                R.id.tableFragment -> {
                    startActivity(Intent(this, TableActivity::class.java))
                    overridePendingTransition(R.anim.right_to_left,
                        R.anim.right_to_left_out)
                }
                R.id.menuFragment -> {
                    startActivity(Intent(this, MenuActivity::class.java))
                    overridePendingTransition(R.anim.right_to_left,
                        R.anim.right_to_left_out)
                }
                R.id.employeeFragment -> {
                    if (accountLogin.role == "Quản lý") {
                        startActivity(Intent(this, EmployeeActivity::class.java))
                        overridePendingTransition(R.anim.right_to_left,
                            R.anim.right_to_left_out)
                    } else
                        Toast.makeText(this,
                            "Bạn không có quyền sử dụng chức năng này",
                            Toast.LENGTH_SHORT).show()
                }
                R.id.itemFacebook -> {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse("https://fb.com/DoanNamVBHP")
                    startActivity(intent)
                    overridePendingTransition(R.anim.right_to_left,
                        R.anim.right_to_left_out)
                }
                R.id.itemZalo -> {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse("https://zalo.me/0397482016")
                    startActivity(intent)
                    overridePendingTransition(R.anim.right_to_left,
                        R.anim.right_to_left_out)
                }
                R.id.infoFragment -> {
                    if (tag != "InfoFragment") {
                        fragmentTransaction.replace(R.id.fragmentContainerView, InfoFragment())
                            .commit()
                        fragmentTransaction.addToBackStack("InfoFragment")
                    }
                    it.isChecked = true
                }
                R.id.itemLogout -> {
                    startActivity(Intent(this, LoginScreenActivity::class.java))
                    finish()
                    overridePendingTransition(R.anim.left_to_right,
                        R.anim.left_to_right_out)
                }
            }
            drawerLayout.closeDrawers()
            true
        }

        // load image
        imageAccount.setOnClickListener {
            val intent = Intent(this, AccountActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable("account", accountLogin)
            intent.putExtras(bundle)
            startActivity(intent)
            overridePendingTransition(R.anim.right_to_left,
                R.anim.right_to_left_out)
        }

    }

    private fun initView() {
        val imageRef = firebaseStore.reference.child(accountLogin.id)
        imageRef.downloadUrl.addOnSuccessListener { Uri ->
            val imageURL = Uri.toString()
            if (imageURL != "null")
                try {
                    Glide.with(this)
                        .load(imageURL)
                        .into(imageAccount)
                    Glide.with(this)
                        .load(imageURL)
                        .into(imageHead)
                    accountLogin.imageUrl = imageURL
                } catch (e: Exception) {
                }
        }
        val navigationView: NavigationView = findViewById(R.id.navigationView)
        val viewHeader = navigationView.getHeaderView(0)
        val name: TextView = viewHeader.findViewById(R.id.nameHead)
        val email: TextView = viewHeader.findViewById(R.id.emailHead)
        name.text = accountLogin.name
        email.text = accountLogin.email
    }

    override fun onResume() {
        super.onResume()
        initView()
    }
}