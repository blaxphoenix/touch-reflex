package ro.blaxphoenix.touchreflex.db

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

class HighScoreRepository(private val highScoreDao: HighScoreDao) {

    val allHighScoreItems: Flow<MutableList<HighScoreItem>> = highScoreDao.getAll()

    @WorkerThread
    suspend fun insert(highScoreItem: HighScoreItem) = highScoreDao.insert(highScoreItem)

    @WorkerThread
    suspend fun delete(highScoreItem: HighScoreItem) = highScoreDao.delete(highScoreItem)
}