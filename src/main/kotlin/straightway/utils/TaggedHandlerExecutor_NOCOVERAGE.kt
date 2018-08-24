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

import kotlin.reflect.full.functions

/**
 * Get all handler functions as callable lambdas of the target object being annotated
 * with the TTag annotation and matching the given request type selector.
 */
inline fun <reified TTag : Annotation> Any.getHandlers(noinline selector: RequestTypeSelector) =
        getHandlerFunctionsFor<TTag>(selector)
                .map { { request: Any -> it.call(this, request); Unit } }

/**
 * Get all handler functions of the target object being annotated with the TTag annotation
 * and having a parameter matching the given request type selector.
 */
inline fun <reified TTag : Annotation> Any.getHandlerFunctionsFor(
        noinline selector: RequestTypeSelector
) =
        this::class.functions
                .filter { fn -> fn.recursiveAnnotations.any { it is TTag } }
                .filter { it.isHandlerOf(selector) }