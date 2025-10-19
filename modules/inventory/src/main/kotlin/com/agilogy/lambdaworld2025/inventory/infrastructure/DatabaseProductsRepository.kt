package com.agilogy.lambdaworld2025.inventory.infrastructure

import arrow.core.raise.Raise
import com.agilogy.lambdaworld2025.inventory.domain.IllegalSku
import com.agilogy.lambdaworld2025.inventory.domain.Product
import com.agilogy.lambdaworld2025.inventory.domain.ProductsRepository

class DatabaseProductsRepository : ProductsRepository {

    override fun getProduct(sku: String): Product? = TODO("Not yet implemented")

    override fun Raise<IllegalSku>.registerProduct(sku: String): Unit = TODO("Not yet implemented")
}
