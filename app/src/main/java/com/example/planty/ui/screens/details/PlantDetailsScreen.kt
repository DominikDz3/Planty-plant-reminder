package com.example.planty.ui.screens.details

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantDetailsScreen(
    plantId: Int,
    onNavigateBack: () -> Unit
) {
    val viewModel: PlantDetailsViewModel = viewModel(factory = PlantDetailsViewModel.provideFactory(plantId))
    val plant by viewModel.plant.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.6f), CircleShape)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Wróć",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { paddingValues ->
        if (plant != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = paddingValues.calculateBottomPadding())
                    .padding(bottom = 16.dp)
            ) {
                Box(modifier = Modifier.fillMaxWidth().height(300.dp)) {
                    val photo = plant!!.photoUris.firstOrNull()
                    if (photo != null) {
                        AsyncImage(
                            model = photo,
                            contentDescription = plant!!.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.secondaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Opacity,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .fillMaxWidth()
                            .background(Color.Black.copy(alpha = 0.4f))
                            .padding(16.dp)
                    ) {
                        Text(
                            text = plant!!.name,
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Column(modifier = Modifier.padding(16.dp)) {

                    if (plant!!.description.isNotBlank()) {
                        Text(
                            text = "O roślinie",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = plant!!.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Częstotliwość", style = MaterialTheme.typography.labelMedium)
                                Text("Co ${plant!!.wateringFrequencyDays} dni", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                            }
                            Column {
                                Text("Ostatnie podlanie", style = MaterialTheme.typography.labelMedium)
                                val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                                Text(sdf.format(Date(plant!!.lastWatered)), style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Kalendarz podlewania",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Kliknij w dzień, aby zaznaczyć/odznaczyć podlanie",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    WateringCalendar(
                        history = plant!!.wateringHistory,
                        onDateClick = { date -> viewModel.toggleWateringStatus(date) }
                    )
                }
            }
        } else {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
fun WateringCalendar(
    history: List<Long>,
    onDateClick: (Long) -> Unit
) {
    var displayedMonth by remember { mutableStateOf(Calendar.getInstance()) }

    val monthTitle = remember(displayedMonth.timeInMillis) {
        val sdf = SimpleDateFormat("LLLL yyyy", Locale("pl"))
        val dateStr = sdf.format(displayedMonth.time)
        dateStr.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale("pl")) else it.toString() }
    }

    val daysInMonth = displayedMonth.getActualMaximum(Calendar.DAY_OF_MONTH)

    val firstDayOfMonth = (displayedMonth.clone() as Calendar).apply {
        set(Calendar.DAY_OF_MONTH, 1)
    }

    val startOffset = (firstDayOfMonth.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY + 7) % 7

    val sdfCompare = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
    val historyStrings = history.map { sdfCompare.format(Date(it)) }

    val todayCal = Calendar.getInstance()
    val isCurrentMonth = todayCal.get(Calendar.YEAR) == displayedMonth.get(Calendar.YEAR) &&
            todayCal.get(Calendar.MONTH) == displayedMonth.get(Calendar.MONTH)
    val todayDay = if (isCurrentMonth) todayCal.get(Calendar.DAY_OF_MONTH) else -1

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                val newCal = (displayedMonth.clone() as Calendar)
                newCal.add(Calendar.MONTH, -1)
                displayedMonth = newCal
            }) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Poprzedni miesiąc",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Text(
                text = monthTitle,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            IconButton(onClick = {
                val newCal = (displayedMonth.clone() as Calendar)
                newCal.add(Calendar.MONTH, 1)
                displayedMonth = newCal
            }) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Następny miesiąc",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Column(modifier = Modifier.fillMaxWidth()) {

            Row(modifier = Modifier.fillMaxWidth()) {
                val weekDays = listOf("Pn", "Wt", "Śr", "Cz", "Pt", "Sb", "Nd")
                weekDays.forEach { day ->
                    Text(
                        text = day,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline,
                        modifier = Modifier
                            .weight(1f)
                            .padding(bottom = 8.dp)
                    )
                }
            }

            val calendarDays = mutableListOf<Int?>()
            repeat(startOffset) { calendarDays.add(null) }
            for (day in 1..daysInMonth) { calendarDays.add(day) }

            val weeks = calendarDays.chunked(7)

            weeks.forEach { week ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    for (i in 0 until 7) {
                        if (i < week.size) {
                            val day = week[i]
                            if (day != null) {
                                val cellCal = (displayedMonth.clone() as Calendar).apply {
                                    set(Calendar.DAY_OF_MONTH, day)
                                }
                                val currentDayMillis = cellCal.timeInMillis
                                val dateString = sdfCompare.format(Date(currentDayMillis))

                                val isWatered = historyStrings.contains(dateString)
                                val isToday = (day == todayDay)

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                        .padding(4.dp)
                                        .clip(CircleShape)
                                        .clickable { onDateClick(currentDayMillis) }
                                        .background(
                                            when {
                                                isWatered -> MaterialTheme.colorScheme.primary
                                                isToday -> MaterialTheme.colorScheme.secondaryContainer
                                                else -> Color.Transparent
                                            }
                                        )
                                        .border(
                                            width = 1.dp,
                                            color = if (isToday) MaterialTheme.colorScheme.primary else Color.Transparent,
                                            shape = CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = day.toString(),
                                        color = if (isWatered) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            } else {
                                Box(modifier = Modifier.weight(1f).aspectRatio(1f))
                            }
                        } else {
                            Box(modifier = Modifier.weight(1f).aspectRatio(1f))
                        }
                    }
                }
            }
        }
    }
}