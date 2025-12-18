package com.example.planty.ui.screens.details

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Opacity
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
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.example.planty.data.database.entity.TimelineEntry
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

    var showAddTimelineDialog by remember { mutableStateOf(false) }
    var timelineEntryToEdit by remember { mutableStateOf<TimelineEntry?>(null) }
    var timelineEntryToDelete by remember { mutableStateOf<TimelineEntry?>(null) }

    if (showAddTimelineDialog || timelineEntryToEdit != null) {
        val isEditMode = timelineEntryToEdit != null
        val existingTitles = plant?.timeline?.map { it.title } ?: emptyList()

        AddEditTimelineEntryDialog(
            initialEntry = timelineEntryToEdit,
            existingTitles = existingTitles,
            onDismiss = {
                showAddTimelineDialog = false
                timelineEntryToEdit = null
            },
            onConfirm = { title, uris ->
                if (isEditMode) {
                    viewModel.updateTimelineEntry(timelineEntryToEdit!!, title, uris)
                } else {
                    viewModel.addTimelineEntry(title, uris)
                }
                showAddTimelineDialog = false
                timelineEntryToEdit = null
            }
        )
    }

    // Dialog potwierdzenia usuwania etapu
    if (timelineEntryToDelete != null) {
        AlertDialog(
            onDismissRequest = { timelineEntryToDelete = null },
            title = { Text("Usuń etap") },
            text = { Text("Czy na pewno chcesz usunąć etap \"${timelineEntryToDelete?.title}\"?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteTimelineEntry(timelineEntryToDelete!!)
                    timelineEntryToDelete = null
                }) {
                    Text("Usuń", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { timelineEntryToDelete = null }) {
                    Text("Anuluj")
                }
            }
        )
    }

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
                    .padding(bottom = 32.dp)
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

                    Spacer(modifier = Modifier.height(32.dp))

                    // --- GALERIA WZROSTU (OS CZASU) ---
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Galeria wzrostu",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(onClick = { showAddTimelineDialog = true }) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Dodaj etap",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (plant!!.timeline.isEmpty()) {
                        Text(
                            text = "Brak zdjęć w galerii. Dodaj pierwszy etap, np. '1 Tydzień'.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    } else {
                        plant!!.timeline.forEach { entry ->
                            TimelineItem(
                                entry = entry,
                                onEditClick = { timelineEntryToEdit = entry },
                                onDeleteClick = { timelineEntryToDelete = entry }
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
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
fun TimelineItem(
    entry: TimelineEntry,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = entry.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                    Text(
                        text = sdf.format(Date(entry.date)),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Opcje")
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Edytuj") },
                            onClick = {
                                showMenu = false
                                onEditClick()
                            },
                            leadingIcon = { Icon(Icons.Default.Edit, null) }
                        )
                        DropdownMenuItem(
                            text = { Text("Usuń") },
                            onClick = {
                                showMenu = false
                                onDeleteClick()
                            },
                            leadingIcon = { Icon(Icons.Default.Delete, null) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(entry.photos) { uri ->
                    AsyncImage(
                        model = uri,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.secondaryContainer)
                    )
                }
            }
        }
    }
}

@Composable
fun AddEditTimelineEntryDialog(
    initialEntry: TimelineEntry? = null,
    existingTitles: List<String>,
    onDismiss: () -> Unit,
    onConfirm: (String, List<String>) -> Unit
) {
    var title by remember { mutableStateOf(initialEntry?.title ?: "") }
    var selectedUris by remember { mutableStateOf(initialEntry?.photos ?: emptyList()) }
    var titleError by remember { mutableStateOf<String?>(null) }

    fun validateTitle(newTitle: String) {
        val isNameTaken = existingTitles.any { it.equals(newTitle, ignoreCase = true) }
        val isSameAsInitial = initialEntry != null && initialEntry.title.equals(newTitle, ignoreCase = true)

        titleError = if (isNameTaken && !isSameAsInitial) {
            "Taki etap już istnieje!"
        } else {
            null
        }
        title = newTitle
    }

    val photoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(5)
    ) { uris ->
        selectedUris = selectedUris + uris.map { it.toString() }
    }

    val isFormValid = title.isNotBlank() && selectedUris.isNotEmpty() && titleError == null

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = if (initialEntry == null) "Dodaj etap wzrostu" else "Edytuj etap",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { validateTitle(it) },
                    label = { Text("Nazwa etapu (np. 1 Tydzień)") },
                    singleLine = true,
                    isError = titleError != null,
                    supportingText = { if (titleError != null) Text(titleError!!) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Zdjęcia (wymagane min. 1):",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (selectedUris.isEmpty()) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(selectedUris) { uriString ->
                        Box {
                            AsyncImage(
                                model = uriString,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            )
                            IconButton(
                                onClick = { selectedUris = selectedUris - uriString },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .size(20.dp)
                                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                            ) {
                                Icon(Icons.Default.Close, null, tint = Color.White, modifier = Modifier.padding(2.dp))
                            }
                        }
                    }
                    item {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.secondaryContainer)
                                .clickable {
                                    photoLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.AddPhotoAlternate, null, tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Anuluj") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (isFormValid) {
                                onConfirm(title, selectedUris)
                            }
                        },
                        enabled = isFormValid
                    ) {
                        Text("Zapisz")
                    }
                }
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
    val firstDayOfMonth = (displayedMonth.clone() as Calendar).apply { set(Calendar.DAY_OF_MONTH, 1) }
    val startOffset = (firstDayOfMonth.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY + 7) % 7
    val sdfCompare = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
    val historyStrings = history.map { sdfCompare.format(Date(it)) }
    val todayCal = Calendar.getInstance()
    val isCurrentMonth = todayCal.get(Calendar.YEAR) == displayedMonth.get(Calendar.YEAR) &&
            todayCal.get(Calendar.MONTH) == displayedMonth.get(Calendar.MONTH)
    val todayDay = if (isCurrentMonth) todayCal.get(Calendar.DAY_OF_MONTH) else -1

    Column {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { val n = (displayedMonth.clone() as Calendar); n.add(Calendar.MONTH, -1); displayedMonth = n }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Poprzedni", tint = MaterialTheme.colorScheme.primary)
            }
            Text(monthTitle, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            IconButton(onClick = { val n = (displayedMonth.clone() as Calendar); n.add(Calendar.MONTH, 1); displayedMonth = n }) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, "Następny", tint = MaterialTheme.colorScheme.primary)
            }
        }
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.fillMaxWidth()) {
                listOf("Pn", "Wt", "Śr", "Cz", "Pt", "Sb", "Nd").forEach { day ->
                    Text(day, textAlign = TextAlign.Center, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline, modifier = Modifier.weight(1f).padding(bottom = 8.dp))
                }
            }
            val calendarDays = mutableListOf<Int?>()
            repeat(startOffset) { calendarDays.add(null) }
            for (day in 1..daysInMonth) { calendarDays.add(day) }
            calendarDays.chunked(7).forEach { week ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    for (i in 0 until 7) {
                        if (i < week.size) {
                            val day = week[i]
                            if (day != null) {
                                val cellCal = (displayedMonth.clone() as Calendar).apply { set(Calendar.DAY_OF_MONTH, day) }
                                val currentDayMillis = cellCal.timeInMillis
                                val dateString = sdfCompare.format(Date(currentDayMillis))
                                val isWatered = historyStrings.contains(dateString)
                                val isToday = (day == todayDay)
                                Box(
                                    modifier = Modifier.weight(1f).aspectRatio(1f).padding(4.dp).clip(CircleShape)
                                        .clickable { onDateClick(currentDayMillis) }
                                        .background(when { isWatered -> MaterialTheme.colorScheme.primary; isToday -> MaterialTheme.colorScheme.secondaryContainer; else -> Color.Transparent })
                                        .border(width = 1.dp, color = if (isToday) MaterialTheme.colorScheme.primary else Color.Transparent, shape = CircleShape),
                                    contentAlignment = Alignment.Center
                                ) { Text(day.toString(), color = if (isWatered) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface) }
                            } else { Box(modifier = Modifier.weight(1f).aspectRatio(1f)) }
                        } else { Box(modifier = Modifier.weight(1f).aspectRatio(1f)) }
                    }
                }
            }
        }
    }
}