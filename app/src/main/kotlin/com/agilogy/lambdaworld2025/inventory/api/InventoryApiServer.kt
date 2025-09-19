package com.agilogy.lambdaworld2025.inventory.api

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.request.receiveText
import io.ktor.server.response.respondText
import io.ktor.server.routing.post
import io.ktor.server.routing.routing

class InventoryApiServer() {

    fun start() {
        embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = { module() }).start(wait = true)
    }

    fun Application.module() {
        routing {
            post("/reconcileStock") {
                val body = call.receiveText()
                val response = "Hello Lambda World 2025! You sent: $body"
                call.respondText(response, status = HttpStatusCode.OK)
            }
        }
    }
}
