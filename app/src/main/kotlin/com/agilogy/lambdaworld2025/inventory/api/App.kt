package com.agilogy.lambdaworld2025.inventory.api

import com.agilogy.lambdaworld2025.inventory.domain.InventoryService
import com.agilogy.lambdaworld2025.inventory.infrastructure.DatabaseInventoryRepository
import com.agilogy.lambdaworld2025.product.infrastructure.DatabaseProductsRepository
import kotlin.time.Clock

fun main() {
    val apiServer =
        InventoryApiServer(
            InventoryService(
                Clock.System,
                DatabaseProductsRepository(),
                DatabaseInventoryRepository(),
            )
        )
    apiServer.start()
}
