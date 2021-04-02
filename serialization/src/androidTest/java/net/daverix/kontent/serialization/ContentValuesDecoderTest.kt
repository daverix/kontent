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
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.serialization.ExperimentalSerializationApi
import net.daverix.kontent.serialization.data.*
import org.junit.Test
import org.junit.runner.RunWith
import strikt.api.expectThat
import strikt.assertions.isEqualTo

@ExperimentalSerializationApi
@RunWith(AndroidJUnit4::class)
class ContentValuesDecoderTest {

    @Test
    fun decodeString() {
        verify(
            key = "text",
            value = "hello",
            putValue = ContentValues::put,
            propertyValue = StringObject::text
        )
    }

    @Test
    fun decodeInt() {
        verify(
            key = "value",
            value = 42,
            putValue = ContentValues::put,
            propertyValue = IntObject::value
        )
    }

    @Test
    fun decodeFloat() {
        verify(
            key = "value",
            value = 42.123f,
            putValue = ContentValues::put,
            propertyValue = FloatObject::value
        )
    }

    @Test
    fun decodeDouble() {
        verify(
            key = "value",
            value = 1234567891011121314.12345,
            putValue = ContentValues::put,
            propertyValue = DoubleObject::value
        )
    }

    @Test
    fun decodeLong() {
        verify(
            key = "value",
            value = 1234567891011121314L,
            putValue = ContentValues::put,
            propertyValue = LongObject::value
        )
    }

    @Test
    fun decodeBooleanTrue() {
        verify(
            key = "value",
            value = true,
            putValue = ContentValues::put,
            propertyValue = BooleanObject::value
        )
    }

    @Test
    fun decodeByte() {
        verify(
            key = "value",
            value = 0xF0.toByte(),
            putValue = ContentValues::put,
            propertyValue = ByteObject::value
        )
    }

    @Test
    fun decodeByteArray() {
        verify(
            key = "value",
            value = byteArrayOf(0x00.toByte(), 0x66.toByte(), 0xFF.toByte()),
            putValue = ContentValues::put,
            propertyValue = ByteArrayObject::value
        )
    }

    @Test
    fun decodeGeneric() {
        verify(
            key = "value",
            value = "Testing",
            putValue = ContentValues::put,
            propertyValue = GenericObject<String?>::value
        )
    }

    @Test
    fun decodeNull() {
        verify(
            key = "value",
            value = null,
            putValue = ContentValues::put,
            propertyValue = GenericObject<String?>::value
        )
    }

    private inline fun <Value : Any?, reified Object : Any> verify(
        key: String,
        value: Value,
        putValue: ContentValues.(String,Value)->Unit,
        noinline propertyValue: Object.()->Value
    ) {
        val contentValues = ContentValues()
        contentValues.putValue(key, value)

        val obj: Object = Kontent.decodeFromContentValues(contentValues)

        expectThat(obj) {
            get(propertyValue) isEqualTo value
        }
    }
}