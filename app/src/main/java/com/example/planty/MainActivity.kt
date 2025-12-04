package com.example.planty

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.planty.ui.screens.add_edit.AddEditPlantScreen
import com.example.planty.ui.screens.home.HomeScreen
import com.example.planty.ui.PlantyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PlantyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    NavHost(navController = navController, startDestination = "home") {

                        composable("home") {
                            HomeScreen(
                                onNavigateToAddPlant = {
                                    navController.navigate("add_plant")
                                },
                                onNavigateToSettings = {
                                    Toast.makeText(this@MainActivity, "Ustawienia - w budowie", Toast.LENGTH_SHORT).show()
                                },
                                onNavigateToDetails = { plantId ->
                                    Toast.makeText(this@MainActivity, "Szczegóły rośliny ID: $plantId", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }

                        composable("add_plant") {
                            AddEditPlantScreen(
                                onNavigateBack = {
                                    navController.popBackStack()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}