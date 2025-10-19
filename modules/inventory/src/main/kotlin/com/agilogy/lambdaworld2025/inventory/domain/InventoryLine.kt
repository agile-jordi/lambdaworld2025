package com.agilogy.lambdaworld2025.inventory.domain

import kotlin.time.Instant

data class InventoryLine(val sku: String, val stock: Int, val reconciliationDate: Instant) {
    init {
        if (stock < 0) throw IllegalStockAmountNegative(stock)
    }
}
