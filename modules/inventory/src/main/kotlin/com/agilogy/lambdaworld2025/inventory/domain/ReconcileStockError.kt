package com.agilogy.lambdaworld2025.inventory.domain

sealed interface ReconcileStockError

data class IllegalReconciliationDateEarlierThanLast(val last: InventoryLine) : ReconcileStockError

data class IllegalStockAmountNegative(val amount: Int) : ReconcileStockError

data class ProductNotFound(val sku: String) : ReconcileStockError
