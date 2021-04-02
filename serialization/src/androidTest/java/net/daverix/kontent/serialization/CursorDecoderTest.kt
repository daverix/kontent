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

import android.database.MatrixCursor
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.serialization.ExperimentalSerializationApi
import net.daverix.kontent.serialization.data.*
import org.junit.Test
import org.junit.runner.RunWith
import strikt.api.expectThat
import strikt.assertions.containsExactly
import strikt.assertions.isEqualTo

@ExperimentalSerializationApi
@RunWith(AndroidJUnit4::class)
class CursorDecoderTest {

    @Test
    fun decodeString() {
        verify(
            key = "text",
            expected = "hello",
            propertyValue = StringObject::text
        )
    }

    @Test
    fun decodeInt() {
        verify(
            key = "value",
            expected = 42,
            propertyValue = IntObject::value
        )
    }

    @Test
    fun decodeFloat() {
        verify(
            key = "value",
            expected = 42.123f,
            propertyValue = FloatObject::value
        )
    }

    @Test
    fun decodeDouble() {
        verify(
            key = "value",
            expected = 1234567891011121314.12345,
            propertyValue = DoubleObject::value
        )
    }

    @Test
    fun decodeLong() {
        verify(
            key = "value",
            expected = 1234567891011121314L,
            propertyValue = LongObject::value
        )
    }

    @Test
    fun decodeBooleanTrue() {
        verify(
            key = "value",
            value = 1,
            expected = true,
            propertyValue = BooleanObject::value
        )
    }

    @Test
    fun decodeByte() {
        verify(
            key = "value",
            expected = 0xF0.toByte(),
            propertyValue = ByteObject::value
        )
    }

    @Test
    fun decodeByteArray() {
        verify(
            key = "value",
            expected = byteArrayOf(0x00.toByte(), 0x66.toByte(), 0xFF.toByte()),
            propertyValue = ByteArrayObject::value
        )
    }

    @Test
    fun decodeGeneric() {
        verify(
            key = "value",
            expected = "Testing",
            propertyValue = GenericObject<String?>::value
        )
    }

    @Test
    fun decodeNull() {
        verify(
            key = "value",
            expected = null,
            propertyValue = GenericObject<String?>::value
        )
    }

    @Test
    fun decodeList() {
        val cursor = MatrixCursor(arrayOf("value"))
        cursor.addRow(arrayOf("hello"))
        cursor.addRow(arrayOf("world"))

        val actual: List<GenericObject<String>> = Kontent.decodeFromCursor(cursor)

        expectThat(actual) {
            containsExactly(GenericObject("hello"), GenericObject("world"))
        }
    }

    private inline fun <reified Value : Any?, reified Object : Any> verify(
        key: String,
        expected: Value,
        value: Any? = expected,
        crossinline propertyValue: Object.()->Value
    ) {
        val cursor = MatrixCursor(arrayOf(key))
        cursor.addRow(arrayOf(value))

        val obj: Object = Kontent.decodeFromCursor(cursor)

        expectThat(obj) {
            get { propertyValue() } isEqualTo expected
        }
    }
}