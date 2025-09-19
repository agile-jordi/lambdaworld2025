package infrastructure

import com.agilogy.lambdaworld2025.inventory.domain.InventoryLine
import com.agilogy.lambdaworld2025.inventory.domain.InventoryRepository

class InMemoryInventoryRepository(
    initialState: Set<InventoryLine>
) : InventoryRepository {
    private val stock =
        mutableListOf(*initialState.toTypedArray())

    override fun register(inventoryLine: InventoryLine) {
        this.stock.add(inventoryLine)
    }

    val state: Set<InventoryLine>
        get() = stock.toSet()
}
