package com.agilogy.lambdaworld2025.product.domain

interface ProductsRepository {

    fun getProduct(sku: String): Product?
}
