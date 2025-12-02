package com.example.planty

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.planty.ui.screens.home.HomeScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Wyświetlamy nasz Ekran Główny
                    HomeScreen(
                        onNavigateToAddPlant = {
                            // Tymczasowa akcja - wyświetla dymek
                            Toast.makeText(this, "Kliknięto: Dodaj Roślinę", Toast.LENGTH_SHORT).show()
                        },
                        onNavigateToSettings = {
                            Toast.makeText(this, "Kliknięto: Ustawienia", Toast.LENGTH_SHORT).show()
                        },
                        onNavigateToDetails = { plantId ->
                            Toast.makeText(this, "Kliknięto: Szczegóły rośliny ID $plantId", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }
}