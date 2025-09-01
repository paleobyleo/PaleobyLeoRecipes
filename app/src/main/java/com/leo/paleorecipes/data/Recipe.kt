package com.leo.paleorecipes.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "recipes")
data class Recipe(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val ingredients: List<String>,
    val instructions: List<String>,
    val prepTime: Int, // in minutes
    val cookTime: Int, // in minutes
    val servings: Int,
    val imageUrl: String = "",
    val isUserCreated: Boolean = false,
    val isFavorite: Boolean = false
) : Parcelable