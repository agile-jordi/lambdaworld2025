package com.agilogy.lambdaworld2025.inventory.domain

import arrow.core.getOrElse
import arrow.core.raise.either
import kotlin.test.Test
import kotlin.test.fail
import kotlin.time.Clock

class InventoryServiceTest {

    @Test
    fun foo() {
        val sku = " "
        val inventoryRepository = InMemoryInventoryRepository()
        val productsRepository = InMemoryProductsRepository()
        val service = InventoryService(inventoryRepository, productsRepository)
        either { service.reconcileStock(sku, -23, Clock.System.now()) }
            .getOrElse { fail("Error reconciling stock: $it") }
    }
}
