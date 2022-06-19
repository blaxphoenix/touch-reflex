package ro.blaxphoenix.touchreflex.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface HighScoreDao {
    @Query("SELECT * FROM HighScore")
    fun getAll(): Flow<MutableList<HighScoreItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(highScoreItem: HighScoreItem)

    @Delete
    suspend fun delete(highScoreItem: HighScoreItem)
}