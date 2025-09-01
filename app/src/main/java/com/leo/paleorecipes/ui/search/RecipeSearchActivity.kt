package com.leo.paleorecipes.ui.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.leo.paleorecipes.R
import com.leo.paleorecipes.databinding.ActivityRecipeSearchContainerBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecipeSearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecipeSearchContainerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecipeSearchContainerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.recipe_search_title)

        // Get ingredients from intent
        val ingredients = intent.getStringArrayListExtra(EXTRA_INGREDIENTS) ?: emptyList<String>()

        // Add fragment if this is the first creation
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, RecipeSearchFragment.newInstance(ingredients))
                .commit()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        private const val EXTRA_INGREDIENTS = "ingredients"

        fun newIntent(context: Context, ingredients: List<String>): Intent {
            return Intent(context, RecipeSearchActivity::class.java).apply {
                putStringArrayListExtra(EXTRA_INGREDIENTS, ArrayList(ingredients))
            }
        }
    }
}
