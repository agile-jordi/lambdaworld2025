package com.agilogy.lambdaworld2025.product.infrastructure

import com.agilogy.lambdaworld2025.product.domain.Product
import com.agilogy.lambdaworld2025.product.domain.ProductsRepository

class DatabaseProductsRepository : ProductsRepository {

    override fun getProduct(sku: String): Product? = TODO()
}
