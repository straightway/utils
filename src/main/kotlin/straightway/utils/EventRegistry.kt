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

/**
 * Allow attaching to and detaching from an event. To detach, the event token
 * returned by the attach must be provided to identify the connection.
 */
interface EventRegistry<T> {
    infix fun attach(handler: (T) -> Unit): EventHandlerToken
    infix fun detach(token: EventHandlerToken): Boolean
}

fun <T> EventRegistry<T>.handleOnce(handler: (T) -> Unit): EventHandlerToken {
    lateinit var eventHandlerToken: EventHandlerToken
    eventHandlerToken = attach { handler(it); detach(eventHandlerToken) }
    return eventHandlerToken
}
