package com.vishnu.lifenest.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        TaskEntity::class, TaskEntryEntity::class,
        ToDoEntity::class, NoteEntity::class,
        EventEntity::class, LedgerEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao
    abstract fun toDoDao(): ToDoDao
    abstract fun noteDao(): NoteDao
    abstract fun eventDao(): EventDao
    abstract fun ledgerDao(): LedgerDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "lifenest.db"
                )
                    // Early-stage app: on schema changes, reset local data rather than
                    // writing migrations. Fine for now; revisit before a real release.
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
