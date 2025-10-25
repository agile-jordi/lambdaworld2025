package com.agilogy.lambdaworld2025.inventory.domain

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.getOrElse
import arrow.core.raise.ExperimentalRaiseAccumulateApi
import arrow.core.raise.Raise
import arrow.core.raise.RaiseAccumulate
import arrow.core.raise.accumulate as accumulateEither
import arrow.core.raise.context.accumulate
import arrow.core.raise.context.accumulating
import arrow.core.raise.context.raise
import arrow.core.raise.either
import arrow.core.raise.recover
import kotlin.time.Instant

class InventoryService(
    val inventoryRepository: InventoryRepository,
    val productsRepository: ProductsRepository,
) {

    // This seems really natural. We just call functions that happen to return whatever we need
    context(_: Raise<ReconcileStockError>)
    // It could have been:
    // context(r: Raise<ReconcileStockError>)
    // But I didn't find any reason to name the context parameter here. On the contrary: for me, the
    // only (very small) confusion experimenting with a context parameter with a name was IntelliJ
    // not complaining of the parameter name not being used. In my mind, if I never use the `r` name
    // it could be marked as unused somehow telling me to use _ to avoid polluting the name space.
    // That would be consequent with IntelliJ complaining about me naming a parameter I don't use
    // in `foo { a -> 23 }`
    fun failFastWithContext(sku: String, stock: Int, reconciliationDate: Instant): InventoryLine {
        val currentStock =
            recover({ inventoryRepository.getCurrentStock(sku) }) {
                // Functions returning Unit have no ceremony and properly shortcut to the
                // context(Raise<...>) above
                productsRepository.registerProduct(sku)
                null
            }

        if (currentStock != null && currentStock.reconciliationDate >= reconciliationDate) {
            // Also very natural, like throw:
            raise(IllegalReconciliationDateEarlierThanLast(currentStock))
        }
        val line = InventoryLine(sku, stock, reconciliationDate)
        inventoryRepository.register(line)
        return line
    }

    // I find accumulation with context not that intuitive:
    @OptIn(ExperimentalRaiseAccumulateApi::class)
    context(_: Raise<NonEmptyList<ReconcileStockError>>)
    fun accumulateWithContext(sku: String, stock: Int, reconciliationDate: Instant): InventoryLine =
        accumulate {
            val currentStock =
                recover({ inventoryRepository.getCurrentStock(sku) }) {
                    // Ignoring the value returned by `accumulating` here is ok, so far so good.
                    accumulating { productsRepository.registerProduct(sku) }

                    // I would have expected for `productsRepository.registerProduct` to accumulate
                    // errors without any other "ceremony" (i.e. without wrapping it in
                    // `accumulating`),
                    // given that we are inside an `accumulate` block...
                    // Of course this can not be done in the general case, because we cannot
                    // return a Value<A> when the invoked function returns A.
                    // Even though it can't be done, I still feel like accumulating errors and
                    // returning
                    // Unit would be the natural thing to expect.
                    // I don't think this has an alternative solution, just providing my thoughts
                    // as someone approaching accumulation with context parameters for the first
                    // time.

                    null
                }

            if (currentStock != null && currentStock.reconciliationDate >= reconciliationDate) {
                // This won't compile, which confused me:
                // accumulate(IllegalReconciliationDateEarlierThanLast(currentStock))

                // This worked, but it seems boilerplatey:
                // accumulating{ raise(IllegalReconciliationDateEarlierThanLast(currentStock))}

                // This also worked, but it seems even more convoluted. I don't thing using
                // `contextOf`
                // to be usually expected
                // contextOf<RaiseAccumulate<ReconcileStockError>>()
                //    .accumulate(IllegalReconciliationDateEarlierThanLast(currentStock))

                // If I do use contextOf, IntelliJ suggests removing the type parameter, which
                // also works but seems confusing to me because the presence of the word "of".
                // Context
                // of what?
                // contextOf().accumulate(IllegalReconciliationDateEarlierThanLast(currentStock))

                // I thought it may be because I was expected to use the context parameter name...
                // but this does not work either because we don't want the context parameter
                // of type Raise<NonEmptyList<Error>> but the implicit one in the `accumulate {...}`
                // block:
                // r.accumulate(IllegalReconciliationDateEarlierThanLast(currentStock))

                // r.accumulate didn't work but IntelliJ also suggested the wrong import (the
                // accumulate taking a block) which caused more confusion.

                // The fact that this accumulate has the same name than the accumulate taking a
                // block was confusing not only IntelliJ (by suggesting the wrong import) but also
                // me. I
                // see how accumulate seems the right word when passing the error you want to
                // accumulate, but maybe there is some better name for the accumulate dsl thing
                // taking a
                // block?

                // There may be this function somewhere, but I couldn't find it, so I programmed it
                // myself. I feel like it could be part of the standard Raise dsl.
                context(r: RaiseAccumulate<E>)
                fun <E> accumulate(e: E) = r.accumulate(e)

                // This is the code that seems natural to me:
                accumulate(IllegalReconciliationDateEarlierThanLast(currentStock))
            }

            val line by accumulating { InventoryLine(sku, stock, reconciliationDate) }

            // Ignoring the returned value here is also ok. In fact, it seems we won't want to use
            // the returned Value<Unit> at all.

            accumulating { inventoryRepository.register(line) }

            // Is there a way to provide a second version of accumulating that returns Unit
            // when the A value is Unit without confusing the compiler?
            // Something like this:
            // context(raise: RaiseAccumulate<Error>)
            // inline fun <Error> accumulating(block: context(RaiseAccumulate<Error>) () -> Unit):
            // Unit
            // {
            //    contract { callsInPlace(block, AT_MOST_ONCE) }
            //    with(raise) { accumulating(block) }
            // }
            // I don't know whether this would be compatible with the current version so that it is
            // caught by the same import and the user doesn't need to do anything special.
            line
        }

    fun failFastWithEither(
        sku: String,
        stock: Int,
        reconciliationDate: Instant,
    ): Either<ReconcileStockError, InventoryLine> = either {
        val currentStock =
            inventoryRepository.getCurrentStockEither(sku).getOrElse {
                // Known issue: Ignoring the return value is ok to the compiler (unless unused
                // return values is available):
                // productsRepository.registerProductEither(sku)
                productsRepository.registerProductEither(sku).bind()
                null
            }

        if (currentStock != null && currentStock.reconciliationDate >= reconciliationDate) {
            raise(IllegalReconciliationDateEarlierThanLast(currentStock))
        }
        // Having to use `bind` here is more ceremony... but it feels ok, because I need somehow
        // to "get" the correct value from inside the `Either`. I don't have an strong opinion on
        // the name `bind`... but there may be more natural names to imply you are getting the
        // correct value and shortcutting if there is any error (`getOrRaise`)?
        val line = InventoryLineEither(sku, stock, reconciliationDate).bind()
        inventoryRepository.registerEither(line).bind()
        line
    }

    // AN ARGUMENT FOR USING EITHER INSTEAD OF RAISE DSL + CONTEXT PARMETERS

    // Thinking about rich errors... the Either version of the shortcutting solution, although more
    // verbose than the context parameters version, may be more like the upcoming rich errors
    // solution, which would be a good thing for long projects that may want to eventually
    // use rich errors.
    // Whenever rich errors is available we would need to:
    // 1. Replace all `bind()` invocations with `?`:
    //   `val line = InventoryLineEither(sku, stock, reconciliationDate)?`
    // 2. Replace all `raise` invocations with `return`:
    //   return IllegalReconciliationDateEarlierThanLast(currentStock)
    // 3. Replace `getOrElse` for `?:` when the error is not used:
    //   inventoryRepository.getCurrentStockEither(sku) ?:
    // productsRepository.registerProductEither(sku)...
    // 4. And we will probably want some API for something like `getOrElse`, where you want to
    // handle the error but you need the error to handle and `?:` is not enough.
    // Naturally, I didn't cover all of the Raise API with this small example, so there may be
    // things I didn't take into account.

    @OptIn(ExperimentalRaiseAccumulateApi::class)
    fun accumulationWithEither(
        sku: String,
        stock: Int,
        reconciliationDate: Instant,
    ): Either<NonEmptyList<ReconcileStockError>, InventoryLine> =
        // Although I see the value of having just one `accumulate` in the Raise DSL, that you can
        // use inside `either` blocks, I find some single function for accumulating in either would
        // be nice, like:
        // eitherAccumulate {
        //     val currentStock = ...
        //     ...
        // }
        either {
            // Note: Just accumulate renamed because I use both in the same file:
            accumulateEither {
                val currentStock =
                    inventoryRepository.getCurrentStockEither(sku).getOrElse {
                        // productsRepository.registerProductEither(sku)
                        // My initial intuition was that bind() would accumulate errors, but I
                        // understand that we may want one behavior or the other depending on the
                        // use case. Maybe renaming `bind` to something like `getOrRaise` would make
                        // clear that the function raises an error even in `accumulate{...}`. And
                        // `bindOrAccumulate` could be `getOrAccumulate`.
                        productsRepository.registerProductEither(sku).bindOrAccumulate()
                        null
                    }

                if (currentStock != null && currentStock.reconciliationDate >= reconciliationDate) {
                    // I didn't have any trouble with `accumulate` here, while I had it with the
                    // context parameters version
                    // But I feel it odd that it returns a Value<Nothing>. Why not simply return
                    // Unit? I'm like very vigilant of not ignoring return values and this
                    // Value<Nothing> confused me
                    accumulate(IllegalReconciliationDateEarlierThanLast(currentStock))
                }
                val line by InventoryLineEither(sku, stock, reconciliationDate).bindOrAccumulate()
                inventoryRepository.registerEither(line).bindOrAccumulate()
                line
            }
        }

    // This is pure speculation of what a rich errors solution may look like.
    // Added just so I can explore the idea and look at it as a the possible future
    // mainstream design I would be interested in imitating to minimize future migration efforts.
    // fun failFastWithRichErrors(
    //    sku: String,
    //    stock: Int,
    //    reconciliationDate: Instant,
    // ): InventoryLine | ReconcileStockError {
    //    val currentStock =
    //        inventoryRepository.getCurrentStock(sku) ?: run{
    //            productsRepository.registerProductEither(sku)?
    //            null
    //        }
    //
    //    if (currentStock != null && currentStock.reconciliationDate >= reconciliationDate) {
    //        return IllegalReconciliationDateEarlierThanLast(currentStock)
    //    }
    //    val line = InventoryLineEither(sku, stock, reconciliationDate)?
    //    inventoryRepository.registerEither(line)?
    //    return line
    // }
}
