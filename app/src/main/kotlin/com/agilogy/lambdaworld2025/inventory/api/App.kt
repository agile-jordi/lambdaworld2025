package com.agilogy.lambdaworld2025.inventory.api

import com.agilogy.lambdaworld2025.inventory.domain.InventoryService
import com.agilogy.lambdaworld2025.inventory.infrastructure.DatabaseInventoryRepository
import com.agilogy.lambdaworld2025.product.infrastructure.DatabaseProductsRepository

fun main() {
    val apiServer =
        InventoryApiServer(
            InventoryService(DatabaseInventoryRepository(), DatabaseProductsRepository())
        )
    apiServer.start()
}
