package com.marpal.note_scanner.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.marpal.note_scanner.data.NoteDatabase
import com.marpal.note_scanner.data.NoteEntity
import com.marpal.note_scanner.export.ExportService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextEditingScreen(
    noteId: Long,
    onBackPressed: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val database = remember { NoteDatabase.getDatabase(context) }
    
    var note by remember { mutableStateOf<NoteEntity?>(null) }
    var textFieldValue by remember { mutableStateOf(TextFieldValue("")) }
    var isLoading by remember { mutableStateOf(true) }
    var showExportMenu by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }
    var exportFormat by remember { mutableStateOf("TXT") }
    val snackbarHostState = remember { SnackbarHostState() }

    // Pager state for swipeable views (0 = text editor, 1 = image viewer)
    val pagerState = rememberPagerState(pageCount = { 2 })

    // Load note data
    LaunchedEffect(noteId) {
        scope.launch {
            withContext(Dispatchers.IO) {
                val loadedNote = database.noteDao().getNoteById(noteId)
                note = loadedNote
                loadedNote?.let {
                    textFieldValue = TextFieldValue(it.parsedText)
                }
                isLoading = false
            }
        }
    }

    // Auto-save functionality with debouncing
    LaunchedEffect(textFieldValue.text) {
        kotlinx.coroutines.delay(500) // Wait 500ms after last keystroke
        note?.let {
            withContext(Dispatchers.IO) {
                database.noteDao().updateParsedText(
                    noteId,
                    textFieldValue.text,
                    "completed"
                )
            }
        }
    }

    // Handle back button
    BackHandler {
        onBackPressed()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = note?.title ?: "Loading...",
                        maxLines = 1
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back to notes list"
                        )
                    }
                },
                actions = {
                    // Export menu
                    Box {
                        IconButton(onClick = { showExportMenu = true }) {
                            Icon(
                                imageVector = Icons.Default.FileDownload,
                                contentDescription = "Export options"
                            )
                        }

                        DropdownMenu(
                            expanded = showExportMenu,
                            onDismissRequest = { showExportMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Export as TXT") },
                                onClick = {
                                    exportFormat = "TXT"
                                    showExportDialog = true
                                    showExportMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Export as PDF") },
                                onClick = {
                                    exportFormat = "PDF"
                                    showExportDialog = true
                                    showExportMenu = false
                                }
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) { page ->
                when (page) {
                    0 -> {
                        // Text editing page
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {
                            // Text editing area
                            SelectionContainer {
                                OutlinedTextField(
                                    value = textFieldValue,
                                    onValueChange = { textFieldValue = it },
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .weight(1f),
                                    placeholder = {
                                        Text(
                                            "Start editing your extracted text here...",
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                        )
                                    },
                                    textStyle = androidx.compose.ui.text.TextStyle(
                                        fontSize = 16.sp
                                    ),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                                    )
                                )
                            }

                            // Status indicator
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Changes saved automatically • Swipe to view image",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                        }
                    }
                    1 -> {
                        // Image viewing page
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black),
                            contentAlignment = Alignment.Center
                        ) {
                            note?.let { currentNote ->
                                val imageFile = File(currentNote.imagePath)
                                if (imageFile.exists()) {
                                    Image(
                                        painter = rememberAsyncImagePainter(imageFile),
                                        contentDescription = "Captured note image",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Fit
                                    )
                                } else {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Image,
                                            contentDescription = "Image not found",
                                            tint = Color.White.copy(alpha = 0.5f),
                                            modifier = Modifier.size(64.dp)
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Text(
                                            text = "Image not found",
                                            color = Color.White.copy(alpha = 0.7f),
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                        Text(
                                            text = "Path: ${currentNote.imagePath}",
                                            color = Color.White.copy(alpha = 0.5f),
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }

                                // Hint text at bottom
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .align(Alignment.BottomCenter)
                                        .padding(16.dp)
                                ) {
                                    Text(
                                        text = "Swipe to return to text",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.White.copy(alpha = 0.6f),
                                        modifier = Modifier.align(Alignment.Center)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        
        // Export dialog
        if (showExportDialog) {
            AlertDialog(
                onDismissRequest = { showExportDialog = false },
                title = { Text("Export Note") },
                text = { 
                    Text("Export \"${note?.title ?: "Note"}\" as $exportFormat file to Downloads folder?")
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            note?.let { currentNote ->
                                scope.launch {
                                    try {
                                        val exportService = ExportService(context)
                                        if (exportFormat == "TXT") {
                                            exportService.exportNoteAsText(currentNote)
                                        } else {
                                            exportService.exportNoteAsPdf(currentNote)
                                        }
                                        snackbarHostState.showSnackbar(
                                            message = "Note exported successfully",
                                            duration = SnackbarDuration.Short
                                        )
                                    } catch (e: Exception) {
                                        snackbarHostState.showSnackbar(
                                            message = "Export failed: ${e.message}",
                                            duration = SnackbarDuration.Long
                                        )
                                    }
                                    showExportDialog = false
                                }
                            }
                        }
                    ) {
                        Text("Export")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showExportDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}