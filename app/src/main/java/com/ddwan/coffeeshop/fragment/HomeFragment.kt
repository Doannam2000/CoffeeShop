package com.ddwan.coffeeshop.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ddwan.coffeeshop.Application.Companion.accountLogin
import com.ddwan.coffeeshop.Application.Companion.firebaseDB
import com.ddwan.coffeeshop.R
import com.ddwan.coffeeshop.activities.BillInfoActivity
import com.ddwan.coffeeshop.activities.EmployeeActivity
import com.ddwan.coffeeshop.activities.MenuActivity
import com.ddwan.coffeeshop.activities.TableActivity
import com.ddwan.coffeeshop.adapter.BillAdapter
import com.ddwan.coffeeshop.model.Bill
import com.ddwan.coffeeshop.model.LoadingDialog
import com.ddwan.coffeeshop.viewmodel.MyViewModel
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_home.view.*

class HomeFragment : Fragment() {

    val list = ArrayList<Bill>()
    private val adapter by lazy { BillAdapter(list) }
    private val dialogLoad by lazy { LoadingDialog(requireActivity()) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        val imageList = ArrayList<SlideModel>()
        imageList.add(SlideModel(R.drawable.image_slide1))
        imageList.add(SlideModel(R.drawable.image_slide2))
        imageList.add(SlideModel(R.drawable.image_slide3))
        imageList.add(SlideModel(R.drawable.image_slide4))
        view.imageSlider.setImageList(imageList, ScaleTypes.FIT)
        view.cardViewMenu.setOnClickListener {
            startActivity(Intent(requireActivity(), MenuActivity::class.java))
            activity?.overridePendingTransition(
                R.anim.right_to_left,
                R.anim.right_to_left_out
            )
        }
        view.cardViewTable.setOnClickListener {
            startActivity(Intent(requireActivity(), TableActivity::class.java))
            activity?.overridePendingTransition(
                R.anim.right_to_left,
                R.anim.right_to_left_out
            )
        }
        view.cardViewEmployee.setOnClickListener {
            if (ViewModelProvider(requireActivity()).get(MyViewModel::class.java).returnRoleName(
                    accountLogin.roleId
                ) == "Quản lý"
            ) {
                startActivity(Intent(requireActivity(), EmployeeActivity::class.java))
                activity?.overridePendingTransition(
                    R.anim.right_to_left,
                    R.anim.right_to_left_out
                )
            } else
                Toast.makeText(
                    requireContext(),
                    "Bạn không có quyền sử dụng chức năng này",
                    Toast.LENGTH_SHORT
                ).show()
        }
        adapter.setCallBack {
            val bundle = Bundle()
            bundle.putSerializable("Bill", list[it])
            val intent = Intent(requireActivity(), BillInfoActivity::class.java)
            intent.putExtras(bundle)
            startActivity(intent)
            activity?.overridePendingTransition(
                R.anim.right_to_left,
                R.anim.right_to_left_out
            )
        }
        adapter.setCallBackLoad { dialogLoad.stopLoadingDialog() }
        view.recyclerViewBillInHome.layoutManager = LinearLayoutManager(requireContext())
        view.recyclerViewBillInHome.setHasFixedSize(true)
        view.recyclerViewBillInHome.adapter = adapter
        loadData()
        return view
    }

    private fun loadData() {
        list.clear()
        dialogLoad.startLoadingDialog()
        firebaseDB.reference.child("Bill")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for ((i, item) in snapshot.children.reversed().withIndex()) {
                            if ((item.child("Status").value as Boolean)) {
                                list.add(
                                    Bill(
                                        item.key.toString(),
                                        item.child("Date_Check_In").value.toString(),
                                        item.child("Date_Check_Out").value.toString(),
                                        item.child("Table_ID").value.toString(),
                                        item.child("User_ID").value.toString(),
                                        item.child("Status").value as Boolean
                                    )
                                )
                            }
                            if (i == 4)
                                break
                        }
                        adapter.notifyDataSetChanged()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

}