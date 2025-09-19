package com.agilogy.lambdaworld2025.inventory.api

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.*

class InventoryApiServerTest : FunSpec() {
    init {
        val inventoryApiServer = InventoryApiServer()
        test("reconcileStock endpoint should return OK") {
            testApplication {
                application { with(inventoryApiServer) { module() } }
                val response = client.post("/reconcileStock") { this.setBody("Hi, you server!") }
                response.status shouldBe HttpStatusCode.OK
                response.body<String>() shouldBe "Hello Lambda World 2025! You sent: Hi, you server!"
            }
        }
    }
}
