package com.example.sneakerhub.Models

import java.io.Serializable

data class shoes (
    var shoe_id: String = "",
    var category_id: String = "",
    var name: String = "",
    var price: String = "", // Ensure this is a String if API sends price as String
    var description: String = "",
    var brand_name: String = "",
    var quantity: String = "", // Ensure this is a String if API sends quantity as String
    var photo_url: String = ""
) : Serializable
