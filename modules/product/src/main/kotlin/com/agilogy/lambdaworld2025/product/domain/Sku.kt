package com.agilogy.lambdaworld2025.product.domain

import arrow.core.Either
import arrow.core.NonEmptySet
import arrow.core.raise.either
import arrow.core.raise.ensure
import arrow.core.toNonEmptySetOrNull

@JvmInline
value class Sku private constructor(val asString: String) {
    companion object {
        operator fun invoke(
            id: String
        ): Either<Error, Sku> = either {
            ensure(id.isNotBlank()) { Error.Blank }
            val forbiddenCharacters =
                id.toSet() - allowedCharacters
            forbiddenCharacters.toNonEmptySetOrNull()?.let {
                raise(Error.ForbiddenCharacters(it))
            }
            Sku(id)
        }

        val allowedCharacters =
            (('a'..'z') +
                    ('A'..'Z') +
                    ('0'..'9') +
                    listOf('-'))
                .toSet()
    }

    sealed class Error(val message: String) {
        object Blank : Error("Sku cannot be blank")

        data class ForbiddenCharacters(
            val forbiddenCharacters: NonEmptySet<Char>
        ) :
            Error(
                "Sku contains forbidden characters: ${forbiddenCharacters.sorted().joinToString(",")}"
            )
    }
}
