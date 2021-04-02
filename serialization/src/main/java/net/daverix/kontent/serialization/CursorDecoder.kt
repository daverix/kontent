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
class CursorDecoder(
    private val cursor: Cursor,
) : AbstractDecoder() {
    private lateinit var currentKey: String
    private var elementIndex = 0
    
    override val serializersModule: SerializersModule = EmptySerializersModule

    override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
        if (elementIndex == descriptor.elementsCount) return CompositeDecoder.DECODE_DONE
        return elementIndex++
    }

    override fun decodeBoolean(): Boolean = cursor.getInt(cursor.getColumnIndexOrThrow(currentKey)) != 0

    override fun decodeByte(): Byte = cursor.getInt(cursor.getColumnIndexOrThrow(currentKey)).toByte()

    override fun decodeShort(): Short = cursor.getShort(cursor.getColumnIndexOrThrow(currentKey))

    override fun decodeInt(): Int = cursor.getInt(cursor.getColumnIndexOrThrow(currentKey))

    override fun decodeLong(): Long = cursor.getLong(cursor.getColumnIndexOrThrow(currentKey))

    override fun decodeFloat(): Float = cursor.getFloat(cursor.getColumnIndexOrThrow(currentKey))

    override fun decodeDouble(): Double = cursor.getDouble(cursor.getColumnIndexOrThrow(currentKey))

    override fun decodeString(): String = cursor.getString(cursor.getColumnIndexOrThrow(currentKey))

    override fun decodeNotNullMark(): Boolean = !cursor.isNull(cursor.getColumnIndexOrThrow(currentKey))

    override fun <T> decodeSerializableValue(deserializer: DeserializationStrategy<T>): T {
        if(deserializer.descriptor.kind == StructureKind.LIST) {
            val elementDescriptor = deserializer.descriptor.getElementDescriptor(0)
            if(elementDescriptor.kind == PrimitiveKind.BYTE) {
                val array = cursor.getBlob(cursor.getColumnIndexOrThrow(currentKey))
                @Suppress("UNCHECKED_CAST")
                return array as T
            }
        }
        return super.decodeSerializableValue(deserializer)
    }

    override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder {
        currentKey = descriptor.getElementName(0)

        if(descriptor.kind == StructureKind.CLASS) {
            if(!cursor.moveToNext())
                error("cannot read next row in cursor")
        } else if(descriptor.kind == StructureKind.LIST) {
            return CursorDecoder(cursor)
        }

        return super.beginStructure(descriptor)
    }

    override fun decodeSequentially(): Boolean = true
    override fun decodeCollectionSize(descriptor: SerialDescriptor): Int = cursor.count
}