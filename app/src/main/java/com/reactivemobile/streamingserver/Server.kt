package com.reactivemobile.streamingserver

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

class Server {

    private val queue = ConcurrentLinkedQueue<Point>()

    private lateinit var server: JettyApplicationEngine

    fun start(measuredWidth: Int, measuredHeight: Int) {
        server = embeddedServer(Jetty, port = 8080) {

            install(WebSockets)

            routing {
                webSocket("/") {
                    Log.d("Server", "connected")

                    outgoing.send(Frame.Text("${measuredWidth},${measuredWidth}\n"))

                    while (true) {
                        if (!queue.isEmpty()) {
                            val point = queue.remove()
                            outgoing.send(Frame.Text("${point.x},${point.y}\n"))
                        }
                    }
                }
                // Open {url}/hello to test if the server is working
                get("/hello") {
                    call.respond("Hello World Android!!\n\n")
                }
            }
        }.start(false)

        Log.d("Server", "Started")
    }

    fun stop() {
        server.stop(500, 1000)
        queue.clear()
        Log.d("Server", "Stopped")
    }

    fun update(x: Int, y: Int) {
        queue.add(Point(x, y))
    }

    fun clear() {
        queue.clear()
        queue.add(Point(-1, -1))
    }

    fun pointerLifted() {
        queue.add(Point(-2, -2))
    }
}