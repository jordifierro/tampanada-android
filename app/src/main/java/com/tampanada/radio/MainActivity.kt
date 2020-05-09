package com.tampanada.radio

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import com.tampanada.radio.MainActivity.PlayerStateBroadCastReceiver.Companion.BROADCAST_ACTION
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private val receiver = PlayerStateBroadCastReceiver({ showPlayButton() }, { showPauseButton() })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main)

        play.setOnClickListener {
            showPlayButton()
            playService()
        }
        pause.setOnClickListener {
            showPauseButton()
            pauseService()
        }

        registerMyReceiver()
        startService()
    }

    override fun onDestroy() {
        super.onDestroy()

        unregisterReceiver(receiver)
        stopServiceIfNotPlaying()
    }

    private fun showPlayButton() {
        play.visibility = View.INVISIBLE
        pause.visibility = View.VISIBLE
    }

    private fun showPauseButton() {
        play.visibility = View.VISIBLE
        pause.visibility = View.INVISIBLE
    }

    private fun startService() {
        val intent = Intent(this, ForegroundService::class.java)
        intent.action = ForegroundService.ACTION_START_FOREGROUND_SERVICE
        startService(intent)
    }

    private fun playService() {
        val intent = Intent(this, ForegroundService::class.java)
        intent.action = ForegroundService.ACTION_PLAY_FOREGROUND_SERVICE
        startService(intent)
    }

    private fun pauseService() {
        val intent = Intent(this, ForegroundService::class.java)
        intent.action = ForegroundService.ACTION_PAUSE_FOREGROUND_SERVICE
        startService(intent)
    }

    private fun stopServiceIfNotPlaying() {
        val intent = Intent(this, ForegroundService::class.java)
        intent.action = ForegroundService.ACTION_STOP_FOREGROUND_SERVICE_IF_NOT_PLAYING
        startService(intent)
    }

    internal class PlayerStateBroadCastReceiver(
        val showPlayButton: () -> Unit,
        val showPauseButton: () -> Unit
    ) : BroadcastReceiver() {

        companion object {
            const val BROADCAST_ACTION = "com.tampanada.radio"
            const val STATE = "STATE"
            const val PLAYING = "PLAYING"
            const val PAUSED = "PAUSED"
        }

        override fun onReceive(context: Context?, intent: Intent?) {
            try {
                when (intent?.getStringExtra(STATE)) {
                    PLAYING -> showPlayButton()
                    PAUSED -> showPauseButton()
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    private fun registerMyReceiver() {
        try {
            val intentFilter = IntentFilter()
            intentFilter.addAction(BROADCAST_ACTION)
            registerReceiver(receiver, intentFilter)
        } catch (ex: java.lang.Exception) {
            ex.printStackTrace()
        }
    }
}
