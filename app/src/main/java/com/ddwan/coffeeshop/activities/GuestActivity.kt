package com.ddwan.coffeeshop.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ddwan.coffeeshop.Application.Companion.firebaseDB
import com.ddwan.coffeeshop.Application.Companion.listFood
import com.ddwan.coffeeshop.R
import com.ddwan.coffeeshop.adapter.FoodAdapter
import com.ddwan.coffeeshop.model.Food
import com.ddwan.coffeeshop.model.LoadingDialog
import com.ddwan.coffeeshop.viewmodel.MyViewModel
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_guest.*
import kotlinx.android.synthetic.main.activity_guest.searchView

class GuestActivity : AppCompatActivity() {

    private val listP = ArrayList<Food>()
    private val adapterAddFood by lazy { FoodAdapter(listFood, this, false) }
    private val dialogLoad by lazy { LoadingDialog(this) }
    val model by lazy {
        ViewModelProvider(this).get(MyViewModel::class.java)
    }
    val handle = Handler()
    val run = Runnable {
        dialogLoad.startLoadingDialog()
        val text = searchView.text
        disableView()
        if (listP.size == 0)
            listP.addAll(listFood)
        listFood.clear()
        for (item in listP) {
            if (item.foodName.uppercase().contains(text.toString().uppercase())) {
                listFood.add(item)
            }
        }
        if (listFood.size == listP.size) {
            visibleView()
        }
        adapterAddFood.notifyDataSetChanged()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guest)
        val imageList = ArrayList<SlideModel>()
        imageList.add(SlideModel(R.drawable.image_slide1))
        imageList.add(SlideModel(R.drawable.image_slide2))
        imageList.add(SlideModel(R.drawable.image_slide3))
        imageList.add(SlideModel(R.drawable.image_slide4))
        imageSlider.setImageList(imageList, ScaleTypes.FIT)
        adapterAddFood.setCallBackLoad {
            dialogLoad.stopLoadingDialog()
        }
        btnPrevious.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.left_to_right,
                R.anim.left_to_right_out)
        }
        recyclerGuestFood.adapter = adapterAddFood
        recyclerGuestFood.layoutManager = LinearLayoutManager(this)
        recyclerGuestFood.setHasFixedSize(true)
        searchView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                handle.removeCallbacks(run)
                handle.postDelayed(run, 1000)
            }
        })
        loadTable()
        model.loadDataFood(dialogLoad, "", null, adapterAddFood)
    }


    private fun visibleView() {
        cardView2.visibility = View.VISIBLE
        txtStatus.visibility = View.VISIBLE
        imageView3.visibility = View.VISIBLE
        textView2.visibility = View.VISIBLE
    }

    private fun disableView() {
        cardView2.visibility = View.GONE
        txtStatus.visibility = View.GONE
        imageView3.visibility = View.GONE
        textView2.visibility = View.GONE
    }

    private fun loadTable() {
        firebaseDB.getReference("Table")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        var d = 0
                        for (tb in snapshot.children) {
                            if (tb.child("Status").value as Boolean)
                                d++
                        }
                        if (d == 0) {
                            txtStatus.text = "Tình trạng bàn : Hết bàn trống"
                        } else {
                            txtStatus.text = "Tình trạng bàn : Còn $d bàn trống"
                        }
                    } else {
                        txtStatus.text = "Tình trạng bàn : Hết bàn trống"
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

}