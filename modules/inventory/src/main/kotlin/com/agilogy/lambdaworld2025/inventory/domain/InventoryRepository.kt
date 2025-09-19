package com.agilogy.lambdaworld2025.inventory.domain

interface InventoryRepository {
    fun register(inventoryLine: InventoryLine)
}
