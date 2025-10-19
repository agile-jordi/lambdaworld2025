package com.agilogy.lambdaworld2025.inventory.domain

import arrow.core.Either

interface ProductsRepository {

    fun getProduct(sku: String): Product?

    fun registerProduct(sku: String): Either<IllegalSku, Unit>
}

data class IllegalSku(val sku: String) : ReconcileStockError
