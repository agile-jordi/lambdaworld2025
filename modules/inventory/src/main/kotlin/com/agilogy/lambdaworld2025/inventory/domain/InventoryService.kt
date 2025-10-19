package com.agilogy.lambdaworld2025.inventory.domain

import com.agilogy.lambdaworld2025.product.domain.ProductsRepository
import kotlin.time.Instant

class InventoryService(
    val inventoryRepository: InventoryRepository,
    val productsRepository: ProductsRepository,
) {

    fun reconcileStock(sku: String, stock: Int, reconciliationDate: Instant): InventoryLine {
        val currentStock =
            try {
                inventoryRepository.getCurrentStock(sku)
            } catch (_: ProductNotFound) {
                productsRepository.registerProduct(sku)
                null
            }

        if (currentStock != null && currentStock.reconciliationDate >= reconciliationDate) {
            throw IllegalReconciliationDateEarlierThanLast(currentStock)
        }

        val line = InventoryLine(sku, stock, reconciliationDate)
        inventoryRepository.register(line)
        return line
    }
}
