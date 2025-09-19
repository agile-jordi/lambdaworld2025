package com.agilogy.lambdaworld2025.product.domain

import arrow.core.left
import arrow.core.nonEmptySetOf
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class ProductIdTest {
    @Test
    @DisplayName("it should reject empty ids")
    fun shouldRejectEmptyIds() {
        assertEquals(
            ProductId.Error.Blank.left(),
            ProductId(""),
        )
    }

    @Test
    @DisplayName(
        "it should reject ids with forbidden characters"
    )
    fun shouldRejectForbiddenCharacters() {
        assertEquals(
            ProductId.Error.ForbiddenCharacters(
                    nonEmptySetOf(' ', '$', '-')
                )
                .left(),
            ProductId("invalid id with $ and -"),
        )
    }
}
