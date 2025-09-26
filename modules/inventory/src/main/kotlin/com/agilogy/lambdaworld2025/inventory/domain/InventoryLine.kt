package com.agilogy.lambdaworld2025.inventory.domain

import kotlin.time.Instant

class IllegalStockAmountNegative(val amount: Int) :
    IllegalArgumentException(
        "Illegal stock amount: $amount. It can't be negative."
    )

data class InventoryLine(
    val productId: String,
    val stock: Int,
    val reconciliationDate: Instant,
) {
    init {
        if (stock < 0)
            throw IllegalStockAmountNegative(stock)
    }
}
