package com.marpal.note_scanner.ocr

import android.content.Context
import android.graphics.BitmapFactory
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.marpal.note_scanner.data.NoteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class OcrService(private val context: Context) {
    private val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    
    suspend fun processNoteForOcr(noteId: Long) {
        withContext(Dispatchers.IO) {
            val database = NoteDatabase.getDatabase(context)
            val note = database.noteDao().getNoteById(noteId) ?: return@withContext
            
            try {
                // Update status to processing
                database.noteDao().updateOcrStatus(noteId, "processing")
                
                // Load and process image
                val imageFile = File(note.imagePath)
                if (!imageFile.exists()) {
                    database.noteDao().updateOcrStatus(noteId, "failed")
                    return@withContext
                }
                
                val bitmap = BitmapFactory.decodeFile(note.imagePath)
                if (bitmap == null) {
                    database.noteDao().updateOcrStatus(noteId, "failed")
                    return@withContext
                }
                
                val inputImage = InputImage.fromBitmap(bitmap, 0)
                
                // Perform OCR
                textRecognizer.process(inputImage)
                    .addOnSuccessListener { visionText ->
                        val extractedText = visionText.text
                        
                        // Update database with extracted text
                        CoroutineScope(Dispatchers.IO).launch {
                            database.noteDao().updateParsedText(
                                noteId, 
                                extractedText, 
                                "completed"
                            )
                        }
                    }
                    .addOnFailureListener { 
                        // Update status to failed
                        CoroutineScope(Dispatchers.IO).launch {
                            database.noteDao().updateOcrStatus(noteId, "failed")
                        }
                    }
                    
            } catch (e: Exception) {
                database.noteDao().updateOcrStatus(noteId, "failed")
            }
        }
    }
    
    suspend fun retryOcr(noteId: Long) {
        processNoteForOcr(noteId)
    }
}