package com.agilogy.lambdaworld2025.inventory.domain

import arrow.core.Either
import arrow.core.raise.either
import kotlin.time.Instant

@ConsistentCopyVisibility
data class InventoryLine
private constructor(val sku: String, val stock: Int, val reconciliationDate: Instant) {

    companion object {
        operator fun invoke(
            sku: String,
            stock: Int,
            reconciliationDate: Instant,
        ): Either<IllegalStockAmountNegative, InventoryLine> = either {
            if (stock < 0) raise(IllegalStockAmountNegative(stock))
            InventoryLine(sku, stock, reconciliationDate)
        }
    }
}
