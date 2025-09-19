package com.agilogy.lambdaworld2025.inventory.domain

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
    ): InventoryLine {
        val reconciliationDate =
            reconciliationDate ?: clock.now()
        val product = productsRepository.getProduct(sku)!!
        val line =
            InventoryLine(
                product.sku,
                stock,
                reconciliationDate,
            )
        inventoryRepository.register(line)
        return line
    }
}
