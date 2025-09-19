package com.agilogy.lambdaworld2025.inventory.api

import kotlin.time.Clock

fun main() {
    val apiServer = InventoryApiServer(Clock.System)
    apiServer.start()
}
