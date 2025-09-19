package com.agilogy.lambdaworld2025.inventory.domain

import kotlin.time.Instant

data class InventoryLine(
    val productId: String,
    val stock: Int,
    val reconciliationDate: Instant,
) {
    init {
        require(stock >= 0) { "Stock cannot be negative" }
    }
}
