package com.example.buildingimagerecognition.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [BuildingEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun buildingDao(): BuildingDao
    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "building_db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            prepopulate(context)
                        }
                    })
                    .build()
                    .also { INSTANCE = it }
            }
        }

        private fun prepopulate(context: Context) {
            CoroutineScope(Dispatchers.IO).launch {
                getDatabase(context).buildingDao().insertBuilding(
                    BuildingEntity(
                        name = "Abhyudaya Building",
                        location = "Mumbai",
                        labels = "building,architecture",
                        imagePaths = "drawable:img_1,drawable:img_2"
                    )
                )
            }
        }
    }
}