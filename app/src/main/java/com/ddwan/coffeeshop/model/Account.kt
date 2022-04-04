package com.ddwan.coffeeshop.model

import java.io.Serializable

class Account constructor(
    var id: String = "",
    var email: String = "",
    var password: String = "",
    var name: String = "",
    var address: String = "",
    var phone: String = "",
    var role: String = "",
    var gender: Boolean = true,
    var imageUrl: String = "",
) : Serializable {
}
