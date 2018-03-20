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

import straightway.error.Panic

/**
 * Event mechanism allowing attaching and detaching handlers as functions or lambdas,
 * and invocation with notification of all attached handlers.
 */
class Event<T> : EventRegistry<T>, EventTrigger<T> {

    override fun invoke(e: T) {
        if (_isInvocationInProgress) throw Panic("Recursive event invocation")
        _isInvocationInProgress = true
        try {
            _handlers.toList().forEach { it.handler(e) }
        } finally {
            _isInvocationInProgress = false
        }
    }

    override infix fun attach(handler: (T) -> Unit) : EventHandlerToken {
        val token = EventHandlerToken()
        _handlers += HandlerEntry(token, handler)
        return token
    }

    override infix fun detach(token: EventHandlerToken) =
        _handlers.removeIf { it.token === token }

    private data class HandlerEntry<T>(val token: EventHandlerToken, val handler: (T) -> Unit)
    private val _handlers = mutableListOf<HandlerEntry<T>>()
    private var _isInvocationInProgress = false
}