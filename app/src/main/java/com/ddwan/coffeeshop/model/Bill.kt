package com.ddwan.coffeeshop.model

import java.sql.Date

class Bill(
    var billId: Int,
    var dateCheckIn: Date,
    var dateCheckOut: Date,
    var tableId: Int,
    var status: Boolean,
)
