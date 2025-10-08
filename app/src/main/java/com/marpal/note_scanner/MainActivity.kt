package com.marpal.note_scanner

import android.app.Activity
import android.os.Bundle
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.BackHandler
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs
import kotlin.math.roundToInt
import coil.compose.AsyncImage
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.launch
import com.marpal.note_scanner.camera.CameraActivity
import com.marpal.note_scanner.data.NoteDatabase
import com.marpal.note_scanner.data.NoteEntity
import com.marpal.note_scanner.export.ExportService
import com.marpal.note_scanner.ocr.OcrService
import com.marpal.note_scanner.ui.ModernScrollIndicator
import com.marpal.note_scanner.ui.ScrollIndicatorContainer
import com.marpal.note_scanner.ui.TextEditingScreen
import com.marpal.note_scanner.ui.theme.Note_scannerTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class SortType {
    TITLE, TIMESTAMP_ASC, TIMESTAMP_DESC
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Note_scannerTheme {
                NotesView()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var currentView by remember { mutableStateOf("Capture") }

    BackHandler(enabled = drawerState.isOpen) {
        scope.launch {
            drawerState.close()
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = true,
        drawerContent = {
            SideMenu(
                onNavigate = { view ->
                    currentView = view
                    scope.launch {
                        drawerState.close()
                    }
                }
            )
        }
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = { Text("Note Scanner") }
                )
            }
        ) { innerPadding ->
            MainContent(
                currentView = currentView,
                modifier = Modifier
                    .padding(innerPadding)
                    .background(Color.Gray)
            )
        }
    }
}

@Composable
fun SideMenu(onNavigate: (String) -> Unit) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val drawerWidth = screenWidth / 2

    Column(
        modifier = Modifier
            .width(drawerWidth)
            .fillMaxHeight()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        NavigationDrawerItem(
            label = { Text("Capture", color = Color.White) },
            selected = false,
            onClick = { onNavigate("Capture") }
        )
        NavigationDrawerItem(
            label = { Text("Notes", color = Color.White) },
            selected = false,
            onClick = { onNavigate("Notes") }
        )
    }
}


@Composable
fun MainContent(currentView: String, modifier: Modifier = Modifier) {
    NotesView(modifier = modifier)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesView(modifier: Modifier = Modifier) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val database = remember { NoteDatabase.getDatabase(context) }
    val notes by database.noteDao().getAllNotes().collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()
    
    var currentScreen by remember { mutableStateOf("list") } // "list" or "edit"
    var selectedNoteId by remember { mutableStateOf<Long?>(null) }
    var sortType by remember { mutableStateOf(SortType.TIMESTAMP_DESC) }
    var showSortMenu by remember { mutableStateOf(false) }
    var showTitleDialog by remember { mutableStateOf(false) }
    var capturedImagePath by remember { mutableStateOf<String?>(null) }
    var titleText by remember { mutableStateOf("") }
    var showBatchExportMenu by remember { mutableStateOf(false) }
    var showBatchExportDialog by remember { mutableStateOf(false) }
    var batchExportFormat by remember { mutableStateOf("TXT") }
    
    val listState = rememberLazyListState()
    
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imagePath = result.data?.getStringExtra("image_path")
            imagePath?.let {
                capturedImagePath = it
                showTitleDialog = true
            }
        }
    }
    
    val sortedNotes = remember(notes, sortType) {
        when (sortType) {
            SortType.TITLE -> notes.sortedBy { it.title.lowercase() }
            SortType.TIMESTAMP_ASC -> notes.sortedBy { it.timestamp }
            SortType.TIMESTAMP_DESC -> notes.sortedByDescending { it.timestamp }
        }
    }

    when (currentScreen) {
        "list" -> {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        title = { Text("Note Scanner") },
                        actions = {
                            // Batch export menu
                            Box {
                                IconButton(onClick = { showBatchExportMenu = true }) {
                                    Icon(
                                        imageVector = Icons.Default.FileDownload,
                                        contentDescription = "Batch export"
                                    )
                                }
                                
                                DropdownMenu(
                                    expanded = showBatchExportMenu,
                                    onDismissRequest = { showBatchExportMenu = false }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("Export All as TXT") },
                                        onClick = {
                                            batchExportFormat = "TXT"
                                            showBatchExportDialog = true
                                            showBatchExportMenu = false
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Export All as PDF") },
                                        onClick = {
                                            batchExportFormat = "PDF"
                                            showBatchExportDialog = true
                                            showBatchExportMenu = false
                                        }
                                    )
                                }
                            }
                        }
                    )
                }
            ) { innerPadding ->
        Box(modifier = modifier
            .fillMaxSize()
            .padding(innerPadding)
            .background(MaterialTheme.colorScheme.background)
        ) {
        if (sortedNotes.isEmpty()) {
            // Empty state
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "No notes",
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.outline
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No notes yet",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Tap the camera button to capture your first note",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
            }
        } else {
            ScrollIndicatorContainer(listState = listState) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                items(sortedNotes, key = { it.id }) { note ->
                SwipeToDeleteItem(
                    note = note,
                    onDelete = { noteToDelete ->
                        scope.launch {
                            withContext(Dispatchers.IO) {
                                database.noteDao().deleteNote(noteToDelete.id)
                            }
                        }
                    },
                    onNoteClick = { noteId ->
                        selectedNoteId = noteId
                        currentScreen = "edit"
                    }
                )
            }
        }
        }
        }
        
        // Bottom-right buttons
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .padding(bottom = 48.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Sort button and dropdown
            Box {
                FloatingActionButton(
                    onClick = { showSortMenu = true },
                    modifier = Modifier
                        .size(56.dp)
                        .shadow(8.dp, RoundedCornerShape(16.dp)),
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                ) {
                    Icon(
                        imageVector = Icons.Default.Sort,
                        contentDescription = "Sort options"
                    )
                }
                
                DropdownMenu(
                    expanded = showSortMenu,
                    onDismissRequest = { showSortMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Title (A-Z)") },
                        onClick = {
                            sortType = SortType.TITLE
                            showSortMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Newest First") },
                        onClick = {
                            sortType = SortType.TIMESTAMP_DESC
                            showSortMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Oldest First") },
                        onClick = {
                            sortType = SortType.TIMESTAMP_ASC
                            showSortMenu = false
                        }
                    )
                }
            }
            
            // Capture button
            FloatingActionButton(
                onClick = {
                    val intent = Intent(context, CameraActivity::class.java)
                    cameraLauncher.launch(intent)
                },
                modifier = Modifier
                    .size(56.dp)
                    .shadow(8.dp, RoundedCornerShape(16.dp)),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Capture Image"
                )
            }
        }
        
        // Title dialog for captured images
        if (showTitleDialog) {
            AlertDialog(
                onDismissRequest = { 
                    showTitleDialog = false
                    titleText = ""
                    capturedImagePath = null
                },
                title = { Text("Add Title") },
                text = {
                    OutlinedTextField(
                        value = titleText,
                        onValueChange = { titleText = it },
                        label = { Text("Enter title for your note") }
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val currentTitle = titleText
                            capturedImagePath?.let { imagePath ->
                                scope.launch {
                                    saveNoteToDatabase(context, imagePath, currentTitle)
                                }
                            }
                            showTitleDialog = false
                            titleText = ""
                            capturedImagePath = null
                        }
                    ) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showTitleDialog = false
                            titleText = ""
                            capturedImagePath = null
                        }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }
        
        // Batch export dialog
        if (showBatchExportDialog) {
            val completedNotes = sortedNotes.filter { it.ocrStatus == "completed" }
            AlertDialog(
                onDismissRequest = { showBatchExportDialog = false },
                title = { Text("Batch Export") },
                text = { 
                    Text("Export ${completedNotes.size} completed notes as $batchExportFormat files to Downloads folder?")
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            scope.launch {
                                try {
                                    val exportService = ExportService(context)
                                    val filePath = if (batchExportFormat == "TXT") {
                                        exportService.exportMultipleNotesAsText(completedNotes)
                                    } else {
                                        exportService.exportMultipleNotesAsPdf(completedNotes)
                                    }
                                    // Could add a toast or snackbar here to show success
                                } catch (e: Exception) {
                                    // Could add error handling here
                                }
                                showBatchExportDialog = false
                            }
                        }
                    ) {
                        Text("Export")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showBatchExportDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
        }
            }
        }
        "edit" -> {
            selectedNoteId?.let { noteId ->
                TextEditingScreen(
                    noteId = noteId,
                    onBackPressed = {
                        currentScreen = "list"
                        selectedNoteId = null
                    }
                )
            }
        }
    }
}

@Composable
fun SwipeToDeleteItem(
    note: NoteEntity,
    onDelete: (NoteEntity) -> Unit,
    onNoteClick: (Long) -> Unit
) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    var showConfirmDialog by remember { mutableStateOf(false) }
    val density = LocalDensity.current
    val swipeThreshold = with(density) { 150.dp.toPx() }
    val transitionStartThreshold = with(density) { 100.dp.toPx() }

    if (showConfirmDialog) {
        // Show confirmation dialog in the same space
        ConfirmDeleteDialog(
            note = note,
            onConfirm = {
                onDelete(note)
                showConfirmDialog = false
            },
            onDismiss = {
                showConfirmDialog = false
                offsetX = 0f
            }
        )
    } else {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        ) {
            // Background placeholder/delete indicator
            SwipeBackground(
                offsetX = offsetX,
                transitionStartThreshold = transitionStartThreshold,
                swipeThreshold = swipeThreshold
            )
            
            // The actual note item that moves
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .offset { IntOffset(offsetX.roundToInt(), 0) }
                    .pointerInput(Unit) {
                        detectHorizontalDragGestures(
                            onDragEnd = {
                                if (abs(offsetX) > swipeThreshold) {
                                    showConfirmDialog = true
                                } else {
                                    offsetX = 0f
                                }
                            }
                        ) { _, dragAmount ->
                            offsetX += dragAmount
                        }
                    }
            ) {
                NoteListItem(
                    note = note,
                    onNoteClick = onNoteClick
                )
            }
        }
    }
}

@Composable
fun SwipeBackground(
    offsetX: Float,
    transitionStartThreshold: Float,
    swipeThreshold: Float
) {
    val absOffsetX = abs(offsetX)
    
    // Calculate transition progress from 100dp to 150dp
    val transitionProgress = when {
        absOffsetX < transitionStartThreshold -> 0f
        absOffsetX >= swipeThreshold -> 1f
        else -> (absOffsetX - transitionStartThreshold) / (swipeThreshold - transitionStartThreshold)
    }
    
    // Colors for the background
    val placeholderColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
    val deleteColor = MaterialTheme.colorScheme.errorContainer
    
    // Interpolate between placeholder and delete colors
    val backgroundColor = lerp(placeholderColor, deleteColor, transitionProgress)
    val iconAlpha = if (absOffsetX > 0) transitionProgress else 0f
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        if (iconAlpha > 0f) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete",
                tint = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = iconAlpha),
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
fun ConfirmDeleteDialog(
    note: NoteEntity,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.errorContainer)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Delete \"${note.title}\"?",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TextButton(
                    onClick = onDismiss
                ) {
                    Text(
                        "Cancel",
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
                
                TextButton(
                    onClick = onConfirm
                ) {
                    Text(
                        "Delete",
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun NoteListItem(
    note: NoteEntity,
    onNoteClick: (Long) -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val scope = rememberCoroutineScope()
    val dateFormatter = remember { 
        SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault()) 
    }
    val formattedDate = dateFormatter.format(Date(note.timestamp))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable { 
                if (note.ocrStatus == "completed") {
                    onNoteClick(note.id)
                }
            },
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
        // Thumbnail image
        AsyncImage(
            model = File(note.imagePath),
            contentDescription = "Note thumbnail",
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )

        // OCR Status indicator
        OcrStatusIndicator(
            ocrStatus = note.ocrStatus,
            onRetryClick = {
                if (note.ocrStatus == "failed") {
                    scope.launch {
                        val ocrService = OcrService(context)
                        ocrService.retryOcr(note.id)
                    }
                }
            },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
        )

        // Title and timestamp overlay
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .background(
                    Color.Black.copy(alpha = 0.6f),
                    RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)
                )
                .padding(12.dp)
        ) {
            Column {
                Text(
                    text = note.title,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = androidx.compose.ui.text.TextStyle(
                        shadow = androidx.compose.ui.graphics.Shadow(
                            color = Color.Black,
                            offset = androidx.compose.ui.geometry.Offset(1f, 1f),
                            blurRadius = 3f
                        )
                    )
                )
                Text(
                    text = formattedDate,
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 12.sp,
                    style = androidx.compose.ui.text.TextStyle(
                        shadow = androidx.compose.ui.graphics.Shadow(
                            color = Color.Black,
                            offset = androidx.compose.ui.geometry.Offset(1f, 1f),
                            blurRadius = 3f
                        )
                    )
                )
            }
        }
        }
    }
}

@Composable
fun OcrStatusIndicator(
    ocrStatus: String,
    onRetryClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    when (ocrStatus) {
        "pending" -> {
            Box(
                modifier = modifier
                    .size(20.dp)
                    .background(MaterialTheme.colorScheme.outline, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "OCR Pending",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
        "processing" -> {
            Box(
                modifier = modifier
                    .size(20.dp)
                    .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
        "completed" -> {
            Box(
                modifier = modifier
                    .size(20.dp)
                    .background(MaterialTheme.colorScheme.tertiary, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "OCR Completed",
                    tint = MaterialTheme.colorScheme.onTertiary,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
        "failed" -> {
            Box(
                modifier = modifier
                    .size(20.dp)
                    .clickable { onRetryClick() }
                    .background(MaterialTheme.colorScheme.error, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "OCR Failed - Tap to retry",
                    tint = MaterialTheme.colorScheme.onError,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}

@Composable
fun DefaultView(currentView: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = currentView,
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }
}

private suspend fun saveNoteToDatabase(context: android.content.Context, imagePath: String, title: String) {
    withContext(Dispatchers.IO) {
        val database = NoteDatabase.getDatabase(context)
        val note = NoteEntity(
            imagePath = imagePath,
            timestamp = System.currentTimeMillis(),
            title = title.ifEmpty { "Untitled Note" },
            parsedText = "",
            ocrStatus = "pending"
        )
        val noteId = database.noteDao().insertNote(note)
        
        // Start OCR processing
        val ocrService = OcrService(context)
        ocrService.processNoteForOcr(noteId)
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    Note_scannerTheme {
        MainScreen()
    }
}