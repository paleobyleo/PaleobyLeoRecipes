package com.leo.paleorecipes.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.leo.paleorecipes.R

class IngredientsAdapter(
    private val onRemoveClick: (String) -> Unit,
) : ListAdapter<String, IngredientsAdapter.IngredientViewHolder>(IngredientDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ingredient, parent, false)
        return IngredientViewHolder(view, onRemoveClick)
    }

    override fun onBindViewHolder(holder: IngredientViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class IngredientViewHolder(
        itemView: View,
        private val onRemoveClick: (String) -> Unit,
    ) : RecyclerView.ViewHolder(itemView) {
        private val ingredientName: TextView = itemView.findViewById(R.id.ingredientName)
        private val removeButton: ImageButton = itemView.findViewById(R.id.removeButton)

        fun bind(ingredient: String) {
            ingredientName.text = ingredient
            removeButton.setOnClickListener {
                onRemoveClick(ingredient)
            }
        }
    }
}

class IngredientDiffCallback : DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem.equals(newItem, ignoreCase = true)
    }

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }
}
