package com.agilogy.lambdaworld2025.inventory.domain

import arrow.core.raise.Raise

interface InventoryRepository {

    fun Raise<ProductNotFound>.register(inventoryLine: InventoryLine): Unit

    fun Raise<ProductNotFound>.getCurrentStock(sku: String): InventoryLine?
}
