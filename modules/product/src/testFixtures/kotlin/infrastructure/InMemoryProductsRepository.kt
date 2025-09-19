package infrastructure

import com.agilogy.lambdaworld2025.product.domain.Product
import com.agilogy.lambdaworld2025.product.domain.ProductsRepository
import com.agilogy.lambdaworld2025.product.domain.Sku

class InMemoryProductsRepository(
    initialState: Set<Product>
) : ProductsRepository {

    private val products =
        mutableListOf<Product>(*initialState.toTypedArray())

    override fun getProduct(sku: Sku): Product? =
        products.find { it.sku == sku }
}
