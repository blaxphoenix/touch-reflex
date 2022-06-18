package ro.blaxphoenix.touchreflex.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [HighScoreItem::class], version = 1, exportSchema = false)
abstract class HighScoreDB : RoomDatabase() {
    abstract fun highScoreDao(): HighScoreDao

    companion object {
        @Volatile
        private var INSTANCE: HighScoreDB? = null

        fun getDatabase(context: Context): HighScoreDB {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HighScoreDB::class.java,
                    "highscore_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}