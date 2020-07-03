package com.beta27.mersalsellers.info

import android.location.Location

data class Seller(
    var uid: String = "",
    var name: String = "",
    var shopName: String = "",
    var phone: String = "",
    var longitude: Double = 0.0,
    var latitude: Double = 0.0,
    var imageUrl: String = "",
    var active: Boolean = false
)
