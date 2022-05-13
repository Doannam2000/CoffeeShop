package com.ddwan.coffeeshop.model

import java.io.Serializable


class Bill(
    var billId: String,
    var dateCheckIn: String,
    var dateCheckOut: String,
    var tableId: String,
    var userId: String,
    var status: Boolean,
) : Serializable
