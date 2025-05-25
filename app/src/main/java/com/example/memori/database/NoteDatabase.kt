/**
 * NoteDatabase is the main Room database for the application, providing access to
 * note and folder data through DAOs.
 *
 * This database includes two entities:
 * - [NotesEntity]: Represents individual notes.
 * - [FolderEntity]: Represents folders containing notes.
 *
 * The database version is set to 20. The schema is not exported.
 *
 * Provides abstract methods to access:
 * - [NoteDao]: Data access object for notes.
 * - [FoldersDao]: Data access object for folders.
 *
 * The singleton pattern is used to ensure only one instance of the database exists.
 * The [getDatabase] function returns the singleton instance, creating it if necessary.
 * It uses `.fallbackToDestructiveMigration()` to recreate the database if a migration is missing,
 * which will destroy all existing data.
 *
 * @property INSTANCE The singleton instance of the database.
 * @constructor This class should not be instantiated directly.
 */
package com.example.memori.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.memori.database.folder_data.FolderEntity
import com.example.memori.database.folder_data.FoldersDao
import com.example.memori.database.note_data.NoteDao
import com.example.memori.database.note_data.NotesEntity


@Database(entities = [NotesEntity::class, FolderEntity::class], version = 20, exportSchema = false)
abstract class NoteDatabase: RoomDatabase(){

    /**
     * Abstract class representing the Room database for notes and folders.
     *
     * Provides access to DAOs for notes and folders.
     *
     * The companion object implements the singleton pattern to ensure only one instance
     * of the database is created throughout the application's lifecycle.
     *
     * @property noteDao Provides access to note-related database operations.
     * @property folderDao Provides access to folder-related database operations.
     *
     * @constructor This class should not be instantiated directly.
     *
     * @see NoteDao
     * @see FoldersDao
     */
    abstract fun noteDao(): NoteDao
    abstract fun folderDao(): FoldersDao

    companion object {
        /**
         * Contrassegna il campo come volatile, il che significa che le scritture su questo campo sono immediatamente visibili agli altri thread.
         * Questo viene utilizzato per garantire la sicurezza dei thread quando il campo viene accesso contemporaneamente da più thread.
         */
        /**
         * Marks the field as volatile, meaning that writes to this field are immediately made visible to other threads.
         * This is used to ensure thread safety when the field is accessed by multiple threads concurrently.
         */
        @Volatile
        private var INSTANCE: NoteDatabase? = null

        fun getDatabase(context: Context): NoteDatabase {
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NoteDatabase::class.java,
                    "note_database"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

}

//.fallbackToDestructiveMigration() permette di distruggere il database precedente e crearne uno nuovo
