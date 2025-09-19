package com.agilogy.lambdaworld2025.inventory.infrastructure

import com.agilogy.lambdaworld2025.inventory.domain.InventoryLine
import com.agilogy.lambdaworld2025.inventory.domain.InventoryRepository
import com.agilogy.lambdaworld2025.product.domain.ProductId

class DatabaseInventoryRepository : InventoryRepository {

    override fun register(inventoryLine: InventoryLine) {
        TODO()
    }

    override fun getCurrentStock(
        productId: ProductId
    ): InventoryLine? = TODO()
}
