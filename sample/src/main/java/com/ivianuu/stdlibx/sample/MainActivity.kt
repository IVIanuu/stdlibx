/*
 * Copyright 2018 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.stdlibx.sample

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.ivianuu.stdlibx.dropUntil
import com.ivianuu.stdlibx.takeUntil

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val list = listOf(1, 2, 3, 4, 5)
        val predicate: (Int) -> Boolean = { it < 3 }

        d { "take until ${list.takeUntil(predicate)}" }
        d { "take while ${list.takeWhile(predicate)}" }
        d { "drop until ${list.dropUntil(predicate)}" }
        d { "drop while ${list.dropWhile(predicate)}" }
    }

    private fun d(m: () -> String) {
        Log.d("testt", m())
    }
}
