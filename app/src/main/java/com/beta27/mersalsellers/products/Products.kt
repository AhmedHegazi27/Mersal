package com.beta27.mersalsellers.products

data class Products(
    var productImage: String = ""
    , var productName: String = ""
    , var productDescription: String = ""
    , var productCategories: String = ""
    , var productPrice: Double = 0.0
    , var discount: Boolean = false
    , var discountPrice: Double = 0.0
    , var productOwnerUid: String = ""
    , var productId: String = ""
    , var discountNote: String = ""
)