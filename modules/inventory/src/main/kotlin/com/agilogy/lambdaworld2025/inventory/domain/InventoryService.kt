package com.agilogy.lambdaworld2025.inventory.domain

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import arrow.core.raise.ensureNotNull
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
    ): Either<Error, InventoryLine> = either {
        val product = productsRepository.getProduct(sku)
        ensureNotNull(product) { ProductNotFound(sku) }
        ensure(
            reconciliationDate == null ||
                reconciliationDate <= clock.now()
        ) {
            IllegalReconciliationDateInTheFuture(
                reconciliationDate!!
            )
        }
        val currentStock =
            inventoryRepository.getCurrentStock(product.id)
        ensure(
            reconciliationDate == null ||
                currentStock == null ||
                currentStock.reconciliationDate <
                    reconciliationDate
        ) {
            IllegalReconciliationDateEarlierThanLast(
                reconciliationDate!!,
                currentStock!!.reconciliationDate,
            )
        }
        val reconciliationDate =
            reconciliationDate ?: clock.now()
        val inventoryLine =
            InventoryLine(
                    product.id,
                    stock,
                    reconciliationDate,
                )
                .bind()
        inventoryRepository.register(inventoryLine)
        inventoryLine
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
