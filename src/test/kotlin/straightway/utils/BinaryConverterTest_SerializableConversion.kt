/*
 * Copyright 2016 github.com/straightway
 *
 *  Licensed under the Apache License, Version 2.0 (the &quot;License&quot;);
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an &quot;AS IS&quot; BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package straightway.utils

import org.junit.jupiter.api.Test
import straightway.testing.bdd.Given
import straightway.testing.flow.Equal
import straightway.testing.flow.expect
import straightway.testing.flow.is_
import straightway.testing.flow.to_
import java.io.Serializable

class BinaryConverterTest_SerializableConversion {

    @Test
    fun `Int as serializable is converted raw`() =
            Given {
                83 as Serializable
            } when_ {
                toByteArray()
            } then {
                expect(it.result is_ Equal to_ 83.toByteArray())
            }

    @Test
    fun `Long as serializable is converted raw`() =
            Given {
                83L as Serializable
            } when_ {
                toByteArray()
            } then {
                expect(it.result is_ Equal to_ 83L.toByteArray())
            }

    @Test
    fun `String as serializable is converted using UTF-8 bytes`() =
            Given {
                "Hello" as Serializable
            } when_ {
                toByteArray()
            } then {
                expect(it.result is_ Equal to_ "Hello".toByteArray(Charsets.UTF_8))
            }

    @Test
    fun `other serializable object is converted using raw data serialization`() {
        Given {
            OtherSerializable(83) as Serializable
        } when_ {
            toByteArray()
        } then {
            expect(it.result is_ Equal to_ this.serializeToByteArray())
        }
    }

    private data class OtherSerializable(val value: Int) : Serializable {
        companion object { const val serialVersionUID = 1L }
    }
}