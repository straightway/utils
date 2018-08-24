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

import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.valueParameters
import kotlin.reflect.jvm.javaMethod

/**
 * Determine if the given function is a handler function matching the given RequestTypeSelector.
 */
fun KFunction<*>.isHandlerOf(selector: RequestTypeSelector) =
        returnType.classifier == Unit::class &&
        valueParameters.singleOrNull { it.type.selector() } != null

/**
 * Get all annotations for the given function, also recursively for all base classes.
 */
val KFunction<*>.recursiveAnnotations: Set<Annotation> get() =
        annotations.toSet() + fromSupertypes.flatMap { it.recursiveAnnotations }

private val KFunction<*>.fromSupertypes get() =
    javaMethod!!.declaringClass.kotlin.supertypes.mapNotNull {
        (it.classifier as? KClass<*>)?.getFunction(this)
    }

private fun KClass<*>.getFunction(function: KFunction<*>) =
    declaredFunctions.singleOrNull { declaredFunction ->
        declaredFunction.name == function.name &&
        declaredFunction.returnType == function.returnType &&
        declaredFunction.parameterTypes == function.parameterTypes
    }

private val KFunction<*>.parameterTypes get() = valueParameters.map { it.type }
