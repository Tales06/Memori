package com.example.memori.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.memori.database.folder_data.FolderEntity
import com.example.memori.database.folder_data.FoldersDao
import com.example.memori.database.note_data.NoteDao
import com.example.memori.database.note_data.NotesEntity


@Database(entities = [NotesEntity::class, FolderEntity::class], version = 19, exportSchema = false)
abstract class NoteDatabase: RoomDatabase(){

    abstract fun noteDao(): NoteDao
    abstract fun folderDao(): FoldersDao

    companion object {
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
