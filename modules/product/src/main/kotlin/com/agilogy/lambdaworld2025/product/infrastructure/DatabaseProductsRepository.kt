package com.agilogy.lambdaworld2025.product.infrastructure

import com.agilogy.lambdaworld2025.product.domain.Product
import com.agilogy.lambdaworld2025.product.domain.ProductsRepository
import com.agilogy.lambdaworld2025.product.domain.Sku

class DatabaseProductsRepository : ProductsRepository {

    override fun getProduct(sku: Sku): Product? = TODO()
}
