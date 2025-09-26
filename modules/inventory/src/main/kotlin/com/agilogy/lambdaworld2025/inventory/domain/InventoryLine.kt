package com.agilogy.lambdaworld2025.inventory.domain

import kotlin.time.Instant

class IllegalStockAmountNegative(val amount: Int) :
    InventoryService.Error(
        "Illegal stock amount: $amount. It can't be negative."
    )

@ConsistentCopyVisibility
data class InventoryLine
private constructor(
    val productId: String,
    val stock: Int,
    val reconciliationDate: Instant,
) {

    companion object {
        operator fun invoke(
            productId: String,
            stock: Int,
            reconciliationDate: Instant,
        ) =
            ensure(stock >= 0) {
                    IllegalStockAmountNegative(stock)
                }
                .map {
                    InventoryLine(
                        productId,
                        stock,
                        reconciliationDate,
                    )
                }
    }
}
