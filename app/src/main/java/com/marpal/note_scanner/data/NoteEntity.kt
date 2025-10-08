package com.marpal.note_scanner.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val imagePath: String,
    val timestamp: Long,
    val title: String,
    val parsedText: String = "",
    val ocrStatus: String = "pending" // "pending", "processing", "completed", "failed"
)