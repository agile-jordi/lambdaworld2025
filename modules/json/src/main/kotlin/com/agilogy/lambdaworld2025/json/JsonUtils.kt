package com.agilogy.lambdaworld2025.json

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

fun jsonObject(
    vararg pairs: Pair<String, JsonElement>
): JsonObject = JsonObject(pairs.toMap())

fun jsonArray(vararg elements: JsonElement): JsonArray =
    JsonArray(elements.toList())

val String.json
    get() = JsonPrimitive(this)
val Int.json
    get() = JsonPrimitive(this)
val Long.json
    get() = JsonPrimitive(this)
val Boolean.json
    get() = JsonPrimitive(this)
