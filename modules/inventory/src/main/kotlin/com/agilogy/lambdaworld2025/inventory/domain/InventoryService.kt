package com.agilogy.lambdaworld2025.inventory.domain

import arrow.core.Either
import arrow.core.flatMap
import kotlin.time.Instant

class InventoryService(
    val inventoryRepository: InventoryRepository,
    val productsRepository: ProductsRepository,
) {

    fun reconcileStock(
        sku: String,
        stock: Int,
        reconciliationDate: Instant,
    ): Either<ReconcileStockError, InventoryLine> =
        inventoryRepository
            .getCurrentStock(sku)
            .orElse { productsRepository.registerProduct(sku).map { null } }
            .flatMap { currentStock ->
                if (currentStock != null && currentStock.reconciliationDate >= reconciliationDate) {
                    Either.Left(IllegalReconciliationDateEarlierThanLast(currentStock))
                } else {
                    InventoryLine(sku, stock, reconciliationDate).flatMap { line ->
                        inventoryRepository.register(line).map { line }
                    }
                }
            }
}

fun <E : E2, E2, A> Either<E, A>.orElse(f: (E) -> Either<E2, A>): Either<E2, A> =
    fold({ f(it) }, { this })
