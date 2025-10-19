package com.agilogy.lambdaworld2025.inventory.infrastructure

import arrow.core.Either
import com.agilogy.lambdaworld2025.inventory.domain.IllegalSku
import com.agilogy.lambdaworld2025.inventory.domain.Product
import com.agilogy.lambdaworld2025.inventory.domain.ProductsRepository

class DatabaseProductsRepository : ProductsRepository {

    override fun getProduct(sku: String): Product? = TODO("Not yet implemented")

    override fun registerProduct(sku: String): Either<IllegalSku, Unit> =
        TODO("Not yet implemented")
}
