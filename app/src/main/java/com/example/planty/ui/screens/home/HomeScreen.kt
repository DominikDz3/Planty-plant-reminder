package com.example.planty.ui.screens.home

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.planty.ui.PlantyLightBackground
import com.example.planty.ui.PlantyPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory),
    onNavigateToAddPlant: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToDetails: (Int) -> Unit
) {
    // Stan list z ViewModel
    val plantList by viewModel.plantList.collectAsState()

    Scaffold(
        containerColor = PlantyLightBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Zapisane rośliny →",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = PlantyPrimary
                    )
                },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = "Ustawienia",
                            tint = PlantyPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PlantyLightBackground
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddPlant,
                containerColor = PlantyPrimary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Dodaj roślinę")
            }
        }
    ) { paddingValues ->

        // Lista Roślin
        LazyColumn(
            modifier = Modifier.padding(paddingValues)
        ) {
            items(plantList) { plant ->
                PlantListItem(
                    plant = plant,
                    onClick = { onNavigateToDetails(plant.id) },
                    onDeleteClick = { viewModel.deletePlant(plant) }
                )
            }
        }
    }
}