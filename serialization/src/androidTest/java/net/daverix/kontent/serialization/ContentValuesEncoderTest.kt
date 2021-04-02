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
class ContentValuesEncoderTest {

    @Test
    fun encodeString() {
        verify(value = "hello", ::StringObject) { getAsString("text") }
    }

    @Test
    fun encodeInt() {
        verify(value = 42, ::IntObject) { getAsInteger("value") }
    }

    @Test
    fun encodeFloat() {
        verify(value = 42.123f, ::FloatObject) { getAsFloat("value") }
    }

    @Test
    fun encodeDouble() {
        verify(value = 1234567891011121314.12345, ::DoubleObject) { getAsDouble("value") }
    }

    @Test
    fun encodeLong() {
        verify(value = 1234567891011121314L, ::LongObject) { getAsLong("value") }
    }

    @Test
    fun encodeBooleanTrue() {
        verify(value = true, ::BooleanObject) { getAsBoolean("value") }
    }

    @Test
    fun encodeByte() {
        verify(value = 0xF0.toByte(), ::ByteObject) { getAsByte("value") }
    }

    @Test
    fun encodeByteArray() {
        verify(
            value = byteArrayOf(0x00.toByte(), 0x66.toByte(), 0xFF.toByte()),
            ::ByteArrayObject
        ) { getAsByteArray("value") }
    }

    @Test
    fun encodeGeneric() {
        verify<String?, GenericObject<String?>>(
            value = "Testing",
            ::GenericObject
        ) { getAsString("value") }
    }

    @Test
    fun encodeNull() {
        verify(
            value = null,
            ::GenericObject
        ) { getAsString("value") }
    }

    private inline fun <T : Any?, reified R : Any> verify(
        value: T,
        createObject: (T) -> R,
        noinline valuesGetter: ContentValues.() -> T
    ) {
        val obj = createObject(value)
        val values = Kontent.encodeToContentValues(obj)

        expectThat(values) {
            get(valuesGetter) isEqualTo value
        }
    }
}