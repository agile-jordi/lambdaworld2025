package com.agilogy.lambdaworld2025.inventory.domain

import arrow.core.Either
import arrow.core.getOrElse
import arrow.core.raise.either
import arrow.core.raise.recover
import kotlin.time.Instant

class InventoryService(
    val inventoryRepository: InventoryRepository,
    val productsRepository: ProductsRepository,
) {

    fun reconcileStock(
        sku: String,
        stock: Int,
        reconciliationDate: Instant,
    ): Either<ReconcileStockError, InventoryLine> = either {
        val currentStock =
            recover(inventoryRepository.getCurrentStock(sku)) {
                productsRepository.registerProduct(sku).bind()
                null
            }

        if (currentStock != null && currentStock.reconciliationDate >= reconciliationDate) {
            raise(IllegalReconciliationDateEarlierThanLast(currentStock))
        }
        val line = InventoryLine(sku, stock, reconciliationDate).bind()
        inventoryRepository.register(line).bind()
        line
    }
}

fun <E, A> recover(e: Either<E, A>, f: (E) -> A): A = e.getOrElse(f)
