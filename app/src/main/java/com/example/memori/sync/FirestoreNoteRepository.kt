package com.example.memori.sync

import com.example.memori.database.note_data.NotesEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await


class FirestoreNoteRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
) {
    /**
     * Repository class for handling synchronization of notes with Firestore.
     *
     * Provides methods to upload, retrieve, insert, and delete notes in the Firestore database
     * under the "users/{userId}/notes" collection path.
     *
     * @param userId The unique identifier of the user whose notes are being managed.
     * @param notes A list of [NotesEntity] objects representing the notes to be uploaded.
     * @param note A single [NotesEntity] object representing the note to be inserted or uploaded.
     * @param noteId The unique identifier of the note to be deleted.
     *
     * Functions:
     * - uploadNote: Uploads a list of notes to Firestore for the specified user.
     * - getAllNotesFromCloud: Retrieves all notes for the specified user from Firestore.
     * - deleteNote: Deletes a specific note for the user from Firestore.
     * - insertOneNote: Inserts a single note for the user into Firestore.
     * - uploadOneNote: Uploads (or updates) a single note for the user in Firestore.
     *
     * All functions are suspend functions and should be called from a coroutine or another suspend function.
     */
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