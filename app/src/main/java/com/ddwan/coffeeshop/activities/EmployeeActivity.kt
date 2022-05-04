package com.ddwan.coffeeshop.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ddwan.coffeeshop.Application.Companion.listAccount
import com.ddwan.coffeeshop.R
import com.ddwan.coffeeshop.adapter.EmployeeAdapter
import com.ddwan.coffeeshop.model.Account
import com.ddwan.coffeeshop.viewmodel.MyViewModel
import kotlinx.android.synthetic.main.activity_employee.*
import kotlinx.android.synthetic.main.activity_employee.btnPrevious
import kotlinx.android.synthetic.main.activity_employee.searchView

class EmployeeActivity : AppCompatActivity() {

    val adapter by lazy { EmployeeAdapter(listAccount, this) }
    var position = -1
    val model by lazy {
        ViewModelProvider(this).get(MyViewModel::class.java)
    }
    var listP = ArrayList<Account>()
    val handle = Handler()
    val run = Runnable {
        val text = searchView.text
        listAccount.clear()
        for (item in listP) {
            if (item.name.uppercase().contains(text.toString().uppercase())) {
                listAccount.add(item)
            }
        }
        adapter.notifyDataSetChanged()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_employee)
        adapter.setCallBack {
            val bundle = Bundle()
            position = it
            bundle.putSerializable("account", listAccount[it])
            bundle.putSerializable("EmployeeActivity", true)
            val intent = Intent(this, AccountActivity::class.java)
            intent.putExtras(bundle)
            startActivityForResult(intent, 105)
            overridePendingTransition(R.anim.right_to_left,
                R.anim.right_to_left_out)
        }
        val recyclerViewEmployee: RecyclerView = findViewById(R.id.recyclerView_employee)
        recyclerViewEmployee.layoutManager = LinearLayoutManager(this)
        recyclerViewEmployee.setHasFixedSize(true)
        recyclerViewEmployee.adapter = adapter
        addAccount.setOnClickListener {
            startActivityForResult(Intent(this, EditProfileActivity::class.java), 106)
            overridePendingTransition(R.anim.right_to_left,
                R.anim.right_to_left_out)
        }
        btnPrevious.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.left_to_right,
                R.anim.left_to_right_out)
        }
        adapter.notifyDataSetChanged()
        if (listAccount.isEmpty())
            model.loadDataAccount(adapter)

        listP.addAll(listAccount)
        searchView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                handle.removeCallbacks(run)
                handle.postDelayed(run, 1000)
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK)
            when (requestCode) {
                105 -> {
                    val deleted = data?.getBooleanExtra("Change", false)!!
                    if (deleted) {
                        listAccount.removeAt(position)
                        adapter.notifyItemRemoved(position)
                    } else {
                        model.loadDataAccount(adapter)
                    }
                }
                106 -> {
                    model.loadDataAccount(adapter)
                }
            }

    }

    override fun onBackPressed() {
        finish()
        overridePendingTransition(R.anim.left_to_right,
            R.anim.left_to_right_out)
        super.onBackPressed()
    }
}