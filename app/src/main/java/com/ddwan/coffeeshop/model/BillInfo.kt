package com.ddwan.coffeeshop.model

import java.io.Serializable

class BillInfo(
    var billInfoId: String,
    var billId: String,
    var foodId: String,
    var price: Int,
    var count: Int,
):Serializable
