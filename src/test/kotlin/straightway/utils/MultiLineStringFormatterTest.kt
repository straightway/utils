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

class MultiLineStringFormatterTest {

    @Test
    fun `formatted empty list consists only of braces`() =
            Given {
                listOf<Any>()
            } when_ {
                joinMultiLine()
            } then {
                expect(it.result is_ Equal to_ "{}")
            }

    @Test
    fun `formatted non-empty list`() =
            Given {
                listOf<Any>(1, 2)
            } when_ {
                joinMultiLine()
            } then {
                expect(it.result is_ Equal to_ "{\n1\n2\n}")
            }

    @Test
    fun `formatted non-empty list with indent`() =
            Given {
                listOf<Any>(1, 2)
            } when_ {
                joinMultiLine(indentation = 2)
            } then {
                expect(it.result is_ Equal to_ "{\n  1\n  2\n}")
            }

    @Test
    fun `formatted non-empty list with indent and multi-line element`() =
            Given {
                listOf<Any>("line1\nline2")
            } when_ {
                joinMultiLine(indentation = 2)
            } then {
                expect(it.result is_ Equal to_ "{\n  line1\n  line2\n}")
            }
}