package com.tampanada.radio

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util


class ForegroundService : Service() {

    private lateinit var player: SimpleExoPlayer
    private lateinit var musicSource: ProgressiveMediaSource

    companion object {
        const val ACTION_START_FOREGROUND_SERVICE = "ACTION_START_FOREGROUND_SERVICE"
        const val ACTION_PLAY_FOREGROUND_SERVICE = "ACTION_PLAY_FOREGROUND_SERVICE"
        const val ACTION_PAUSE_FOREGROUND_SERVICE = "ACTION_PAUSE_FOREGROUND_SERVICE"
        const val ACTION_STOP_FOREGROUND_SERVICE = "ACTION_STOP_FOREGROUND_SERVICE"
        const val ACTION_STOP_FOREGROUND_SERVICE_IF_NOT_PLAYING =
            "ACTION_STOP_FOREGROUND_SERVICE_IF_NOT_PLAYING"
    }

    override fun onCreate() {
        super.onCreate()

        initializePlayer()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            when (intent.action) {
                ACTION_START_FOREGROUND_SERVICE -> startForegroundService()
                ACTION_PLAY_FOREGROUND_SERVICE -> playForegroundService()
                ACTION_STOP_FOREGROUND_SERVICE -> stopForegroundService()
                ACTION_PAUSE_FOREGROUND_SERVICE -> pauseForegroundService()
                ACTION_STOP_FOREGROUND_SERVICE_IF_NOT_PLAYING -> {
                    if (!player.playWhenReady) stopForegroundService()
                }
            }
            sendBroadcastWithState()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder {
        throw UnsupportedOperationException("Not yet implemented")
    }

    private fun startForegroundService() {
        if (player.playWhenReady)
            startForeground(1, createNotification(ACTION_PAUSE_FOREGROUND_SERVICE))
        else  startForeground(1, createNotification(ACTION_PLAY_FOREGROUND_SERVICE))
    }

    private fun playForegroundService() {
        startPlayer()
        startForeground(1, createNotification(ACTION_PAUSE_FOREGROUND_SERVICE))
    }

    private fun pauseForegroundService() {
        stopPlayer()
        startForeground(1, createNotification(ACTION_PLAY_FOREGROUND_SERVICE))
    }

    private fun stopForegroundService() {
        stopPlayer()
        stopForeground(true)
        stopSelf()
    }

    private fun createNotification(action: String): Notification {
        val intent = Intent()
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
        val builder: NotificationCompat.Builder = NotificationCompat.Builder(this)

        val bigTextStyle: NotificationCompat.BigTextStyle = NotificationCompat.BigTextStyle()
        bigTextStyle.setBigContentTitle("Tampanada Radio")
        bigTextStyle.bigText("L'emissora del Pallars")
        builder.setStyle(bigTextStyle)
        builder.setWhen(System.currentTimeMillis())
        builder.setSmallIcon(R.mipmap.ic_launcher)
        val largeIconBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher)
        builder.setLargeIcon(largeIconBitmap)
        builder.setPriority(Notification.PRIORITY_MAX)
        builder.setFullScreenIntent(pendingIntent, true)

        when (action) {
            ACTION_PLAY_FOREGROUND_SERVICE -> {
                val playIntent = Intent(this, ForegroundService::class.java)
                playIntent.action = ACTION_PLAY_FOREGROUND_SERVICE
                val pendingPlayIntent = PendingIntent.getService(this, 0, playIntent, 0)
                val playAction: NotificationCompat.Action =
                    NotificationCompat.Action(android.R.drawable.ic_media_play, "Reproduís", pendingPlayIntent)
                builder.addAction(playAction)
            }
            ACTION_PAUSE_FOREGROUND_SERVICE -> {
                val pauseIntent = Intent(this, ForegroundService::class.java)
                pauseIntent.action = ACTION_PAUSE_FOREGROUND_SERVICE
                val pendingPauseIntent = PendingIntent.getService(this, 0, pauseIntent, 0)
                val pauseAction: NotificationCompat.Action =
                    NotificationCompat.Action(android.R.drawable.ic_media_pause, "Tin-ti", pendingPauseIntent)
                builder.addAction(pauseAction)
            }
        }

        val stopIntent = Intent(this, ForegroundService::class.java)
        stopIntent.action = ACTION_STOP_FOREGROUND_SERVICE
        val pendingStopIntent = PendingIntent.getService(this, 0, stopIntent, 0)
        val stopAction: NotificationCompat.Action =
            NotificationCompat.Action(android.R.drawable.ic_delete, "Fui", pendingStopIntent)
        builder.addAction(stopAction)

        return builder.build()
    }

    private fun initializePlayer() {
        val url = "http://c6.auracast.net:8340/radio.mp3"
        player = SimpleExoPlayer.Builder(this).build()

        val dataSourceFactory = DefaultDataSourceFactory(this,
            Util.getUserAgent(this, "com.tampanada.radio"))

        musicSource = ProgressiveMediaSource.Factory(dataSourceFactory)
            .setLoadErrorHandlingPolicy(RetryPolicy())
            .createMediaSource(Uri.parse(url))

        val audioAttributes: AudioAttributes = AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.CONTENT_TYPE_SPEECH)
            .build()
        player.setAudioAttributes(audioAttributes, true)

        player.prepare(musicSource)
    }

    private fun startPlayer() {
        player.playWhenReady = true
    }

    private fun stopPlayer() {
        player.playWhenReady = false
    }

    private fun sendBroadcastWithState() {
        val state =
            if (player.playWhenReady) MainActivity.PlayerStateBroadCastReceiver.PLAYING
            else MainActivity.PlayerStateBroadCastReceiver.PAUSED
        val broadCastIntent = Intent()
        broadCastIntent.action = MainActivity.PlayerStateBroadCastReceiver.BROADCAST_ACTION
        broadCastIntent.putExtra(MainActivity.PlayerStateBroadCastReceiver.STATE, state);
        sendBroadcast(broadCastIntent)
    }
}