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

import com.nhaarman.mockito_kotlin.mock
import org.junit.jupiter.api.Test
import straightway.error.Panic
import straightway.testing.bdd.Given
import straightway.testing.bdd.WhenResult
import straightway.testing.flow.*
import java.time.LocalDateTime
import kotlin.reflect.KCallable

class BufferTracerTest {

    private class Tester {
        val currentTime = LocalDateTime.of(2000, 1, 2, 3, 4, 5)!!
        val timeProvider = mock<TimeProvider> { on { now }.thenAnswer { currentTime } }
        val tracer = BufferTracer(timeProvider)
        var traced = tracer.traces
        fun callTestReturn(value: Int) = testReturn(value)
        fun testReturn(value: Int) = tracer(value) { value + 1 }
        fun callTestPanic() = testPanic()
        fun testPanic(): Int = tracer { throw Panic("Panic") }
        fun callTestTrace(level: TraceLevel, message: String) = testTrace(level, message)
        fun testTrace(level: TraceLevel, message: String) = tracer {
            traceMessage(level) { message }
        }
        fun nestedCall() = tracer { testReturn(83) }
        fun nestedPanic() = tracer { try { testPanic() } catch(e: Panic) {} }
    }

    private val test = Given { Tester() }

    @Test
    fun `result is traced`() {
        test when_ {
            callTestReturn(83)
        } then {
            assertEventSequence(
                    { assertCallingEvent(Tester::callTestReturn) },
                    { assertEnterEvent(Tester::testReturn, 83) },
                    { assertReturnEvent(Tester::testReturn, 84) })
        }
    }

    @Test
    fun `multiple events are traced`() {
        test when_ {
            callTestReturn(83)
            callTestReturn(83)
        } then {
            assertEventSequence(
                    { assertCallingEvent(Tester::callTestReturn) },
                    { assertEnterEvent(Tester::testReturn, 83) },
                    { assertReturnEvent(Tester::testReturn, 84) },
                    { assertCallingEvent(Tester::callTestReturn) },
                    { assertEnterEvent(Tester::testReturn, 83) },
                    { assertReturnEvent(Tester::testReturn, 84) })
        }
    }

    @Test
    fun `exception is traced`() {
        test when_ {
            callTestPanic()
        } then {
            assertExceptionHasBeenThrown(it)
            assertEventSequence(
                    { assertCallingEvent(Tester::callTestPanic) },
                    { assertEnterEvent(Tester::testPanic) },
                    { assertPanic() })
        }
    }

    @Test
    fun `returning null in onTrace interceptor disables tracing enter and return events`() =
            test while_ {
                tracer.onTrace { null }
            } when_ {
                testReturn(83)
            } then {
                expect(traced is_ Empty)
            }

    @Test
    fun `returning null in onTrace interceptor disables tracing exception events`() =
            test while_ {
                tracer.onTrace { null }
            } when_ {
                testPanic()
            } then {
                assertExceptionHasBeenThrown(it)
                expect(traced is_ Empty)
            }

    @Test
    fun `returning null in onTrace interceptor disables tracing message events`() =
            test while_ {
                tracer.onTrace { null }
            } when_ {
                testTrace(TraceLevel.Info, "Hello")
            } then {
                expect(traced is_ Empty)
            }

    @Test
    fun `clear removes all traces`() =
            test while_ {
                testReturn(83)
            } when_ {
                tracer.clear()
            } then {
                expect(traced is_ Empty)
            }

    @Test
    fun `trace traces a message`() =
            test when_ {
                callTestTrace(TraceLevel.Debug, "Hello World")
            } then {
                lateinit var callTraceEntry: StackTraceElement
                lateinit var enterTraceEntry: StackTraceElement
                assertEventSequence(
                        {
                            callTraceEntry = stackTraceElement
                            assertCallingEvent(Tester::callTestTrace)
                        },
                        {
                            expect(stackTraceElement.fileName is_ Equal to_ callTraceEntry.fileName)
                            expect(stackTraceElement.lineNumber is_ Equal to_ callTraceEntry.lineNumber + 1)
                            enterTraceEntry = stackTraceElement
                            assertEnterEvent(Tester::testTrace)
                        },
                        {
                            expect(stackTraceElement.fileName is_ Equal to_ enterTraceEntry.fileName)
                            expect(stackTraceElement.lineNumber is_ Equal to_ enterTraceEntry.lineNumber + 1)
                            expect(event is_ Equal to_ TraceEvent.Message)
                            expect(level is_ Equal to_ TraceLevel.Debug)
                            expect(value is_ Equal to_ "Hello World")
                        },
                        { assertReturnEvent(Tester::testTrace) })
            }

    @Test
    fun `first call, entry and return events have nesting level 0`() =
            test when_ {
                callTestReturn(83)
            } then {
                traced.map { it as TraceEntry }.forEach {
                    expect(it.nestingLevel is_ Equal to_ 0)
                }

            }

    @Test
    fun `intermediate trace event has nesting level 1`() =
            test when_ {
                callTestTrace(TraceLevel.Debug, "Hello")
            } then {
                traced.map { it as TraceEntry }.filter { it.event == TraceEvent.Message }.forEach {
                    expect(it.nestingLevel is_ Equal to_ 1)
                }
            }

    @Test
    fun `exception trace event has nesting level 1`() =
            test when_ {
                nestedPanic()
            } then {
                expect(traced.map { (it as TraceEntry).nestingLevel } is_
                        Equal to_ Values(0, 0, 1, 1, 2, 0))
            }

    @Test
    fun `trace entry has thread id`() =
            test when_ {
                callTestTrace(TraceLevel.Debug, "Hello")
            } then {
                val currentThreadId = Thread.currentThread().id
                traced.forEach {
                    expect((it as TraceEntry).threadId is_ Equal to_ currentThreadId)
                }
            }

    @Test
    fun `exception trace entry has thread id`() =
            test when_ {
                callTestPanic()
            } then {
                assertExceptionHasBeenThrown(it)
                val currentThreadId = Thread.currentThread().id
                traced.forEach {
                    expect((it as TraceEntry).threadId is_ Equal to_ currentThreadId)
                }
            }

    @Test
    fun `nested call has nesting level 1`() =
            test when_ {
                nestedCall()
            } then {
                expect(traced.map { (it as TraceEntry).nestingLevel } is_
                        Equal to_ Values(0, 0, 1, 1, 1, 0))

            }

    @Test
    fun `call contains time stamp`() =
            test when_ {
                callTestReturn(83)
            } then {
                expect(traced.all { (it as TraceEntry).timeStamp == currentTime } )
            }

    private companion object {

        fun assertExceptionHasBeenThrown(test: WhenResult<*>) {
            expect({ test.result } does Throw.exception)
        }

        fun Tester.assertEventSequence(vararg asserts: TraceEntry.() -> Unit) {
            expect(traced has Size of asserts.size)
            asserts.forEachIndexed { index, assert ->
                @Suppress("UNCHECKED_CAST")
                (traced[index] as TraceEntry).assert()
            }
        }

        fun TraceEntry.assertPanic() {
            assertFunctionCall(Tester::testPanic)
            expect(level is_ Equal to_ TraceLevel.Unknown)
            expect(event is_ Equal to_ TraceEvent.Exception)
            expect(value is Panic) { "Unexpected exception type: $value" }
        }

        fun TraceEntry.assertCallingEvent(method: KCallable<*>) {
            assertFunctionCall(method)
            expect(level is_ Equal to_ TraceLevel.Unknown)
            expect(event is_ Equal to_ TraceEvent.Calling)
            expect(value is_ Null)
        }

        fun TraceEntry.assertEnterEvent(method: KCallable<*>, vararg params: Any? ) {
            assertFunctionCall(method)
            expect(level is_ Equal to_ TraceLevel.Unknown)
            expect(event is_ Equal to_ TraceEvent.Enter)
            expect(value is_ Equal to_ params)
        }

        fun TraceEntry.assertReturnEvent(method: KCallable<*>, returnValue: Any = Unit) {
            assertFunctionCall(method)
            expect(level is_ Equal to_ TraceLevel.Unknown)
            expect(event is_ Equal to_ TraceEvent.Return)
            expect(value is_ Equal to_ returnValue)
        }

        fun TraceEntry.assertFunctionCall(method: KCallable<*>) {
            expect(stackTraceElement.isTestFunCall(method)) { "Unexpected function call: $stackTraceElement" }
        }

        fun StackTraceElement.isTestFunCall(method: KCallable<*>) =
                className.startsWith(Tester::class.java.name) && methodName == method.name
    }
}