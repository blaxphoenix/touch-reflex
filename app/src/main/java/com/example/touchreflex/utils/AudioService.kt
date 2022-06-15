package com.example.touchreflex.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import com.example.touchreflex.R
import java.util.*

class AudioService(context: Context) {

    private val cache: Hashtable<MusicType, ArrayList<MediaPlayer>> = Hashtable()
    private var isPaused: Boolean = false
    private var currentMediaPlayer: MediaPlayer? = null
    var currentMusicType: MusicType? = null
        private set

    private val soundPool: SoundPool = SoundPool.Builder()
        .setMaxStreams(3)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_GAME)
                .build()
        )
        .build()

    private val confirmSoundId: Int = soundPool.load(context, R.raw.sound_confirm, 1)
    private val gameOverSoundId: Int = soundPool.load(context, R.raw.sound_game_over, 1)
    private val touchSoundId: Int = soundPool.load(context, R.raw.sound_touch_plop, 1)
    private val highScoreSoundId: Int = soundPool.load(context, R.raw.sound_high_score, 1)

    init {
        cache[MusicType.MENU] = arrayListOf(setUpMediaPlayer(context, R.raw.music_menu_1))
        cache[MusicType.DEFAULT_GAME] = arrayListOf(
            setUpMediaPlayer(context, R.raw.music_default_game_1),
            setUpMediaPlayer(context, R.raw.music_default_game_2),
            setUpMediaPlayer(context, R.raw.music_default_game_3)
        )
    }

    private fun setUpMediaPlayer(context: Context, musicResourceId: Int): MediaPlayer {
        val musicMP = MediaPlayer.create(context, musicResourceId)
        musicMP.isLooping = true
        musicMP.setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()
        )
        return musicMP
    }

    fun playConfirmSound(): AudioService {
        if (!isPaused) soundPool.play(confirmSoundId, 1f, 1f, 0, 0, 1f)
        return this
    }

    fun playTouchSound(): AudioService {
        if (!isPaused) soundPool.play(touchSoundId, 1f, 1f, 0, 0, 1f)
        return this
    }

    fun playHighScoreSound(): AudioService {
        if (!isPaused) soundPool.play(highScoreSoundId, 1f, 1f, 0, 0, 1f)
        return this
    }

    fun playGameOverSound(): AudioService {
        if (!isPaused) soundPool.play(gameOverSoundId, 1f, 1f, 0, 0, 1f)
        return this
    }

    fun start(): AudioService {
        isPaused = false
        currentMediaPlayer?.start()
        return this
    }

    fun pause(): AudioService {
        isPaused = true
        currentMediaPlayer?.pause()
        return this
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun stop(): AudioService {
        currentMediaPlayer?.pause()
        currentMediaPlayer?.seekTo(0)
        return this
    }

    fun switchMusic(musicType: MusicType): AudioService {
        if (currentMusicType != musicType) {
            stop()
            currentMusicType = musicType
            currentMediaPlayer = cache[musicType]?.random()
            if (!isPaused) {
                start()
            }
        }
        return this
    }

}