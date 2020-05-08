package com.tampanada.radio

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util


class ForegroundService : Service() {

    private lateinit var player: SimpleExoPlayer
    private lateinit var musicSource: ProgressiveMediaSource

    companion object {
        private const val TAG_FOREGROUND_SERVICE = "FOREGROUND_SERVICE"
        const val ACTION_START_FOREGROUND_SERVICE = "ACTION_START_FOREGROUND_SERVICE"
        const val ACTION_STOP_FOREGROUND_SERVICE = "ACTION_STOP_FOREGROUND_SERVICE"
    }

    override fun onBind(intent: Intent?): IBinder {
        // TODO: Return the communication channel to the service.
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun onCreate() {
        super.onCreate()
        val url = "http://c6.auracast.net:8340/radio.mp3"
        player = SimpleExoPlayer.Builder(this).build()

        val dataSourceFactory = DefaultDataSourceFactory(this,
            Util.getUserAgent(this, "com.tampanada.radio"))

        musicSource = ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(Uri.parse(url))

        player.prepare(musicSource)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            when (intent.action) {
                ACTION_START_FOREGROUND_SERVICE -> {
                    startForegroundService()
                }
                ACTION_STOP_FOREGROUND_SERVICE -> {
                    stopForegroundService()
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    /* Used to build and start foreground service. */
    private fun startForegroundService() {
        Log.d(TAG_FOREGROUND_SERVICE, "Start foreground service.")

        // Create notification default intent.
        val intent = Intent()
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        // Create notification builder.
        val builder: NotificationCompat.Builder = NotificationCompat.Builder(this)

        // Make notification show big text.
        val bigTextStyle: NotificationCompat.BigTextStyle = NotificationCompat.BigTextStyle()
        bigTextStyle.setBigContentTitle("Tampanada Radio")
        bigTextStyle.bigText("L'emissora del Pallars")
        // Set big text style.
        builder.setStyle(bigTextStyle)
        builder.setWhen(System.currentTimeMillis())
        builder.setSmallIcon(R.mipmap.ic_launcher)
        val largeIconBitmap =
            BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher)
        builder.setLargeIcon(largeIconBitmap)
        // Make the notification max priority.
        builder.setPriority(Notification.PRIORITY_MAX)
        // Make head-up notification.
        builder.setFullScreenIntent(pendingIntent, true)

        // Add Play button intent in notification.
        val stopIntent = Intent(this, ForegroundService::class.java)
        stopIntent.action = ACTION_STOP_FOREGROUND_SERVICE
        val pendingPlayIntent = PendingIntent.getService(this, 0, stopIntent, 0)
        val playAction: NotificationCompat.Action =
            NotificationCompat.Action(android.R.drawable.ic_media_pause, "Atura", pendingPlayIntent)
        builder.addAction(playAction)

        // Build the notification.
        val notification: Notification = builder.build()

        player.playWhenReady = true
        if (player.playbackState == ExoPlayer.STATE_IDLE) {
            player.prepare(musicSource)
        }
        // Start foreground service.
        startForeground(1, notification)
    }

    private fun stopForegroundService() {
        Log.d(TAG_FOREGROUND_SERVICE, "Stop foreground service.")

        player.playWhenReady = false
        // Stop foreground service and remove the notification.
        stopForeground(true)

        // Stop the foreground service.
        stopSelf()
    }
}