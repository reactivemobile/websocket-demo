package com.reactivemobile.websocketdemo

import android.annotation.SuppressLint
import android.net.wifi.WifiManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_drawing.*
import java.math.BigInteger
import java.net.InetAddress

@AndroidEntryPoint
class DrawingFragment : Fragment() {

    private val viewModel: DrawingViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModelListener()

        setupButtonListeners()

        setupDrawListener()
    }

    private fun setupViewModelListener() {
        viewModel.uiState.observe(viewLifecycleOwner, {
            updateUiState(it.running, it.connected)
        })
    }

    private fun updateUiState(running: Boolean, connected: Boolean) {
        if (running) {
            status.text = getString(R.string.running, getIpAddress(), getString(if (connected) R.string.connected else R.string.disconnected))
            drawing_view.isEnabled = connected
            clear_button.isEnabled = connected
            start_button.isEnabled = false
            stop_button.isEnabled = true
        } else {
            drawing_view.clear()
            status.text = getString(R.string.stopped)
            drawing_view.isEnabled = false
            clear_button.isEnabled = false
            start_button.isEnabled = true
            stop_button.isEnabled = false
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_drawing, container, false)
    }

    override fun onStart() {
        updateUiState(running = false, connected = false)
        super.onStart()
    }

    private fun setupButtonListeners() {

        start_button.setOnClickListener {
            viewModel.start(drawing_view.measuredWidth, drawing_view.measuredHeight)
        }

        stop_button.setOnClickListener {
            viewModel.stop()
            drawing_view.clear()
        }

        clear_button.setOnClickListener {
            viewModel.clear()
            drawing_view.clear()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupDrawListener() =
        drawing_view.setOnTouchListener { _, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    drawing_view.move(motionEvent.x, motionEvent.y)
                }
                MotionEvent.ACTION_MOVE -> {
                    drawing_view.draw(motionEvent.x, motionEvent.y)
                    viewModel.pointerMoved(motionEvent.x.toInt(), motionEvent.y.toInt())
                }
                else -> {
                    viewModel.pointerLifted()
                }
            }
            true
        }

    private fun getIpAddress(): String {
        val wifiManager = requireActivity().getSystemService(AppCompatActivity.WIFI_SERVICE) as WifiManager
        val ipAddress = BigInteger.valueOf(wifiManager.connectionInfo.ipAddress.toLong()).toByteArray().reversedArray()

        return InetAddress.getByAddress(ipAddress).hostAddress
    }

    companion object {

        const val TAG = "DrawingFragment"

        @JvmStatic
        fun newInstance() = DrawingFragment()
    }
}