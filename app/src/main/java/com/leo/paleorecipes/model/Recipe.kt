package com.leo.paleorecipes.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "recipes")
data class Recipe(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val ingredients: String,
    val instructions: String,
    val prepTime: Int,
    val cookTime: Int,
    val servings: Int,
    val category: String,
    val difficulty: String,
    val notes: String,
    val imageUri: String,
    val isFavorite: Boolean = false,
    val dateAdded: Long = System.currentTimeMillis()
) : Parcelable