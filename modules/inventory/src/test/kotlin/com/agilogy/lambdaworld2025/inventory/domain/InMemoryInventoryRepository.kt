package com.agilogy.lambdaworld2025.inventory.domain

import arrow.core.raise.Raise
import arrow.core.raise.context.raise

class InMemoryInventoryRepository : InventoryRepository {
    val state = mutableMapOf<String, InventoryLine>()

    context(_: Raise<ProductNotFound>)
    override fun register(inventoryLine: InventoryLine) {
        if (inventoryLine.sku.isBlank()) raise(ProductNotFound(inventoryLine.sku))
        state[inventoryLine.sku] = inventoryLine
    }

    context(_: Raise<ProductNotFound>)
    override fun getCurrentStock(sku: String): InventoryLine? {
        if (sku.isBlank()) raise(ProductNotFound(sku))
        return state[sku]
    }
}
