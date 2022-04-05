package com.ddwan.coffeeshop.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.ddwan.coffeeshop.Application.Companion.accountLogin
import com.ddwan.coffeeshop.Application.Companion.firebaseStore
import com.ddwan.coffeeshop.R
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.header.*

class MainActivity : AppCompatActivity() {
    private lateinit var navHostFragment: NavHostFragment
    lateinit var controller: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // setup navigation
        navMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        controller = navHostFragment.navController
        navigationView.setupWithNavController(controller)

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

    override fun onResume() {
        super.onResume()
        initView()
    }
    fun initView(){
        val imageRef = firebaseStore.reference.child(accountLogin.id)
        imageRef.downloadUrl.addOnSuccessListener { Uri ->
            val imageURL = Uri.toString()
            Glide.with(this)
                .load(imageURL)
                .into(imageAccount)
            Glide.with(this)
                .load(imageURL)
                .into(imageHead)
            accountLogin.imageUrl = imageURL
        }
        val navigationView:NavigationView = findViewById(R.id.navigationView)
        val viewHeader = navigationView.getHeaderView(0)
        val name:TextView = viewHeader.findViewById(R.id.nameHead)
        val email:TextView = viewHeader.findViewById(R.id.emailHead)
        name.text = accountLogin.name
        email.text = accountLogin.email
    }
}