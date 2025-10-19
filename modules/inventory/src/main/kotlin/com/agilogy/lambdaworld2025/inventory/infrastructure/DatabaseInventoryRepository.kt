package com.agilogy.lambdaworld2025.inventory.infrastructure

import arrow.core.Either
import com.agilogy.lambdaworld2025.inventory.domain.InventoryLine
import com.agilogy.lambdaworld2025.inventory.domain.InventoryRepository
import com.agilogy.lambdaworld2025.inventory.domain.ProductNotFound

class DatabaseInventoryRepository : InventoryRepository {
    override fun register(inventoryLine: InventoryLine): Either<ProductNotFound, Unit> = TODO()

    override fun getCurrentStock(sku: String): Either<ProductNotFound, InventoryLine?> = TODO()
}
