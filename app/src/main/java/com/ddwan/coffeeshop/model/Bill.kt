package com.ddwan.coffeeshop.model

import java.sql.Date

class Bill(
    var billId: String,
    var dateCheckIn: Date,
    var dateCheckOut: Date,
    var tableId: String,
    var status: Boolean,
)
