package com.tampanada.radio

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import com.google.android.exoplayer2.SimpleExoPlayer
import kotlinx.android.synthetic.main.activity_main.*
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import android.net.Uri
import android.view.View
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.util.Util




class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main)


        val url = "http://c6.auracast.net:8340/radio.mp3"
        val player = SimpleExoPlayer.Builder(this).build()

        val dataSourceFactory = DefaultDataSourceFactory(this,
            Util.getUserAgent(this, "com.tampanada.radio"))

        val videoSource = ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(Uri.parse(url))

        player.addListener(object: Player.EventListener {
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                if(playbackState == ExoPlayer.STATE_READY){
                    play.visibility = View.VISIBLE
                }
            }
        })
        player.prepare(videoSource)

        play.setOnClickListener {
            player.playWhenReady = true
            play.visibility = View.INVISIBLE
            pause.visibility = View.VISIBLE
        }
        pause.setOnClickListener {
            player.playWhenReady = false
            play.visibility = View.VISIBLE
            pause.visibility = View.INVISIBLE
        }
    }
}
