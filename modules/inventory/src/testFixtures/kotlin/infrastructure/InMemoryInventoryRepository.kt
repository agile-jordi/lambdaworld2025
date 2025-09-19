package infrastructure

import com.agilogy.lambdaworld2025.inventory.domain.InventoryLine
import com.agilogy.lambdaworld2025.inventory.domain.InventoryRepository
import com.agilogy.lambdaworld2025.product.domain.ProductId

class InMemoryInventoryRepository(
    initialState: Set<InventoryLine>
) : InventoryRepository {
    private val stock =
        mutableListOf(*initialState.toTypedArray())

    override fun register(inventoryLine: InventoryLine) {
        this.stock.add(inventoryLine)
    }

    override fun getCurrentStock(
        productId: ProductId
    ): InventoryLine? =
        stock
            .filter { it.productId == productId }
            .maxByOrNull { it.reconciliationDate }

    val state: Set<InventoryLine>
        get() = stock.toSet()
}
