[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

## Coroutines Helper Functions - small Kotlin library

```groovy
implementation "com.vladmarkovic.helper:coroutines:$version"
```

This is a small set of helper functions which enable writing and handling coroutines in a nice readable way such as:

```kotlin
fun nicelyWrittenCoroutine() {
    launchHandleOnMain(
        initially = ::showLoadingIndicator,
        suspended = {
            val result = doSomeAsyncProcessing()
            onMain { showResult(result) }
        },
        onError = ::showError,
        finally = ::hideLoadingIndicator
    )
}
```

In the above example `initially`, `onError` and `finally` are called on main thread, while suspended function executes on `Dispatchers.Default`  
by default, though we can also specify explicitly which the Dispatcher to use.

```kotlin
fun simpleLaunchCoroutine() {
    launch { doSomethingSuspended() }
}
```

```kotlin
fun anotherSimpleLaunchCoroutine() {
    launch(Dispatchers.IO) { doSomethingSuspended() }
}
```

```kotlin
fun oneMoreSimpleLaunchCoroutine() {
    launchHandleOnMain({
        doSomethingSuspended()
    }, { throwable ->
        showError(throwable.message)
    })
}
```

```kotlin
fun andOneMoreSimpleLaunchCoroutine() {
    launch({ doSomethingSuspended() }, { Log.e(it) })
}
```

________________________________________________________________________

## License
```
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```