package com.example.planty.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.rounded.LocalFlorist
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.planty.data.database.entity.Plant
import com.example.planty.ui.PlantyPrimary
import com.example.planty.ui.PlantySecondary
import com.example.planty.ui.PlantyTextSecondary

@Composable
fun PlantListItem(
    plant: Plant,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(vertical = 12.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. Obrazek (Zaokrąglony kwadrat)
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(PlantySecondary),
                contentAlignment = Alignment.Center
            ) {
                if (plant.photoUri != null) {
                    AsyncImage(
                        model = plant.photoUri,
                        contentDescription = plant.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.matchParentSize()
                    )
                } else {
                    // Ikona zastępcza (kwiatek) w stylu z PDF
                    Icon(
                        imageVector = Icons.Rounded.LocalFlorist,
                        contentDescription = null,
                        tint = PlantyPrimary.copy(alpha = 0.5f),
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // 2. Teksty (Środek)
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = plant.name,
                    color = PlantyPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Podlewanie co ${plant.wateringFrequencyDays} dni",
                    color = PlantyTextSecondary,
                    fontSize = 14.sp
                )
            }

            // 3. Ikona Menu (Trzy kropki)
            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Opcje",
                    tint = PlantyPrimary
                )
            }
        }

        // Linia oddzielająca (Divider)
        HorizontalDivider(
            color = PlantySecondary.copy(alpha = 0.5f),
            thickness = 1.dp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}