package com.agilogy.lambdaworld2025.product.domain

import arrow.core.left
import arrow.core.nonEmptySetOf
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class SkuTest {
    @Test
    @DisplayName("it should reject empty ids")
    fun shouldRejectEmptyIds() {
        assertEquals(Sku.Error.Blank.left(), Sku(""))
    }

    @Test
    @DisplayName(
        "it should reject ids with forbidden characters"
    )
    fun shouldRejectForbiddenCharacters() {
        assertEquals(
            Sku.Error.ForbiddenCharacters(
                    nonEmptySetOf(' ', '$', '_')
                )
                .left(),
            Sku("invalid id with $ and _"),
        )
    }
}
