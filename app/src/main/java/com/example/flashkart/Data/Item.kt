package com.example.flashkart.Data

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class Item(
    @StringRes val stringResourceId: Int,
    @StringRes val itemCategoryId: Int,
    val itemQuantity: String,
    val itemPrice: Int,
    @DrawableRes val imageResourceId: Int
)
