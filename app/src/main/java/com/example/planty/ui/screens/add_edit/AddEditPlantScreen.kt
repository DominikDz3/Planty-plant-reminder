package com.example.planty.ui.screens.add_edit

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.example.planty.ui.PlantyLightBackground
import com.example.planty.ui.PlantyPrimary
import com.example.planty.ui.PlantySecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditPlantScreen(
    viewModel: AddEditViewModel = viewModel(factory = AddEditViewModel.Factory),
    onNavigateBack: () -> Unit
) {
    val name by viewModel.plantName.collectAsState()
    val description by viewModel.plantDescription.collectAsState()
    val frequency by viewModel.wateringFreq.collectAsState()
    val photoUris by viewModel.photoUris.collectAsState()

    val multiplePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 5),
        onResult = { uris: List<Uri> ->
            viewModel.onPhotosSelected(uris.map { it.toString() })
        }
    )

    Scaffold(
        containerColor = PlantyLightBackground,
        topBar = {
            TopAppBar(
                title = { Text("Nowa roślina", color = PlantyPrimary, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Wróć", tint = PlantyPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PlantyLightBackground)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            if (photoUris.isNotEmpty()) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth().height(160.dp)
                ) {
                    items(photoUris) { uri ->
                        Box(modifier = Modifier.size(150.dp)) {
                            AsyncImage(
                                model = uri,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .matchParentSize()
                                    .clip(RoundedCornerShape(16.dp))
                            )
                            IconButton(
                                onClick = { viewModel.removePhoto(uri) },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(4.dp)
                                    .background(Color.White.copy(alpha = 0.7f), CircleShape)
                                    .size(24.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Usuń", modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                    item {
                        AddPhotoButton(onClick = {
                            multiplePhotoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        })
                    }
                }
            } else {
                AddPhotoButton(
                    modifier = Modifier.size(150.dp),
                    onClick = {
                        multiplePhotoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = name,
                onValueChange = viewModel::onNameChange,
                label = { Text("Nazwa rośliny") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PlantyPrimary,
                    unfocusedBorderColor = PlantyPrimary.copy(alpha = 0.5f)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = description,
                onValueChange = viewModel::onDescriptionChange,
                label = { Text("Opis") },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                shape = RoundedCornerShape(12.dp),
                maxLines = 5,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PlantyPrimary,
                    unfocusedBorderColor = PlantyPrimary.copy(alpha = 0.5f)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = frequency,
                onValueChange = viewModel::onFrequencyChange,
                label = { Text("Częstotliwość podlewania (dni)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PlantyPrimary,
                    unfocusedBorderColor = PlantyPrimary.copy(alpha = 0.5f)
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { viewModel.savePlant(onSuccess = onNavigateBack) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PlantyPrimary),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Zapisz roślinę", fontSize = 18.sp)
            }
        }
    }
}

@Composable
fun AddPhotoButton(modifier: Modifier = Modifier.size(100.dp, 160.dp), onClick: () -> Unit) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(PlantySecondary)
            .clickable { onClick() }
            .border(2.dp, PlantyPrimary.copy(alpha = 0.2f), RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Rounded.AddPhotoAlternate,
                contentDescription = null,
                tint = PlantyPrimary,
                modifier = Modifier.size(32.dp)
            )
            Text("Wybierz zdjęcie", color = PlantyPrimary, fontSize = 12.sp)
        }
    }
}