package com.agilogy.lambdaworld2025.inventory.api

import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import kotlin.time.Clock
import kotlin.time.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class InventoryApiServer(val clock: Clock) {

    fun start() {
        embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = { module() }).start(wait = true)
    }

    fun Application.module() {
        install(ContentNegotiation) { json(Json { prettyPrint = true }) }
        routing {
            post("/reconcileStock") {
                val body = call.receive<ReconcileStockRequest>()
                val reconciliationDate =
                    body.reconciliationDate?.let { Instant.fromEpochMilliseconds(it) } ?: clock.now()
                val response = ReconcileStockResponse(body.sku, body.stock, reconciliationDate.toEpochMilliseconds())
                call.respond(HttpStatusCode.OK, response)
            }
        }
    }
}

@Serializable data class ReconcileStockRequest(val sku: String, val stock: Int, val reconciliationDate: Long?)

@Serializable data class ReconcileStockResponse(val sku: String, val stock: Int, val reconciliationDate: Long)
