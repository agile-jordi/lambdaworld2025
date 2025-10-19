package com.agilogy.lambdaworld2025.inventory.infrastructure

import arrow.core.raise.Raise
import com.agilogy.lambdaworld2025.inventory.domain.InventoryLine
import com.agilogy.lambdaworld2025.inventory.domain.InventoryRepository
import com.agilogy.lambdaworld2025.inventory.domain.ProductNotFound

class DatabaseInventoryRepository : InventoryRepository {
    override fun Raise<ProductNotFound>.register(inventoryLine: InventoryLine): Unit = TODO()

    override fun Raise<ProductNotFound>.getCurrentStock(sku: String): InventoryLine? = TODO()
}
