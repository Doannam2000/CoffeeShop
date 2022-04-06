package com.ddwan.coffeeshop.model

import java.io.Serializable

class Food(
    var foodId: String = "",
    var foodName: String = "",
    var category: String = "",
    var price: Int = 0,
    var description: String = "",
    var imageUrl: String = "",
):Serializable