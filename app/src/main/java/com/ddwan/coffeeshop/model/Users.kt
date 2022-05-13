package com.ddwan.coffeeshop.model

import java.io.Serializable

class Users constructor(
    var userId: String = "",
    var email: String = "",
    var password: String = "",
    var name: String = "",
    var address: String = "",
    var phone: String = "",
    var roleId: String = "",
    var gender: Boolean = true,
    var imageUrl: String = "",
) : Serializable {
}
