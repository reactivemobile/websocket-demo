package com.reactivemobile.websocketdemo

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class DrawingViewModel @ViewModelInject constructor(private val server: Server) : ViewModel() {

    val uiState: MutableLiveData<UiState> = MutableLiveData()

    fun start(width: Int, height: Int) =
        viewModelScope.launch {
            server.start(width, height) { running, connected -> uiState.postValue(UiState(running = running, connected = connected)) }
        }

    fun pointerLifted() = server.pointerLifted()

    fun pointerMoved(x: Int, y: Int) = server.update(x, y)

    fun stop() {
        server.stop()
        uiState.postValue(UiState(running = false, connected = false))
    }

    fun clear() = server.clear()

    data class UiState(val running: Boolean, val connected: Boolean)
}