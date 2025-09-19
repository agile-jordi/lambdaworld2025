package com.agilogy.lambdaworld2025.inventory.api

import arrow.core.raise.either
import com.agilogy.lambdaworld2025.inventory.domain.IllegalStockAmountNegative
import com.agilogy.lambdaworld2025.inventory.domain.InventoryService
import com.agilogy.lambdaworld2025.product.domain.Sku
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
import kotlin.time.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class InventoryApiServer(
    val inventoryService: InventoryService
) {

    fun start() {
        embeddedServer(
                Netty,
                port = 8080,
                host = "0.0.0.0",
                module = { module() },
            )
            .start(wait = true)
    }

    fun Application.module() {
        install(ContentNegotiation) {
            json(Json { prettyPrint = true })
        }
        routing {
            post("/reconcileStock") {
                val body =
                    call.receive<ReconcileStockRequest>()
                val result = either {
                    val requestReconciliationDate =
                        body.reconciliationDate?.let {
                            Instant.fromEpochMilliseconds(
                                it
                            )
                        }
                    val sku: Sku = Sku(body.sku).bind()
                    inventoryService
                        .reconcileStock(
                            sku,
                            body.stock,
                            requestReconciliationDate,
                        )
                        .bind()
                }
                result.fold(
                    { error ->
                        when (error) {
                            is InventoryService.IllegalReconciliationDateEarlierThanLast ->
                                call.respond(
                                    HttpStatusCode
                                        .BadRequest,
                                    ErrorResponse(
                                        ResponseError(
                                            reconciliationDate =
                                                "cannot-be-earlier-than-last"
                                        )
                                    ),
                                )
                            is InventoryService.IllegalReconciliationDateInTheFuture ->
                                call.respond(
                                    HttpStatusCode
                                        .BadRequest,
                                    ErrorResponse(
                                        ResponseError(
                                            reconciliationDate =
                                                "cannot-be-in-the-future"
                                        )
                                    ),
                                )
                            is InventoryService.ProductNotFound ->
                                call.respond(
                                    HttpStatusCode
                                        .BadRequest,
                                    ErrorResponse(
                                        ResponseError(
                                            sku =
                                                "not-found"
                                        )
                                    ),
                                )

                            is IllegalStockAmountNegative ->
                                call.respond(
                                    HttpStatusCode
                                        .BadRequest,
                                    ErrorResponse(
                                        ResponseError(
                                            amount =
                                                "must-be-non-negative"
                                        )
                                    ),
                                )
                        }
                    },
                    { result ->
                        val response =
                            ReconcileStockResponse(
                                body.sku,
                                result.stock,
                                result.reconciliationDate
                                    .toEpochMilliseconds(),
                            )
                        call.respond(
                            HttpStatusCode.OK,
                            response,
                        )
                    },
                )
            }
        }
    }
}

@Serializable
data class ReconcileStockRequest(
    val sku: String,
    val stock: Int,
    val reconciliationDate: Long? = null,
)

@Serializable
data class ReconcileStockResponse(
    val sku: String,
    val stock: Int,
    val reconciliationDate: Long,
)

@Serializable
data class ErrorResponse(val errors: List<ResponseError>) {
    companion object {
        operator fun invoke(vararg errors: ResponseError) =
            ErrorResponse(errors.toList())
    }
}

@Serializable
data class ResponseError(
    val sku: String? = null,
    val reconciliationDate: String? = null,
    val amount: String? = null,
)
