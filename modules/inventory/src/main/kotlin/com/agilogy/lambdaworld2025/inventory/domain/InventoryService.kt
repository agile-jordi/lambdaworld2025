package com.agilogy.lambdaworld2025.inventory.domain

import com.agilogy.lambdaworld2025.product.domain.ProductsRepository
import kotlin.time.Clock
import kotlin.time.Instant

class InventoryService(
    private val clock: Clock,
    val productsRepository: ProductsRepository,
    val inventoryRepository: InventoryRepository,
) {

    fun reconcileStock(
        sku: String,
        stock: Int,
        reconciliationDate: Instant? = null,
    ): InventoryLine {
        val product =
            productsRepository.getProduct(sku)
                ?: throw ProductNotFound(sku)
        if (
            reconciliationDate != null &&
                reconciliationDate > clock.now()
        ) {
            throw IllegalReconciliationDateInTheFuture(
                reconciliationDate
            )
        }
        val currentStock =
            inventoryRepository.getCurrentStock(product.id)
        if (
            reconciliationDate != null &&
                currentStock != null &&
                currentStock.reconciliationDate >=
                    reconciliationDate
        ) {
            throw IllegalReconciliationDateEarlierThanLast(
                reconciliationDate,
                currentStock.reconciliationDate,
            )
        }
        val reconciliationDate =
            reconciliationDate ?: clock.now()
        val line =
            InventoryLine(
                product.id,
                stock,
                reconciliationDate,
            )
        inventoryRepository.register(line)
        return line
    }

    class IllegalReconciliationDateInTheFuture(
        val reconciliationDate: Instant
    ) :
        IllegalArgumentException(
            "Illegal reconciliation date $reconciliationDate. It cannot be in the future"
        )

    class IllegalReconciliationDateEarlierThanLast(
        val reconciliationDate: Instant,
        val lastReconciliationDate: Instant,
    ) :
        IllegalArgumentException(
            "Illegal reconciliation date $reconciliationDate. It cannot be earlier or equal than the last reconciliation date $lastReconciliationDate"
        )

    class ProductNotFound(val sku: String) :
        IllegalArgumentException(
            "Product with sku $sku does not exist"
        )
}
