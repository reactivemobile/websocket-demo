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
    private val drawingQueue = ConcurrentLinkedQueue<Point>()
    private val commandQueue = ConcurrentLinkedQueue<String>()

    private lateinit var server: JettyApplicationEngine

    fun start(measuredWidth: Int, measuredHeight: Int) {
        server = embeddedServer(Jetty, port = 8080) {

            install(WebSockets)

            routing {
                webSocket("/") {
                    Log.d("Server", "Connected")

                    drawingQueue.clear()

                    outgoing.send(Frame.Text("start,${measuredWidth},${measuredHeight}\n"))

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
        }.start(false)

        Log.d("Server", "Started")
    }

    fun stop() {
        server.stop(500, 1000)
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
}