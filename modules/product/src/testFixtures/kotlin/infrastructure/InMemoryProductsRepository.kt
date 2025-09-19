package infrastructure

import com.agilogy.lambdaworld2025.product.domain.Product
import com.agilogy.lambdaworld2025.product.domain.ProductsRepository

class InMemoryProductsRepository(
    initialState: Set<Product>
) : ProductsRepository {

    private val products =
        mutableListOf<Product>(*initialState.toTypedArray())

    override fun getProduct(sku: String): Product? =
        products.find { it.sku == sku }
}
