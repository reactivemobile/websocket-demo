package com.reactivemobile.websocketdemo

import android.graphics.Point
import android.util.Log
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.http.cio.websocket.Frame
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.jetty.Jetty
import io.ktor.server.jetty.JettyApplicationEngine
import io.ktor.websocket.WebSockets
import io.ktor.websocket.webSocket
import java.util.concurrent.ConcurrentLinkedQueue

class Server(val connectionState: ConnectionState) {
    private val drawingQueue = ConcurrentLinkedQueue<Point>()
    private val commandQueue = ConcurrentLinkedQueue<String>()

    private lateinit var server: JettyApplicationEngine

    fun start(measuredWidth: Int, measuredHeight: Int) {
        server = embeddedServer(Jetty, port = 8080) {
            Log.d("Server", "Started")
            install(WebSockets)
            connectionState.accept(running = true, connected = false)

            routing {
                webSocket("/") {
                    drawingQueue.clear()

                    Log.d("Server", "Connected, sending message to start")
                    outgoing.send(Frame.Text("start,${measuredWidth},${measuredHeight}\n"))

                    connectionState.accept(running = true, connected = true)

                    while (true) {
                        if (!drawingQueue.isEmpty()) {
                            val point = drawingQueue.remove()
                            outgoing.send(Frame.Text("${point.x},${point.y}\n"))
                        }

                        if (!commandQueue.isEmpty()) {
                            val command = commandQueue.remove()
                            outgoing.send(Frame.Text(command))
                        }
                    }
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
        connectionState.accept(running = false, connected = false)
        drawingQueue.clear()
        Log.d("Server", "Stopped")
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

    fun interface ConnectionState{
        fun accept(running: Boolean, connected: Boolean)
    }
}