package com.tampanada.radio

import android.media.AudioManager
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.media.AudioAttributes
import android.os.Build
import android.util.Log


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main)

        val url = "http://vprbbc.streamguys.net/vprbbc24.mp3"
        val player: MediaPlayer? = MediaPlayer()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            player!!.setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setLegacyStreamType(AudioManager.STREAM_MUSIC)
                    .build()
            )
        } else {
            player!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
        }

        player.setOnPreparedListener {
            it.start()
        }
        player.setOnErrorListener( MediaPlayer.OnErrorListener { mediaPlayer, i, i2 ->
            Log.e("tampa", "error " + i + i2)
            false
        })
        player.setDataSource(url)
        player.prepareAsync()
    }
}
