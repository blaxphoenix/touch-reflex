package ro.blaxphoenix.touchreflex.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import androidx.annotation.IntegerRes
import ro.blaxphoenix.touchreflex.R
import java.util.*

class AudioService(val context: Context) {

    private val cache: Hashtable<MusicType, ArrayList<Int>> = Hashtable()
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
        cache[MusicType.MENU] = arrayListOf(
            R.raw.music_menu_1
        )
        cache[MusicType.EASY] = arrayListOf(
            R.raw.music_easy_1,
            R.raw.music_easy_2,
            R.raw.music_easy_3,
            R.raw.music_easy_4
        )
        cache[MusicType.HARD] = arrayListOf(
            R.raw.music_hard_1,
            R.raw.music_hard_2,
            R.raw.music_hard_3,
            R.raw.music_hard_4
        )
    }

    private fun setUpMediaPlayer(context: Context, @IntegerRes musicResourceId: Int): MediaPlayer {
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
        currentMediaPlayer?.stop()
        currentMediaPlayer?.release()
        currentMediaPlayer = null
        return this
    }

    fun switchMusic(musicType: MusicType): AudioService {
        if (currentMusicType != musicType) {
            stop()
            currentMusicType = musicType
            cache[musicType]?.random()?.let {
                currentMediaPlayer = setUpMediaPlayer(context, it)
                if (!isPaused) {
                    start()
                }
            }
        }
        return this
    }

}