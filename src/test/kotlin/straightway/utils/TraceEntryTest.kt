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

class TraceEntryTest {

    private companion object{
        val stackTrace = StackTraceElement("class", "method", "file", 2)
    }

    @Test
    fun `toString with value and known level has defined format`() =
            Given {
                TraceEntry(stackTrace, TraceEvent.Enter, TraceLevel.Fatal, 83)
            } when_ {
                toString()
            } then {
                expect(it.result is_ Equal to_ "Fatal: $stackTrace enters: 83")
            }

    @Test
    fun `toString with value and unknown level has defined format`() =
            Given {
                TraceEntry(stackTrace, TraceEvent.Enter, TraceLevel.Unknown, 83)
            } when_ {
                toString()
            } then {
                expect(it.result is_ Equal to_ "$stackTrace enters: 83")
            }

    @Test
    fun `toString without value and known level has defined format`() =
            Given {
                TraceEntry(stackTrace, TraceEvent.Enter, TraceLevel.Fatal, null)
            } when_ {
                toString()
            } then {
                expect(it.result is_ Equal to_ "Fatal: $stackTrace enters")
            }

    @Test
    fun `toString without value and unknown level has defined format`() =
            Given {
                TraceEntry(stackTrace, TraceEvent.Enter, TraceLevel.Unknown, null)
            } when_ {
                toString()
            } then {
                expect(it.result is_ Equal to_ "$stackTrace enters")
            }
}