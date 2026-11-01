package com.example.util

import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.os.Handler
import android.os.Looper
import java.io.File

class AudioRecorderHelper(private val context: Context) {
  private var recorder: MediaRecorder? = null
  private var currentFilePath: String? = null
  private var startTimeMs: Long = 0

  fun startRecording(): Boolean {
    return try {
      val file = File(context.filesDir, "voice_${System.currentTimeMillis()}.m4a")
      currentFilePath = file.absolutePath

      val newRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        MediaRecorder(context)
      } else {
        @Suppress("DEPRECATION")
        MediaRecorder()
      }

      newRecorder.apply {
        setAudioSource(MediaRecorder.AudioSource.MIC)
        setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        setAudioEncodingBitRate(128000)
        setAudioSamplingRate(44100)
        setOutputFile(file.absolutePath)
        prepare()
        start()
      }

      recorder = newRecorder
      startTimeMs = System.currentTimeMillis()
      true
    } catch (e: Exception) {
      e.printStackTrace()
      recorder?.release()
      recorder = null
      currentFilePath = null
      false
    }
  }

  fun stopRecording(): Pair<String, Int>? {
    val current = recorder ?: return null
    val path = currentFilePath ?: return null
    val durationSec = ((System.currentTimeMillis() - startTimeMs) / 1000).toInt().coerceAtLeast(1)

    return try {
      current.stop()
      current.release()
      recorder = null
      currentFilePath = null
      Pair(path, durationSec)
    } catch (e: Exception) {
      current.release()
      recorder = null
      currentFilePath = null
      null
    }
  }

  fun cancelRecording() {
    try {
      recorder?.stop()
      recorder?.release()
    } catch (_: Exception) {}
    recorder = null
    currentFilePath?.let { File(it).delete() }
    currentFilePath = null
  }
}

class AudioPlayerHelper {
  private var mediaPlayer: MediaPlayer? = null
  private var currentlyPlayingPath: String? = null
  private val handler = Handler(Looper.getMainLooper())
  private var progressRunnable: Runnable? = null

  fun playAudio(
    filePath: String,
    onProgress: (currentSec: Int, totalSec: Int) -> Unit,
    onFinished: () -> Unit
  ) {
    if (currentlyPlayingPath == filePath && mediaPlayer?.isPlaying == true) {
      stopAudio()
      onFinished()
      return
    }

    stopAudio()

    try {
      val player = MediaPlayer()
      player.setDataSource(filePath)
      player.prepare()
      val totalDurationMs = player.duration
      val totalSec = (totalDurationMs / 1000).coerceAtLeast(1)

      player.setOnCompletionListener {
        stopAudio()
        onFinished()
      }

      player.start()
      mediaPlayer = player
      currentlyPlayingPath = filePath

      progressRunnable = object : Runnable {
        override fun run() {
          mediaPlayer?.let { p ->
            if (p.isPlaying) {
              val currentSec = (p.currentPosition / 1000).coerceAtMost(totalSec)
              onProgress(currentSec, totalSec)
              handler.postDelayed(this, 300)
            }
          }
        }
      }
      handler.post(progressRunnable!!)
    } catch (e: Exception) {
      e.printStackTrace()
      stopAudio()
      onFinished()
    }
  }

  fun stopAudio() {
    progressRunnable?.let { handler.removeCallbacks(it) }
    progressRunnable = null
    try {
      mediaPlayer?.apply {
        if (isPlaying) stop()
        release()
      }
    } catch (_: Exception) {}
    mediaPlayer = null
    currentlyPlayingPath = null
  }

  fun isPlaying(filePath: String): Boolean {
    return currentlyPlayingPath == filePath && mediaPlayer?.isPlaying == true
  }
}
