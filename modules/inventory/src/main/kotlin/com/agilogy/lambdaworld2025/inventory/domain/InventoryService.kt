package com.agilogy.lambdaworld2025.inventory.domain

import arrow.core.raise.Raise
import arrow.core.raise.recover
import kotlin.time.Instant

class InventoryService(
    val inventoryRepository: InventoryRepository,
    val productsRepository: ProductsRepository,
) {

    context(_: Raise<ReconcileStockError>)
    fun reconcileStock(sku: String, stock: Int, reconciliationDate: Instant): InventoryLine {
        val currentStock =
            recover({ inventoryRepository.getCurrentStock(sku) }) {
                productsRepository.registerProduct(sku)
                null
            }

        if (currentStock != null && currentStock.reconciliationDate >= reconciliationDate) {
            raise(IllegalReconciliationDateEarlierThanLast(currentStock))
        }
        val line = InventoryLine(sku, stock, reconciliationDate)
        inventoryRepository.register(line)
        return line
    }
}

context(r: Raise<E>)
fun <E> raise(error: E): Nothing = r.raise(error)
