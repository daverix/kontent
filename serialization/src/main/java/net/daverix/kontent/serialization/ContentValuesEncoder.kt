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
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.AbstractEncoder
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule


@ExperimentalSerializationApi
class ContentValuesEncoder : AbstractEncoder() {
    val contentValues = ContentValues()
    private lateinit var currentKey: String

    override val serializersModule: SerializersModule = EmptySerializersModule

    override fun encodeNull() {
        contentValues.putNull(currentKey)
    }

    override fun encodeString(value: String) {
        contentValues.put(currentKey, value)
    }

    override fun encodeBoolean(value: Boolean) {
        contentValues.put(currentKey, value)
    }

    override fun encodeByte(value: Byte) {
        contentValues.put(currentKey, value)
    }

    override fun encodeDouble(value: Double) {
        contentValues.put(currentKey, value)
    }

    override fun encodeFloat(value: Float) {
        contentValues.put(currentKey, value)
    }

    override fun encodeLong(value: Long) {
        contentValues.put(currentKey, value)
    }

    override fun encodeInt(value: Int) {
        contentValues.put(currentKey, value)
    }

    override fun encodeShort(value: Short) {
        contentValues.put(currentKey, value)
    }

    override fun beginCollection(
        descriptor: SerialDescriptor,
        collectionSize: Int
    ): CompositeEncoder {
        val elementDescriptor = descriptor.getElementDescriptor(0)
            if (elementDescriptor.kind == PrimitiveKind.BYTE) {
            return ByteArrayEncoder(currentKey, contentValues, collectionSize)
            }

        return super.beginCollection(descriptor, collectionSize)
    }

    override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder {
        currentKey = descriptor.getElementName(0)
        return super.beginStructure(descriptor)
    }

    private class ByteArrayEncoder(
        private val key: String,
        private val contentValues: ContentValues,
        collectionSize: Int
    ) : AbstractEncoder() {
        private val bytes = ByteArray(collectionSize)
        private var index = 0

        override val serializersModule: SerializersModule = EmptySerializersModule

        override fun encodeByte(value: Byte) {
            bytes[index++] = value
        }

        override fun endStructure(descriptor: SerialDescriptor) {
            contentValues.put(key, bytes)
        }
    }
}
