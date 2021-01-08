package com.reactivemobile.websocketdemo

import android.annotation.SuppressLint
import android.net.wifi.WifiManager
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.math.BigInteger
import java.net.InetAddress

class MainActivity : AppCompatActivity() {

    private val server = Server { running, connected -> updateUiState(running, connected) }

    private fun updateUiState(running: Boolean, connected: Boolean) {
        Log.d("MainActivity", "Running = $running connected = $connected")

        if (running) {
            status.text = getString(R.string.running, getIpAddress(), getString(if (connected) R.string.connected else R.string.disconnected))
            drawing_view.isEnabled = connected
            clear_button.isEnabled = connected
            start_button.isEnabled = false
            stop_button.isEnabled = true
        } else {
            status.text = getString(R.string.stopped)
            drawing_view.isEnabled = false
            clear_button.isEnabled = false
            start_button.isEnabled = true
            stop_button.isEnabled = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupButtonListeners()

        setupDrawListener()
    }

    override fun onStart() {
        updateUiState(running = false, connected = false)
        super.onStart()
    }

    private fun setupButtonListeners() {

        start_button.setOnClickListener {
            server.start(drawing_view.measuredWidth, drawing_view.measuredHeight)
        }

        stop_button.setOnClickListener {
            server.stop()
            drawing_view.clear()
        }

        clear_button.setOnClickListener {
            server.clear()
            drawing_view.clear()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupDrawListener() {
        drawing_view.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> { drawing_view.move(motionEvent.x, motionEvent.y) }
                MotionEvent.ACTION_MOVE -> {
                    drawing_view.draw(motionEvent.x, motionEvent.y)
                    server.update(motionEvent.x.toInt(), motionEvent.y.toInt())
                }
                else -> {
                    server.pointerLifted()
                }
            }
            true
        }
    }

    private fun getIpAddress(): String {
        val wifiManager = getSystemService(WIFI_SERVICE) as WifiManager
        val ipAddress = BigInteger.valueOf(wifiManager.connectionInfo.ipAddress.toLong()).toByteArray().reversedArray()

        return InetAddress.getByAddress(ipAddress).hostAddress
    }
}