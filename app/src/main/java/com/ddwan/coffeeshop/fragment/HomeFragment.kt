package com.ddwan.coffeeshop.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ddwan.coffeeshop.R
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import kotlinx.android.synthetic.main.fragment_home.view.*

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_home, container, false)
        val imageList = ArrayList<SlideModel>()
        imageList.add(SlideModel("https://images.immediate.co.uk/production/volatile/sites/3/2019/04/Avengers-Endgame-Banner-2-de7cf60.jpg?quality=90&resize=620,413","Avengers Endgame"))
        imageList.add(SlideModel("https://img.cinemablend.com/filter:scale/quill/3/7/0/0/8/e/37008e36e98cd75101cf1347396eac8534871a19.jpg?mw=600","Jumanji"))
        imageList.add(SlideModel("https://www.adgully.com/img/800/201711/spider-man-homecoming-banner.jpg","Spider Man"))
        imageList.add(SlideModel("https://live.staticflickr.com/1980/29996141587_7886795726_b.jpg","Venom"))
        view.imageSlider.setImageList(imageList, ScaleTypes.FIT)
        return view
    }
}