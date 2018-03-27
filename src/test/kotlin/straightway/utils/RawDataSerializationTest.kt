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
import straightway.expr.minus
import straightway.testing.bdd.Given
import straightway.testing.flow.Empty
import straightway.testing.flow.Equal
import straightway.testing.flow.Not
import straightway.testing.flow.Throw
import straightway.testing.flow.does
import straightway.testing.flow.expect
import straightway.testing.flow.is_
import straightway.testing.flow.to_

class RawDataSerializationTest {

    @Test
    fun `serialization yields non-empty result`() =
            expect("Hello".serializeToByteArray().toList() is_ Not - Empty)

    @Test
    fun `deserialization retrieves serialized object`() =
            Given { "Hello".serializeToByteArray() } when_ {
                deserializeTo<String>()
            } then {
                expect(it.result is_ Equal to_ "Hello")
            }

    @Test
    fun `deserialization of trash throws`() =
            expect({ byteArrayOf(0).deserializeTo<String>() } does Throw.exception)
}