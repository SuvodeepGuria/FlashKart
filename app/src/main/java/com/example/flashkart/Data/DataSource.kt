package com.example.flashkart.Data

import com.example.flashkart.R
object DataSource {
    fun loadCategories(): List<Categories> {
        return listOf(
            Categories(R.string.fresh_fruits, R.drawable.fruits),
            Categories(R.string.dry_fruits, R.drawable.dryfriutes),
            Categories(R.string.kitchen_essentials, R.drawable.kitchen),
            Categories(R.string.vegetables, R.drawable.vegetable),
            Categories(R.string.sweet_tooth, R.drawable.sweet_tooth),
            Categories(R.string.stationery, R.drawable.stationery),
            Categories(R.string.pet_food, R.drawable.pet_food),
            Categories(R.string.packaged_food, R.drawable.packaged_food),
            Categories(R.string.munchies, R.drawable.munchies),
            Categories(R.string.cleaning_essentials, R.drawable.cleaning_essentials),
            Categories(R.string.bread_biscuits, R.drawable.bread_biscuits),
            Categories(R.string.beverages, R.drawable.beverages),
            Categories(R.string.bath_body, R.drawable.bath_body)
        )
    }

}