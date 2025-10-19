package com.agilogy.lambdaworld2025.inventory.domain

import arrow.core.raise.Raise

interface InventoryRepository {

    context(_: Raise<ProductNotFound>)
    fun register(inventoryLine: InventoryLine)

    context(_: Raise<ProductNotFound>)
    fun getCurrentStock(sku: String): InventoryLine?
}
