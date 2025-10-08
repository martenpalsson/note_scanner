package com.marpal.note_scanner.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Insert
    suspend fun insertNote(note: NoteEntity): Long

    @Query("SELECT * FROM notes ORDER BY timestamp DESC")
    fun getAllNotes(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE id = :id")
    suspend fun getNoteById(id: Long): NoteEntity?
    
    @Query("DELETE FROM notes WHERE id = :id")
    suspend fun deleteNote(id: Long)
    
    @Query("UPDATE notes SET ocrStatus = :status WHERE id = :id")
    suspend fun updateOcrStatus(id: Long, status: String)
    
    @Query("UPDATE notes SET parsedText = :text, ocrStatus = :status WHERE id = :id")
    suspend fun updateParsedText(id: Long, text: String, status: String)
}