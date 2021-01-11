package com.reactivemobile.websocketdemo.data

import android.graphics.Point
import android.util.Log
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.readText
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.jetty.Jetty
import io.ktor.server.jetty.JettyApplicationEngine
import io.ktor.websocket.WebSockets
import io.ktor.websocket.webSocket
import kotlinx.coroutines.isActive
import java.util.concurrent.ConcurrentLinkedQueue

class Server {
    private val drawingQueue = ConcurrentLinkedQueue<Point>()

    private val commandQueue = ConcurrentLinkedQueue<String>()

    private lateinit var server: JettyApplicationEngine

    private val tag = "Server"

    suspend fun start(measuredWidth: Int, measuredHeight: Int, connectionState: ConnectionState) {

        server = embeddedServer(Jetty, port = 8080) {
            Log.d(tag, "Started")
            install(WebSockets)
            connectionState.accept(running = true, connected = false)

            routing {
                webSocket("/") {
                    // Start of the session
                    drawingQueue.clear()

                    // Wait for the client to tell us it's ready
                    var ready = false
                    while (!ready) {
                        val frame = incoming.receive() // Listen to incoming frames
                        if (frame is Frame.Text) {
                            if (frame.readText() == "Ready") {
                                Log.d(tag, "Connected, sending message to start")
                                outgoing.send(Frame.Text("start,${measuredWidth},${measuredHeight}"))

                                connectionState.accept(running = true, connected = true)
                            }
                            ready = true
                        }
                    }

                    // While the coroutine is active, listen to our drawing and command queues and send events as they appear
                    while (this.isActive) {
                        if (!drawingQueue.isEmpty()) {
                            val point = drawingQueue.remove()
                            outgoing.send(Frame.Text("${point.x},${point.y}"))
                        }

                        if (!commandQueue.isEmpty()) {
                            val command = commandQueue.remove()
                            outgoing.send(Frame.Text(command))
                        }
                    }

                    // The socket is no longer active so stop the server
                    connectionState.accept(running = false, connected = false)
                    stop()
                }

                // curl {url}/hello to test if the server is working
                get("/hello") {
                    call.respond("Hello World Android!!\n\n")
                }
            }
        }

        server.start()
    }

    fun stop() {
        server.stop(500, 1000)
        drawingQueue.clear()
        Log.d(tag, "Stopped")
    }

    fun update(x: Int, y: Int) {
        drawingQueue.add(Point(x, y))
    }

    fun clear() {
        drawingQueue.clear()
        commandQueue.add("clear")
    }

    fun pointerLifted() {
        commandQueue.add("pointer_lifted")
    }

    fun interface ConnectionState {
        fun accept(running: Boolean, connected: Boolean)
    }
}