package com.agilogy.lambdaworld2025.inventory.infrastructure

import arrow.core.raise.Raise
import com.agilogy.lambdaworld2025.inventory.domain.InventoryLine
import com.agilogy.lambdaworld2025.inventory.domain.InventoryRepository
import com.agilogy.lambdaworld2025.inventory.domain.ProductNotFound

class DatabaseInventoryRepository : InventoryRepository {
    context(_: Raise<ProductNotFound>)
    override fun register(inventoryLine: InventoryLine): Unit = TODO()

    context(_: Raise<ProductNotFound>)
    override fun getCurrentStock(sku: String): InventoryLine? = TODO()
}
