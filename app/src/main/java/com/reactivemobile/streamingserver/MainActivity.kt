package com.reactivemobile.streamingserver

import android.annotation.SuppressLint
import android.net.wifi.WifiManager
import android.os.Bundle
import android.text.format.Formatter.formatIpAddress
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val server = Server()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupButtonListeners()

        setupDrawListener()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupDrawListener() {
        pane_view.setOnTouchListener { view, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_DOWN || motionEvent.action == MotionEvent.ACTION_MOVE) {
                server.update(motionEvent.x.toInt(), motionEvent.y.toInt())
            } else {
                server.pointerLifted()
            }
            true
        }
    }

    private fun setupButtonListeners() {
        start_button.setOnClickListener {
            server.start(pane_view.measuredWidth, pane_view.measuredHeight)
            status.text = getString(R.string.running, getIpAddress())
            start_button.isEnabled = false
            stop_button.isEnabled = true
            clear_button.isEnabled = true
        }

        stop_button.setOnClickListener {
            server.stop()
            status.text = getString(R.string.stopped)
            start_button.isEnabled = true
            stop_button.isEnabled = false
            clear_button.isEnabled = false
        }

        clear_button.setOnClickListener {
            server.clear()
        }
    }

    private fun getIpAddress(): String {
        val wifiManager = getSystemService(WIFI_SERVICE) as WifiManager
        return formatIpAddress(wifiManager.connectionInfo.ipAddress)
    }
}