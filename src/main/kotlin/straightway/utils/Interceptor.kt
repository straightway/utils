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
 * Execute custom actions before and after the code passed to the invoke method. Also execute custom
 * actions when that code throws an excpetion.
 */
class Interceptor<TReceiver>(private val receiver: TReceiver) {

    private var beforeAction: TReceiver.() -> Unit = {}
    private var afterAction: TReceiver.(result: Any?) -> Unit = {}
    private var onExceptionAction: TReceiver.(Throwable) -> Unit = {}

    constructor(receiver: TReceiver, init: Interceptor<TReceiver>.() -> Unit) : this(receiver) { init() }

    fun before(action: TReceiver.() -> Unit) { beforeAction = action }

    fun after(action: TReceiver.(result: Any?) -> Unit) { afterAction = action }

    fun onException(action: TReceiver.(Throwable) -> Unit) { onExceptionAction = action }

    operator fun <TResult> invoke(action: TReceiver.() -> TResult): TResult {
        var result: Any? = null
        try {
            receiver.beforeAction()
            result = receiver.action()
            return result;
        } catch (ex: Throwable) {
            receiver.onExceptionAction(ex)
            throw ex
        } finally {
            receiver.afterAction(result)
        }
    }
}