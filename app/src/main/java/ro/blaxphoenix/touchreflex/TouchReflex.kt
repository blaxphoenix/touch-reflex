package ro.blaxphoenix.touchreflex

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import ro.blaxphoenix.touchreflex.db.HighScoreDB
import ro.blaxphoenix.touchreflex.db.HighScoreRepository

class TouchReflex : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob())
    private val database by lazy { HighScoreDB.getDatabase(this) }
    val repository by lazy { HighScoreRepository(database.highScoreDao()) }
}