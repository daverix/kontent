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
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.AbstractDecoder
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule


@ExperimentalSerializationApi
class ContentValuesDecoder(
    private val contentValues: ContentValues
) : AbstractDecoder() {
    private lateinit var currentKey: String
    private var elementIndex = 0

    override val serializersModule: SerializersModule = EmptySerializersModule

    override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
        if (elementIndex == descriptor.elementsCount) return CompositeDecoder.DECODE_DONE
        return elementIndex++
    }

    override fun decodeBoolean(): Boolean = contentValues.getAsBoolean(currentKey)

    override fun decodeByte(): Byte = contentValues.getAsByte(currentKey)

    override fun decodeShort(): Short = contentValues.getAsShort(currentKey)

    override fun decodeInt(): Int = contentValues.getAsInteger(currentKey)

    override fun decodeLong(): Long = contentValues.getAsLong(currentKey)

    override fun decodeFloat(): Float = contentValues.getAsFloat(currentKey)

    override fun decodeDouble(): Double = contentValues.getAsDouble(currentKey)

    override fun decodeString(): String = contentValues.getAsString(currentKey)

    override fun decodeNotNullMark(): Boolean = contentValues.get(currentKey) != null

    override fun <T> decodeSerializableValue(deserializer: DeserializationStrategy<T>): T {
        if(deserializer.descriptor.kind == StructureKind.LIST) {
            val elementDescriptor = deserializer.descriptor.getElementDescriptor(0)
            if(elementDescriptor.kind == PrimitiveKind.BYTE) {
                val array = contentValues.getAsByteArray(currentKey)
                @Suppress("UNCHECKED_CAST")
                return array as T
            }
        }
        return super.decodeSerializableValue(deserializer)
    }

    override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder {
        currentKey = descriptor.getElementName(0)
        return super.beginStructure(descriptor)
    }
}