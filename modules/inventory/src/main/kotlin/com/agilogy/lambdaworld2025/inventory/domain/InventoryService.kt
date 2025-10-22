@file:OptIn(ExperimentalRaiseAccumulateApi::class)

package com.agilogy.lambdaworld2025.inventory.domain

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.getOrElse
import arrow.core.raise.ExperimentalRaiseAccumulateApi
import arrow.core.raise.RaiseAccumulate
import arrow.core.raise.accumulate
import arrow.core.raise.either
import kotlin.time.Instant

class InventoryService(
    val inventoryRepository: InventoryRepository,
    val productsRepository: ProductsRepository,
) {

    fun reconcileStock(
        sku: String,
        stock: Int,
        reconciliationDate: Instant,
    ): Either<NonEmptyList<ReconcileStockError>, InventoryLine> = eitherAccumulate {
        val currentStock =
            recover(inventoryRepository.getCurrentStock(sku)) {
                productsRepository.registerProduct(sku).bind()
                null
            }

        if (currentStock != null && currentStock.reconciliationDate >= reconciliationDate) {
            accumulateError(IllegalReconciliationDateEarlierThanLast(currentStock))
        }
        val line = InventoryLine(sku, stock, reconciliationDate).bindOrAccumulate()
        inventoryRepository.register(line.value).bind()
        line.value
    }
}

fun <E, A> recover(e: Either<E, A>, f: (E) -> A): A = e.getOrElse(f)

fun <E, A> eitherAccumulate(f: RaiseAccumulate<E>.() -> A): Either<NonEmptyList<E>, A> = either {
    accumulate(f)
}

fun <E> RaiseAccumulate<E>.accumulateError(e: E): RaiseAccumulate.Value<Nothing> = accumulating {
    raise(e)
}
