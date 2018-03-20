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

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.calls
import com.nhaarman.mockito_kotlin.inOrder
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.verify
import org.junit.jupiter.api.Test
import straightway.error.Panic
import straightway.expr.minus
import straightway.testing.bdd.Given
import straightway.testing.flow.False
import straightway.testing.flow.Not
import straightway.testing.flow.Throw
import straightway.testing.flow.True
import straightway.testing.flow.does
import straightway.testing.flow.expect
import straightway.testing.flow.is_

class EventTest {

    private interface Listener {
        fun onEvent(s: String)
    }

    private val test get() = Given {
        object {
            val sut = Event<String>()
            val listener = mock<Listener>()
        }
    }

    @Test
    fun `firing an event without listeners has no effect`() =
            test when_ { sut("Hello") } then {
                verify(listener, never()).onEvent(any())
            }

    @Test
    fun `firing an event with one listener notifies it on firing`() =
            test while_ {
                sut attach listener::onEvent
            } when_ {
                sut("Hello")
            } then {
                verify(listener).onEvent("Hello")
            }

    @Test
    fun `firing an event with the same listener twice notifies it twice on firing`() =
            test while_ {
                sut attach listener::onEvent
                sut attach listener::onEvent
            } when_ {
                sut("Hello")
            } then {
                inOrder(listener) {
                    verify(listener, calls(2)).onEvent("Hello")
                }
            }

    @Test
    fun `firing an event with a detached listener has no effect`() =
            test while_ {
                sut detach (sut attach listener::onEvent)
            } when_ {
                sut("Hello")
            } then {
                verify(listener, never()).onEvent(any())
            }

    @Test
    fun `detaching an unconnected handler has no effect`() =
            test when_ {
                sut attach listener::onEvent
            } then {
                expect({ it.result } does Not - Throw.exception)
            }

    @Test
    fun `detaching a lambda as handler via token`() =
            test while_ {
                val token = sut.attach { listener.onEvent(it) }
                sut detach token
            } when_ {
                sut("Hello")
            } then {
                verify(listener, never()).onEvent(any())
            }

    @Test
    fun `detach on not connected handler returns false`() =
            test when_ { sut detach EventHandlerToken() } then { expect(it.result is_ False) }

    @Test
    fun `detach on connected event returns true`() =
            test andGiven {
                object {
                    val sut = it.sut
                    val token = sut attach it.listener::onEvent
                }
            } when_ {
                sut detach token
            } then {
                expect(it.result is_ True)
            }

    @Test
    fun `attaching while event is executed is not considered in this execution`() =
            test while_ {
                sut attach { sut attach listener::onEvent}
            } when_ {
                sut("Hello")
            } then {
                verify(listener, never()).onEvent(any())
            }

    @Test
    fun `attaching while event is executed is considered in the next execution`() =
            test while_ {
                sut attach { sut attach listener::onEvent}
            } when_ {
                sut("Hello")
                sut("Hello")
            } then {
                verify(listener).onEvent("Hello")
            }

    @Test
    fun `recursive invocation throws`() =
            test while_ {
                var token = EventHandlerToken()
                token = sut attach { sut detach token; sut("Hello") }
            } when_ {
                sut("Hello")
            } then {
                expect({ it.result } does Throw.type<Panic>())
            }

    @Test
    fun `another invocation after first invocation threw works`() =
            test while_ {
                val token = sut attach { throw Panic("Aaaah!") }
                try { sut("Hello") } catch (e: Panic) {}
                sut detach token
            } when_ {
                sut("Hello")
            } then {
                expect({ it.result } does Not - Throw.exception)
            }
}