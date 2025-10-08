package com.marpal.note_scanner.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.FormatUnderlined
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.marpal.note_scanner.data.NoteDatabase
import com.marpal.note_scanner.data.NoteEntity
import com.marpal.note_scanner.export.ExportService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
    var isBold by remember { mutableStateOf(false) }
    var isItalic by remember { mutableStateOf(false) }
    var isUnderlined by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    var showExportMenu by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }
    var exportFormat by remember { mutableStateOf("TXT") }
    var exportMessage by remember { mutableStateOf<String?>(null) }

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

    // Auto-save functionality
    LaunchedEffect(textFieldValue.text) {
        note?.let {
            scope.launch {
                withContext(Dispatchers.IO) {
                    database.noteDao().updateParsedText(
                        noteId, 
                        textFieldValue.text, 
                        "completed"
                    )
                }
            }
        }
    }

    // Handle back button
    BackHandler {
        onBackPressed()
    }

    Scaffold(
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
                    // Formatting toolbar
                    Row {
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
                        IconButton(
                            onClick = { isBold = !isBold }
                        ) {
                            Icon(
                                imageVector = Icons.Default.FormatBold,
                                contentDescription = "Bold",
                                tint = if (isBold) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                        IconButton(
                            onClick = { isItalic = !isItalic }
                        ) {
                            Icon(
                                imageVector = Icons.Default.FormatItalic,
                                contentDescription = "Italic",
                                tint = if (isItalic) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                        IconButton(
                            onClick = { isUnderlined = !isUnderlined }
                        ) {
                            Icon(
                                imageVector = Icons.Default.FormatUnderlined,
                                contentDescription = "Underline",
                                tint = if (isUnderlined) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
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
                            fontSize = 16.sp,
                            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
                            fontStyle = if (isItalic) FontStyle.Italic else FontStyle.Normal,
                            textDecoration = if (isUnderlined) TextDecoration.Underline else TextDecoration.None
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
                        text = "Changes saved automatically",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
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
                                        val filePath = if (exportFormat == "TXT") {
                                            exportService.exportNoteAsText(currentNote)
                                        } else {
                                            exportService.exportNoteAsPdf(currentNote)
                                        }
                                        exportMessage = "File exported to: $filePath"
                                    } catch (e: Exception) {
                                        exportMessage = "Export failed: ${e.message}"
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
        
        // Export result message
        exportMessage?.let { message ->
            LaunchedEffect(message) {
                kotlinx.coroutines.delay(3000)
                exportMessage = null
            }
        }
    }
}