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
import straightway.error.Panic
import straightway.expr.minus
import straightway.testing.bdd.Given
import straightway.testing.flow.*

class InterceptorTest {

    @Test
    fun `invoke executes given function`() =
            Given {
                Interceptor("x")
            } when_ {
                this { 83 }
            } then {
                expect(it.result is_ Equal to_ 83)
            }

    @Test
    fun `invoke executes given function with receiver`() =
            Given {
                Interceptor("x")
            } when_ {
                this { expect(this is_ Equal to_ "x"); true }
            } then {
                expect(it.result is_ True)
            }

    @Test
    fun `construction with init function calls it with new Interceptor`() {
        lateinit var interceptorReceiver: Interceptor<String>
        val sut = Interceptor("x") { interceptorReceiver = this }
        expect(interceptorReceiver is_ Same as_ sut)
    }

    @Test
    fun `before is executed before the action`() {
        var isBeforeActionCalled = false
        var isActionCalled = false
        Given {
            Interceptor("x")
        } while_ {
            before { isBeforeActionCalled = true }
        } when_ {
            this {
                expect(isBeforeActionCalled is_ True)
                isActionCalled = true
            }
        } then {
            expect(isActionCalled is_ True)
        }
    }

    @Test
    fun `excpetion in before action aborts execution and throws the exception`() {
        var isActionCalled = false
        Given {
            Interceptor("x")
        } while_ {
            before { throw Panic("Panic") }
        } when_ {
            this {
                isActionCalled = true
            }
        } then {
            expect(isActionCalled is_ False)
            expect({ it.result } does Throw.type<Panic>())
        }
    }

    @Test
    fun `after is executed after the action in normal execution`() {
        var isAfterActionCalled = false
        var isActionCalled = false
        Given {
            Interceptor("x")
        } while_ {
            after { isAfterActionCalled = true }
        } when_ {
            this {
                expect(isAfterActionCalled is_ False)
                isActionCalled = true
            }
        } then {
            expect(isActionCalled is_ True)
            expect(isAfterActionCalled is_ True)
        }
    }

    @Test
    fun `after is executed after the action on exception`() {
        var isAfterActionCalled = false
        Given {
            Interceptor("x")
        } while_ {
            after { isAfterActionCalled = true }
        } when_ {
            this { throw Panic("Panic") }
        } then {
            expect(isAfterActionCalled is_ True)
            expect({ it.result } does Throw.exception)
        }
    }

    @Test
    fun `after is executed after the action on exception in before`() {
        var isAfterActionCalled = false
        Given {
            Interceptor("x")
        } while_ {
            before { throw Panic("Panic") }
            after { isAfterActionCalled = true }
        } when_ {
            this { }
        } then {
            expect(isAfterActionCalled is_ True)
            expect({ it.result } does Throw.exception)
        }
    }

    @Test
    fun `exception in after overrides exception in before`() =
            Given {
                Interceptor("x")
            } while_ {
                before { throw IllegalAccessException() }
                after { throw Panic("Panic in after") }
            } when_ {
                this { }
            } then {
                expect({ it.result } does Throw.type<Panic>())
            }

    @Test
    fun `exception in after overrides Exception in action`() =
            Given {
                Interceptor("x")
            } while_ {
                after { throw Panic("Panic in after") }
            } when_ {
                this { throw IllegalAccessException() }
            } then {
                expect({ it.result } does Throw.type<Panic>())
            }

    @Test
    fun `onException is executed on exception in action`() {
        var isOnExceptionCalled = false
        Given {
            Interceptor("x")
        } while_ {
            onException {
                expect(it is Panic)
                isOnExceptionCalled = true
            }
        } when_ {
            this { throw Panic("Panic") }
        } then {
            expect(isOnExceptionCalled is_ True)
            expect({ it.result } does Throw.type<Panic>())
        }
    }

    @Test
    fun `exception in after overrides exception in onException`() =
            Given {
                Interceptor("x")
            } while_ {
                onException { throw IllegalAccessException() }
                after { throw Panic("Panic") }
            } when_ {
                this { throw IllegalAccessException() }
            } then {
                expect({ it.result } does Throw.type<Panic>())
            }

    @Test
    fun `after is called with the code result`() =
            Given {
                Interceptor("x")
            } while_ {
                after { expect(it is_ Equal to_ 83) }
            } when_ {
                this { 83 }
            } then {
                expect({ it.result } does  Not - Throw.exception)
            }

    @Test
    fun `after gets null parameter after exception in before`() =
            Given {
                Interceptor("x")
            } while_ {
                before { throw Panic("Panic") }
                after { expect(it is_ Null) }
            } when_ {
                this { 83 }
            } then {
                expect({ it.result } does  Throw.type<Panic>())
            }

    @Test
    fun `after gets null parameter after exception in invoke`() =
            Given {
                Interceptor("x")
            } while_ {
                after { expect(it is_ Null) }
            } when_ {
                invoke<Int> { throw Panic("Panic") }
            } then {
                expect({ it.result } does  Throw.type<Panic>())
            }
}