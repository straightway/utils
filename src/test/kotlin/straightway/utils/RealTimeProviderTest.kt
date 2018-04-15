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
import straightway.testing.flow.Less
import straightway.testing.flow.expect
import straightway.testing.flow.is_
import straightway.testing.flow.than
import java.time.Duration
import java.time.LocalDateTime

class RealTimeProviderTest {

    @Test
    fun `returned date time is about the current time`() =
            Given {
                RealTimeProvider()
            } when_ {
                currentTime
            } then {
                expect(Duration.between(it.result, LocalDateTime.now())
                               is_ Less than Duration.ofSeconds(1))
            }
}