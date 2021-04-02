/*
   Copyright 2021 David Laurell

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package net.daverix.kontent.serialization

import android.content.ContentValues
import android.database.Cursor
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializer

@ExperimentalSerializationApi
object Kontent {
    fun <T : Any> encodeToContentValues(serializer: KSerializer<T>, value: T): ContentValues {
        val encoder = ContentValuesEncoder()
        encoder.encodeSerializableValue(serializer, value)
        return encoder.contentValues
    }

    inline fun <reified T : Any> encodeToContentValues(value: T): ContentValues =
        encodeToContentValues(serializer(), value)

    fun <T> decodeFromContentValues(contentValues: ContentValues, deserializer: DeserializationStrategy<T>): T {
        val decoder = ContentValuesDecoder(contentValues)
        return decoder.decodeSerializableValue(deserializer)
    }

    inline fun <reified T> decodeFromContentValues(contentValues: ContentValues): T =
        decodeFromContentValues(contentValues, serializer())

    fun <T : Any> encodeToCursor(serializer: KSerializer<T>, value: T): Cursor {
        val encoder = CursorEncoder()
        encoder.encodeSerializableValue(serializer, value)
        return encoder.createCursor()
    }

    inline fun <reified T : Any> encodeToCursor(value: T): Cursor =
        encodeToCursor(serializer(), value)

    fun <T> decodeFromCursor(cursor: Cursor, deserializer: DeserializationStrategy<T>): T {
        val decoder = CursorDecoder(cursor)
        return decoder.decodeSerializableValue(deserializer)
    }

    inline fun <reified T> decodeFromCursor(cursor: Cursor): T =
        decodeFromCursor(cursor, serializer())
}
