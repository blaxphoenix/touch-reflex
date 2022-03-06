package com.example.touchreflex

import android.app.Application
import com.example.touchreflex.db.HighScoreDB
import com.example.touchreflex.db.HighScoreRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class TouchReflex : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob())
    private val database by lazy { HighScoreDB.getDatabase(this, applicationScope) }
    val repository by lazy { HighScoreRepository(database.highScoreDao()) }
}