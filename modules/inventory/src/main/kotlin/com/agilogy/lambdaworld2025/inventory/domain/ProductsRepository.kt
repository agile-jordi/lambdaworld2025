package com.agilogy.lambdaworld2025.inventory.domain

import arrow.core.raise.Raise

interface ProductsRepository {

    fun getProduct(sku: String): Product?

    context(_: Raise<IllegalSku>)
    fun registerProduct(sku: String): Unit
}

data class IllegalSku(val sku: String) : ReconcileStockError
