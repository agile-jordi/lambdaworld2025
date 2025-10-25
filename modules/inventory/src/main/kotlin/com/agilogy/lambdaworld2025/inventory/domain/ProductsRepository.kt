package com.agilogy.lambdaworld2025.inventory.domain

import arrow.core.Either
import arrow.core.raise.context.Raise
import arrow.core.raise.context.either

interface ProductsRepository {

    fun getProduct(sku: String): Product?

    context(_: Raise<IllegalSku>)
    fun registerProduct(sku: String)

    fun registerProductEither(sku: String): Either<IllegalSku, Unit> = either {
        registerProduct(sku)
    }
}

data class IllegalSku(val sku: String) : ReconcileStockError
