package com.marpal.note_scanner.export

import android.content.Context
import android.os.Environment
import com.marpal.note_scanner.data.NoteEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

class ExportService(private val context: Context) {
    
    private val dateFormatter = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
    
    suspend fun exportNoteAsText(note: NoteEntity, customPath: String? = null): String {
        return withContext(Dispatchers.IO) {
            val exportDir = getExportDirectory(customPath)
            val fileName = sanitizeFileName("${note.title}_${dateFormatter.format(Date(note.timestamp))}.txt")
            val file = File(exportDir, fileName)
            
            FileWriter(file).use { writer ->
                writer.write("Title: ${note.title}\n")
                writer.write("Date: ${SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault()).format(Date(note.timestamp))}\n")
                writer.write("---\n\n")
                writer.write(note.parsedText)
            }
            
            file.absolutePath
        }
    }
    
    suspend fun exportNoteAsPdf(note: NoteEntity, customPath: String? = null): String {
        return withContext(Dispatchers.IO) {
            // For now, we'll create a simple text-based PDF using a basic approach
            // In a full implementation, you'd use a PDF library like iText or similar
            val exportDir = getExportDirectory(customPath)
            val fileName = sanitizeFileName("${note.title}_${dateFormatter.format(Date(note.timestamp))}.pdf")
            val file = File(exportDir, fileName)
            
            // Simple PDF implementation - in reality you'd use a proper PDF library
            // For now, create a text file with .pdf extension as placeholder
            FileWriter(file).use { writer ->
                writer.write("%PDF-1.4\n")
                writer.write("% Simple PDF representation\n")
                writer.write("% Title: ${note.title}\n")
                writer.write("% Date: ${SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault()).format(Date(note.timestamp))}\n")
                writer.write("% Content: ${note.parsedText}\n")
            }
            
            file.absolutePath
        }
    }
    
    suspend fun exportMultipleNotesAsText(notes: List<NoteEntity>, customPath: String? = null): String {
        return withContext(Dispatchers.IO) {
            val exportDir = getExportDirectory(customPath)
            val fileName = "batch_export_${dateFormatter.format(Date())}.txt"
            val file = File(exportDir, fileName)
            
            FileWriter(file).use { writer ->
                writer.write("Batch Export - ${SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault()).format(Date())}\n")
                writer.write("Total Notes: ${notes.size}\n")
                writer.write("=".repeat(50) + "\n\n")
                
                notes.forEachIndexed { index, note ->
                    writer.write("Note ${index + 1}: ${note.title}\n")
                    writer.write("Date: ${SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault()).format(Date(note.timestamp))}\n")
                    writer.write("-".repeat(30) + "\n")
                    writer.write(note.parsedText)
                    writer.write("\n\n")
                }
            }
            
            file.absolutePath
        }
    }
    
    suspend fun exportMultipleNotesAsPdf(notes: List<NoteEntity>, customPath: String? = null): String {
        return withContext(Dispatchers.IO) {
            val exportDir = getExportDirectory(customPath)
            val fileName = "batch_export_${dateFormatter.format(Date())}.pdf"
            val file = File(exportDir, fileName)
            
            // Simple PDF implementation - placeholder
            FileWriter(file).use { writer ->
                writer.write("%PDF-1.4\n")
                writer.write("% Batch Export PDF\n")
                writer.write("% Export Date: ${SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault()).format(Date())}\n")
                writer.write("% Total Notes: ${notes.size}\n")
                
                notes.forEach { note ->
                    writer.write("% Note: ${note.title}\n")
                    writer.write("% Date: ${SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault()).format(Date(note.timestamp))}\n")
                    writer.write("% Content: ${note.parsedText}\n")
                }
            }
            
            file.absolutePath
        }
    }
    
    private fun getExportDirectory(customPath: String?): File {
        val exportDir = if (customPath != null) {
            File(customPath)
        } else {
            File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "NoteScanner")
        }
        
        if (!exportDir.exists()) {
            exportDir.mkdirs()
        }
        
        return exportDir
    }
    
    private fun sanitizeFileName(fileName: String): String {
        return fileName.replace(Regex("[^a-zA-Z0-9._-]"), "_")
    }
    
    fun getAvailableFormats(): List<String> {
        return listOf("TXT", "PDF")
    }
}