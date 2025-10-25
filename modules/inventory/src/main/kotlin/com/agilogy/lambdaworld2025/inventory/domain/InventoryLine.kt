package com.agilogy.lambdaworld2025.inventory.domain

import arrow.core.Either
import arrow.core.raise.context.Raise
import arrow.core.raise.context.either
import arrow.core.raise.context.raise
import kotlin.time.Instant

@ConsistentCopyVisibility
data class InventoryLine
private constructor(val sku: String, val stock: Int, val reconciliationDate: Instant) {

    companion object {

        context(_: Raise<IllegalStockAmountNegative>)
        operator fun invoke(sku: String, stock: Int, reconciliationDate: Instant): InventoryLine {
            if (stock < 0) raise(IllegalStockAmountNegative(stock))
            return InventoryLine(sku, stock, reconciliationDate)
        }
    }
}

object InventoryLineEither {

    operator fun invoke(
        sku: String,
        stock: Int,
        reconciliationDate: Instant,
    ): Either<IllegalStockAmountNegative, InventoryLine> = either {
        InventoryLine(sku, stock, reconciliationDate)
    }
}
