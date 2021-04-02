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

import android.database.Cursor
import android.database.MatrixCursor
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.AbstractEncoder
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule


@ExperimentalSerializationApi
class CursorEncoder : AbstractEncoder() {
    private val rows = mutableListOf<Map<String, Any?>>()
    private lateinit var currentKey: String
    private var currentValues = mutableMapOf<String, Any?>()

    override val serializersModule: SerializersModule = EmptySerializersModule


    override fun encodeValue(value: Any) {
        currentValues[currentKey] = value
    }

    override fun encodeNull() {
        currentValues[currentKey] = null
    }

    override fun encodeBoolean(value: Boolean) {
        currentValues[currentKey] = if(value) 1 else 0
    }

    override fun beginCollection(
        descriptor: SerialDescriptor,
        collectionSize: Int
    ): CompositeEncoder {
        val elementDescriptor = descriptor.getElementDescriptor(0)
            if (elementDescriptor.kind == PrimitiveKind.BYTE) {
            return ByteArrayEncoder(currentValues, currentKey, collectionSize)
        }

        return super.beginCollection(descriptor, collectionSize)
    }

    override fun endStructure(descriptor: SerialDescriptor) {
        if(descriptor.kind == StructureKind.CLASS) {
            rows += currentValues
        }
        super.endStructure(descriptor)
    }

    override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder {
        if(descriptor.kind == StructureKind.CLASS) {
            currentValues = mutableMapOf()
        }

        currentKey = descriptor.getElementName(0)
        return super.beginStructure(descriptor)
    }

    fun createCursor(): Cursor {
        val cursor = MatrixCursor(currentValues.keys.toTypedArray(), rows.size)
        rows.forEach { row ->
            cursor.addRow(row.values.toTypedArray())
        }
        return cursor
    }

    private class ByteArrayEncoder(
        private val map: MutableMap<String, Any?>,
        private val key: String,
        collectionSize: Int
    ) : AbstractEncoder() {
        private val bytes = ByteArray(collectionSize)
        private var index = 0

        override val serializersModule: SerializersModule = EmptySerializersModule

        override fun encodeByte(value: Byte) {
            bytes[index++] = value
        }

        override fun endStructure(descriptor: SerialDescriptor) {
            map[key] = bytes
        }
    }
}
