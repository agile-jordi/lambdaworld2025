package com.agilogy.lambdaworld2025.inventory.infrastructure

import com.agilogy.lambdaworld2025.inventory.domain.InventoryLine
import com.agilogy.lambdaworld2025.inventory.domain.InventoryRepository

class DatabaseInventoryRepository : InventoryRepository {

    override fun register(inventoryLine: InventoryLine) {
        TODO()
    }

    override fun getCurrentStock(sku: String): InventoryLine? = TODO()
}
