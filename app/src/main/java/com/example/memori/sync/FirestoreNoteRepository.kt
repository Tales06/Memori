package com.example.memori.sync

import com.example.memori.database.note_data.NotesEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await


class FirestoreNoteRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
) {
    suspend fun uploadNote(userId: String, notes: List<NotesEntity>) {
        val notesRef = db.collection("users")
            .document(userId)
            .collection("notes")

        notes.forEach { note ->
            notesRef.document(note.id.toString()).set(note).await()
        }
    }

    suspend fun getAllNotesFromCloud(userId: String) : List<NotesEntity> {
        val snapshot = db.collection("users")
            .document(userId)
            .collection("notes")
            .get()
            .await()

        return snapshot.documents.mapNotNull { it.toObject(NotesEntity::class.java) }
    }

    suspend fun deleteNote(userId: String, noteId: Int) {
        db.collection("users")
            .document(userId)
            .collection("notes")
            .document(noteId.toString())
            .delete()
            .await()
    }

    suspend fun insertOneNote(userId: String, note: NotesEntity) {
        db.collection("users")
            .document(userId)
            .collection("notes")
            .document(note.id.toString())
            .set(note)
            .await()
    }

    suspend fun uploadOneNote(userId: String, note: NotesEntity) {
        db.collection("users")
            .document(userId)
            .collection("notes")
            .document(note.id.toString())
            .set(note)
            .await()
    }



}