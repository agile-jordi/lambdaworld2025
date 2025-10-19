package com.agilogy.lambdaworld2025.inventory.domain

import arrow.core.Either
import kotlin.time.Instant

@ConsistentCopyVisibility
data class InventoryLine
private constructor(val sku: String, val stock: Int, val reconciliationDate: Instant) {

    companion object {
        operator fun invoke(
            sku: String,
            stock: Int,
            reconciliationDate: Instant,
        ): Either<IllegalStockAmountNegative, InventoryLine> =
            if (stock < 0) Either.Left(IllegalStockAmountNegative(stock))
            else Either.Right(InventoryLine(sku, stock, reconciliationDate))
    }
}
