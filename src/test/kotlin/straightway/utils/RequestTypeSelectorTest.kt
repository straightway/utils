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
import straightway.testing.flow.False
import straightway.testing.flow.True
import straightway.testing.flow.expect
import straightway.testing.flow.is_
import kotlin.reflect.KTypeProjection
import kotlin.reflect.KVariance
import kotlin.reflect.full.createType
import kotlin.reflect.full.starProjectedType

class RequestTypeSelectorTest {

    @Test
    fun `isClass yields true if class is matched`() =
            Given {
                isClass(String::class)
            } when_ {
                String::class.createType().this()
            } then {
                expect(it.result is_ True)
            }

    @Test
    fun `isClass yields false if class is not matched`() =
            Given {
                isClass(Int::class)
            } when_ {
                String::class.createType().this()
            } then {
                expect(it.result is_ False)
            }

    @Test
    fun `isGeneric for yields true for match`() =
            Given {
                isGeneric(MyGeneric::class, isClass(Int::class))
            } when_ {
                MyGeneric.typeWith<Int>().this()
            } then {
                expect(it.result is_ True)
            }

    @Test
    fun `isGeneric yields false for different generic arguments`() =
            Given {
                isGeneric(MyGeneric::class, isClass(Int::class))
            } when_ {
                MyGeneric.typeWith<String>().this()
            } then {
                expect(it.result is_ False)
            }

    @Test
    fun `isGeneric yields false for too few generic arguments`() =
            Given {
                isGeneric(MyGeneric::class)
            } when_ {
                MyGeneric.typeWith<String>().this()
            } then {
                expect(it.result is_ False)
            }

    @Test
    fun `isGeneric yields false for too many generic arguments`() =
            Given {
                isGeneric(MyGeneric::class, isClass(String::class), isClass(String::class))
            } when_ {
                MyGeneric.typeWith<String>().this()
            } then {
                expect(it.result is_ False)
            }

    @Test
    fun `isGeneric yields false for wrong generic class`() =
            Given {
                isGeneric(OtherGeneric::class, isClass(String::class))
            } when_ {
                MyGeneric.typeWith<String>().this()
            } then {
                expect(it.result is_ False)
            }

    @Suppress("unused", "UtilityClassWithPublicConstructor")
    private class MyGeneric<T> {
        companion object {
            inline fun <reified T> typeWith() =
                    MyGeneric::class.createType(arguments = listOf(
                            KTypeProjection(KVariance.INVARIANT,
                                            T::class.starProjectedType)))
        }
    }

    @Suppress("unused")
    private class OtherGeneric<T>
}