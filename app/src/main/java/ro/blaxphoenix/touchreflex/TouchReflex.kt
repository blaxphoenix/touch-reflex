package ro.blaxphoenix.touchreflex

import android.app.Application
import ro.blaxphoenix.touchreflex.db.HighScoreDB
import ro.blaxphoenix.touchreflex.db.HighScoreRepository

class TouchReflex : Application() {
    private val database by lazy { HighScoreDB.getDatabase(this) }
    val repository by lazy { HighScoreRepository(database.highScoreDao()) }
}