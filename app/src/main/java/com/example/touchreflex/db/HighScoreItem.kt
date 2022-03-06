package com.example.touchreflex.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "HighScore")
data class HighScoreItem(
    @PrimaryKey @ColumnInfo(name = "game_mode") val gameMode: GameMode,
    @ColumnInfo(name = "score") val score: Int
)