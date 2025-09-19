package com.agilogy.lambdaworld2025.inventory.domain

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import com.agilogy.lambdaworld2025.product.domain.ProductId
import kotlin.time.Instant

class IllegalStockAmountNegative(val amount: Int) :
    InventoryService.Error(
        "Illegal stock amount: $amount. It can't be negative."
    )

@ConsistentCopyVisibility
data class InventoryLine
private constructor(
    val productId: ProductId,
    val stock: Int,
    val reconciliationDate: Instant,
) {

    companion object {
        operator fun invoke(
            productId: ProductId,
            stock: Int,
            reconciliationDate: Instant,
        ): Either<
            IllegalStockAmountNegative,
            InventoryLine,
        > = either {
            ensure(stock >= 0) {
                IllegalStockAmountNegative(stock)
            }
            InventoryLine(
                productId,
                stock,
                reconciliationDate,
            )
        }
    }
}
