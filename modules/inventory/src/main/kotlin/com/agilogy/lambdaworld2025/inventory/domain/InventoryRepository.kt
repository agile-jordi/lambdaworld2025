package com.agilogy.lambdaworld2025.inventory.domain

import arrow.core.Either

interface InventoryRepository {

    fun register(inventoryLine: InventoryLine): Either<ProductNotFound, Unit>

    fun getCurrentStock(sku: String): Either<ProductNotFound, InventoryLine?>
}
