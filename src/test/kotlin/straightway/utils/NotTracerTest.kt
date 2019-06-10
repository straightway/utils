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
import straightway.testing.flow.*

class NotTracerTest {

    private val test get () = Given { NotTracer() }

    @Test
    fun `traces is empty`() =
            test when_ {
                traces
            } then {
                expect(it.result is_ Empty)
            }

    @Test
    fun `clear has no effect`() =
            test when_ {
                clear()
            } then {
                expect({ it.result } does Not - Throw.exception)
            }

    @Test
    fun `onTrace has no effect`() =
            test when_ {
                onTrace { }
            } then {
                expect({ it.result } does Not - Throw.exception)
            }

    @Test
    fun `trace has no effect`() =
            test when_ {
                trace(TraceLevel.Info) {"Message" }
            } then {
                expect({ it.result } does Not - Throw.exception)
            }

    @Test
    fun `invoke invokes action`() =
            test when_ {
                this { 83 }
            } then {
                expect(it.result is_ Equal to_ 83)
            }
}