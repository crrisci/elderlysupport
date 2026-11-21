package com.example.elderlysupport.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        Farmaco::class,
        Promemoria::class,
        ContattoEmergenza::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun farmacoDao(): FarmacoDao
    abstract fun promemoriaDao(): PromemoriaDao
    abstract fun contattoDao(): ContattoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "elderly.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}