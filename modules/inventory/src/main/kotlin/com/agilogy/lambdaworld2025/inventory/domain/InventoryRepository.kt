package com.agilogy.lambdaworld2025.inventory.domain

import com.agilogy.lambdaworld2025.product.domain.ProductId

interface InventoryRepository {
    fun register(inventoryLine: InventoryLine)

    fun getCurrentStock(
        productId: ProductId
    ): InventoryLine?
}
