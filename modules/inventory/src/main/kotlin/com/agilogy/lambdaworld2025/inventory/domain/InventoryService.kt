package com.agilogy.lambdaworld2025.inventory.domain

import com.agilogy.lambdaworld2025.product.domain.ProductsRepository
import kotlin.time.Instant

class InventoryService(
    val inventoryRepository: InventoryRepository,
    val productsRepository: ProductsRepository,
) {

    fun reconcileStock(sku: String, stock: Int, reconciliationDate: Instant): InventoryLine {
        TODO()
    }
}
