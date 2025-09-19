package com.agilogy.lambdaworld2025.inventory.domain

data class IllegalReconciliationDateEarlierThanLast(val last: InventoryLine) :
    Exception("IllegalReconciliationDateEarlierThanLast: $last")

data class IllegalStockAmountNegative(val amount: Int) :
    Exception("IllegalStockAmountNegative: $amount")

data class ProductNotFound(val sku: String) : Exception("ProductNotFound: $sku")
