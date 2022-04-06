package com.ddwan.coffeeshop.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ddwan.coffeeshop.R
import com.ddwan.coffeeshop.activities.MenuActivity
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import kotlinx.android.synthetic.main.fragment_home.view.*

class HomeFragment : Fragment() {

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
        view.cardViewTable.setOnClickListener {
            startActivity(Intent(requireActivity(), MenuActivity::class.java))
        }
        return view
    }
}