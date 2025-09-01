package com.leo.paleorecipes

import android.content.Intent
import android.os.Bundle
import android.util.Log // Import for Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.leo.paleorecipes.databinding.ActivityMainBinding
import android.net.Uri // Import for Uri
import com.leo.paleorecipes.RecipeListActivity
import com.leo.paleorecipes.AddEditRecipeActivity
import com.leo.paleorecipes.AboutPaleoActivity
import com.leo.paleorecipes.R // Import for R

class MainActivity : AppCompatActivity() {

    // private lateinit var appBarConfiguration: AppBarConfiguration // Remove this line
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("MainActivity", "MainActivity onCreate called") // Add this line

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        // Remove navigation component related code
        // val navController = findNavController(R.id.nav_host_fragment_content_main)
        // appBarConfiguration = AppBarConfiguration(navController.graph)
        // setupActionBarWithNavController(navController, appBarConfiguration)

        // Remove fab related code
        // binding.fab.setOnClickListener {
        //     Snackbar.make(it, "Replace with your own action", Snackbar.LENGTH_LONG)
        //         .setAction("Action", null).show()
        // }

        binding.buttonVisitSocials.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://paleobyleo.blogspot.com/"))
            startActivity(intent)
        }

        binding.buttonAddRecipe.setOnClickListener {
            val intent = Intent(this, AddEditRecipeActivity::class.java)
            startActivity(intent)
        }

        binding.buttonMyRecipes.setOnClickListener {
            val intent = Intent(this, RecipeListActivity::class.java)
            intent.putExtra("isUserRecipes", true)
            startActivity(intent)
        }

        binding.buttonEditRecipes.setOnClickListener {
            val intent = Intent(this, RecipeListActivity::class.java)
            intent.putExtra("isUserRecipes", true)
            intent.putExtra("editMode", true)
            startActivity(intent)
        }

        binding.buttonPrintRecipe.setOnClickListener {
            val intent = Intent(this, RecipeListActivity::class.java)
            intent.putExtra("isUserRecipes", true)
            intent.putExtra("printMode", true)
            startActivity(intent)
        }

        binding.buttonAboutPaleo.setOnClickListener {
            val intent = Intent(this, AboutPaleoActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
// Remove navigation component related code
        return super.onSupportNavigateUp()
    }
}