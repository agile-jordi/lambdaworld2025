package com.agilogy.lambdaworld2025.product.domain

import arrow.core.Either
import arrow.core.NonEmptySet
import arrow.core.raise.either
import arrow.core.raise.ensure
import arrow.core.toNonEmptySetOrNull

@JvmInline
value class ProductId
private constructor(val asString: String) {
    companion object {
        operator fun invoke(
            id: String
        ): Either<Error, ProductId> = either {
            ensure(id.isNotBlank()) { Error.Blank }
            val forbiddenCharacters =
                id.toSet() - allowedCharacters
            forbiddenCharacters.toNonEmptySetOrNull()?.let {
                raise(Error.ForbiddenCharacters(it))
            }
            ProductId(id)
        }

        val allowedCharacters =
            (('a'..'z') +
                    ('A'..'Z') +
                    ('0'..'9') +
                    listOf('_'))
                .toSet()
    }

    sealed class Error(val message: String) {
        object Blank : Error("ProductId cannot be blank")

        data class ForbiddenCharacters(
            val forbiddenCharacters: NonEmptySet<Char>
        ) :
            Error(
                "ProductId contains forbidden characters: ${forbiddenCharacters.sorted().joinToString(",")}"
            )
    }
}
