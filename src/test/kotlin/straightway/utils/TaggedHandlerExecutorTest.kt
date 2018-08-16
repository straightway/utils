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
import straightway.testing.bdd.Given
import straightway.testing.flow.Empty
import straightway.testing.flow.Equal
import straightway.testing.flow.False
import straightway.testing.flow.True
import straightway.testing.flow.expect
import straightway.testing.flow.is_
import straightway.testing.flow.to_
import kotlin.reflect.full.memberFunctions

class TaggedHandlerExecutorTest {

    @Target(AnnotationTarget.FUNCTION)
    annotation class Tag

    interface IHandler {
        @Tag
        fun handlerFun(request: Int)

        @Tag
        fun inheritedTagFun(request: Double)
    }

    interface IOtherHandler {
        @Tag
        fun otherHandlerFun()
    }

    open class BaseHandler {
        @Tag
        fun baseHandlerFun() {}
    }

    class OtherHandler : IOtherHandler {
        override fun otherHandlerFun() {
            throw Panic("Do not call!")
        }
    }

    class Handler : BaseHandler(), IHandler, IOtherHandler by OtherHandler() {
        var receivedRequests = listOf<Int>()

        fun funWithoutTags(i: Int) {
            throw Panic("Do not call (parameter: $i)!")
        }

        fun funWithReturnValue(i: Int): Int {
            throw Panic("Do not call (parameter: $i)!")
        }

        override fun handlerFun(request: Int) {
            receivedRequests += request
        }

        override fun inheritedTagFun(request: Double) {}

        @Tag
        fun handlerFunWithOtherType(request: Double) {
            throw Panic("Do not call (parameter: $request)!")
        }
    }

    @Test
    fun `getHandlers yields executable handler`() =
            Given {
                Handler()
            } when_ {
                this.getHandlers<Tag>(Int::class).single()(83)
            } then {
                expect(receivedRequests is_ Equal to_ listOf(83))
            }

    @Test
    fun `isHandlerOf yields true for function with matching parameters`() =
            Given {
                getHandlerFunction("funWithoutTags")
            } when_ {
                isHandlerOf(Int::class)
            } then {
                expect(it.result is_ True)
            }

    @Test
    fun `isHandlerOf yields false for function with not matching parameters`() =
            Given {
                getHandlerFunction("funWithoutTags")
            } when_ {
                isHandlerOf(Double::class)
            } then {
                expect(it.result is_ False)
            }

    @Test
    fun `isHandlerOf yields false for function with return value`() =
            Given {
                getHandlerFunction("funWithReturnValue")
            } when_ {
                isHandlerOf(Int::class)
            } then {
                expect(it.result is_ False)
            }

    @Test
    fun `recursiveAnnotations yields empty list if noting is annotated`() =
            Given {
                getHandlerFunction("funWithoutTags")
            } when_ {
                recursiveAnnotations
            } then {
                expect(it.result is_ Empty)
            }

    @Test
    fun `recursiveAnnotations returns directly annotated annotations`() =
            Given {
                getHandlerFunction("handlerFunWithOtherType")
            } when_ {
                recursiveAnnotations
            } then {
                expect(it.result.single() is Tag)
            }

    @Test
    fun `recursiveAnnotations returns annotations from base interfaces`() =
            Given {
                getHandlerFunction("inheritedTagFun")
            } when_ {
                recursiveAnnotations
            } then {
                expect(it.result.single() is Tag)
            }

    @Test
    fun `recursiveAnnotations returns annotations from mixin interfaces`() =
            Given {
                getHandlerFunction("otherHandlerFun")
            } when_ {
                recursiveAnnotations
            } then {
                expect(it.result.single() is Tag)
            }

    @Test
    fun `recursiveAnnotations returns annotations from base class`() =
            Given {
                getHandlerFunction("baseHandlerFun")
            } when_ {
                recursiveAnnotations
            } then {
                expect(it.result.single() is Tag)
            }

    private fun getHandlerFunction(name: String) =
            Handler::class.memberFunctions.single { it.name == name }
}