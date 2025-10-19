package com.agilogy.lambdaworld2025.inventory.domain

import arrow.core.raise.Raise
import arrow.core.raise.recover
import kotlin.time.Instant

class InventoryService(
    val inventoryRepository: InventoryRepository,
    val productsRepository: ProductsRepository,
) {

    fun Raise<ReconcileStockError>.reconcileStock(
        sku: String,
        stock: Int,
        reconciliationDate: Instant,
    ): InventoryLine {
        val currentStock =
            recover({ with(inventoryRepository) { getCurrentStock(sku) } }) {
                with(productsRepository) { registerProduct(sku) }
                null
            }

        if (currentStock != null && currentStock.reconciliationDate >= reconciliationDate) {
            raise(IllegalReconciliationDateEarlierThanLast(currentStock))
        }
        val line = with(InventoryLine) { invoke(sku, stock, reconciliationDate) }
        with(inventoryRepository) { register(line) }
        return line
    }
}
