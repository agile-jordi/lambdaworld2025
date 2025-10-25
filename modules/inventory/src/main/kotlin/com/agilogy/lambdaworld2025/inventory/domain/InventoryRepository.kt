package com.agilogy.lambdaworld2025.inventory.domain

import arrow.core.Either
import arrow.core.raise.Raise
import arrow.core.raise.either

interface InventoryRepository {

    context(_: Raise<ProductNotFound>)
    fun register(inventoryLine: InventoryLine)

    fun registerEither(inventoryLine: InventoryLine): Either<ProductNotFound, Unit> = either {
        register(inventoryLine)
    }

    context(_: Raise<ProductNotFound>)
    fun getCurrentStock(sku: String): InventoryLine?

    fun getCurrentStockEither(sku: String) = either { getCurrentStock(sku) }
}
