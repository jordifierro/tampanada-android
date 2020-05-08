package com.tampanada.radio

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main)

        play.setOnClickListener {
            play.visibility = View.INVISIBLE
            pause.visibility = View.VISIBLE

            val intent = Intent(this, ForegroundService::class.java)
            intent.action = ForegroundService.ACTION_START_FOREGROUND_SERVICE
            startService(intent)
        }
        pause.setOnClickListener {
            play.visibility = View.VISIBLE
            pause.visibility = View.INVISIBLE

            val intent = Intent(this, ForegroundService::class.java)
            intent.action = ForegroundService.ACTION_STOP_FOREGROUND_SERVICE
            startService(intent)
        }
    }
}
