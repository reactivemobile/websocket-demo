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

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        start_button.setOnClickListener {
            server.start(pane_view.measuredWidth, pane_view.measuredHeight)
            status.text = getString(R.string.running, getIpAddress())
        }

        stop_button.setOnClickListener {
            server.stop()
            status.text = getString(R.string.running, getIpAddress())
        }

        clear_button.setOnClickListener {
            server.clear()
        }

        pane_view.setOnTouchListener { view, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_DOWN || motionEvent.action == MotionEvent.ACTION_MOVE) {
                server.update(motionEvent.x.toInt(), motionEvent.y.toInt())
            } else {
                server.pointerLifted()
            }
            true
        }
    }

    private fun getIpAddress(): String {
        val wifiManager = getSystemService(WIFI_SERVICE) as WifiManager
        return formatIpAddress(wifiManager.connectionInfo.ipAddress)
    }
}