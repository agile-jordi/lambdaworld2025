package com.agilogy.lambdaworld2025.inventory.domain

import arrow.core.raise.Raise
import kotlin.time.Instant

@ConsistentCopyVisibility
data class InventoryLine
private constructor(val sku: String, val stock: Int, val reconciliationDate: Instant) {

    companion object {
        operator fun Raise<IllegalStockAmountNegative>.invoke(
            sku: String,
            stock: Int,
            reconciliationDate: Instant,
        ): InventoryLine {
            if (stock < 0) raise(IllegalStockAmountNegative(stock))
            return InventoryLine(sku, stock, reconciliationDate)
        }
    }
}
