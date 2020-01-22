package com.tampanada.radio

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import com.google.android.exoplayer2.SimpleExoPlayer
import kotlinx.android.synthetic.main.activity_main.*
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import android.net.Uri
import com.google.android.exoplayer2.util.Util




class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main)

        val url = "http://vprbbc.streamguys.net/vprbbc24.mp3"
        val player = SimpleExoPlayer.Builder(this).build()
        playerView.player = player

        val dataSourceFactory = DefaultDataSourceFactory(this,
            Util.getUserAgent(this, "com.tampanada.radio"))

        val videoSource = ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(Uri.parse(url))

        player.prepare(videoSource)
        player.playWhenReady = true
    }
}
