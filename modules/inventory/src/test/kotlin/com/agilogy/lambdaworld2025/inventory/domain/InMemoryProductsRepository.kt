package com.agilogy.lambdaworld2025.inventory.domain

import arrow.core.raise.context.Raise
import arrow.core.raise.context.raise
import kotlin.uuid.Uuid

class InMemoryProductsRepository : ProductsRepository {

    private val state = mutableMapOf<String, Product>()

    override fun getProduct(sku: String): Product? = state[sku]

    context(_: Raise<IllegalSku>)
    override fun registerProduct(sku: String) {
        if (sku.isBlank()) raise(IllegalSku(sku))
        state[sku] = Product(Uuid.random().toString(), sku)
    }
}
