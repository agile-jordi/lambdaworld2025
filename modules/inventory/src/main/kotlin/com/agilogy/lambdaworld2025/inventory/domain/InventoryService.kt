package com.agilogy.lambdaworld2025.inventory.domain

import arrow.core.Either
import arrow.core.flatMap
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
    ): Either<Error, InventoryLine> =
        productsRepository
            .getProduct(sku)
            .notNullOr { ProductNotFound(sku) }
            .flatMap { product ->
                ensure(
                        reconciliationDate == null ||
                            reconciliationDate <=
                                clock.now()
                    ) {
                        IllegalReconciliationDateInTheFuture(
                            reconciliationDate!!
                        )
                    }
                    .flatMap {
                        val currentStock =
                            inventoryRepository
                                .getCurrentStock(product.id)
                        ensure(
                                reconciliationDate ==
                                    null ||
                                    currentStock == null ||
                                    currentStock
                                        .reconciliationDate <
                                        reconciliationDate
                            ) {
                                IllegalReconciliationDateEarlierThanLast(
                                    reconciliationDate!!,
                                    currentStock!!
                                        .reconciliationDate,
                                )
                            }
                            .map {
                                val reconciliationDate =
                                    reconciliationDate
                                        ?: clock.now()
                                val line =
                                    InventoryLine(
                                        product.id,
                                        stock,
                                        reconciliationDate,
                                    )
                                inventoryRepository
                                    .register(line)
                                line
                            }
                    }
            }

    sealed class Error(val message: String)

    class IllegalReconciliationDateInTheFuture(
        val reconciliationDate: Instant
    ) :
        Error(
            "Illegal reconciliation date $reconciliationDate. It cannot be in the future"
        )

    class IllegalReconciliationDateEarlierThanLast(
        val reconciliationDate: Instant,
        val lastReconciliationDate: Instant,
    ) :
        Error(
            "Illegal reconciliation date $reconciliationDate. It cannot be earlier or equal than the last reconciliation date $lastReconciliationDate"
        )

    class ProductNotFound(val sku: String) :
        Error("Product with sku $sku does not exist")
}

fun <E> ensure(
    condition: Boolean,
    left: () -> E,
): Either<E, Unit> =
    if (condition) Either.Right(Unit)
    else Either.Left(left())

fun <E, B : Any> B?.notNullOr(left: () -> E): Either<E, B> =
    this?.let { Either.Right(it) } ?: Either.Left(left())
