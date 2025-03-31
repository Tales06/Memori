package com.example.memori

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [NotesEntity::class, CheckListNoteEntity::class], version = 4, exportSchema = false)
abstract class NoteDatabase: RoomDatabase(){

    abstract fun noteDao(): NoteDao
    abstract fun checkListDao(): CheckListDao

    companion object {
        @Volatile
        private var INSTANCE: NoteDatabase? = null

        fun getDatabase(context: Context): NoteDatabase{
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