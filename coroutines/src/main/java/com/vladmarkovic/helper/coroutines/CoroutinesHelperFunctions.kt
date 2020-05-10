package com.vladmarkovic.helper.coroutines

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay as coroutinesDelay
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

/**
 * Created by Vlad Markovic on 10 May 2020.
 */


suspend fun <T> onMain(action: (() -> T)?) = withContext(Main) { action?.invoke() }

fun <T> onMainBlocking(action: (() -> T)?) = runBlocking(Main) { action?.invoke() }


// region delay
fun <T> delay(millis: Long, action: () -> T): Job = delay(Default, millis, action)

fun <T> (() -> T).delay(millis: Long): Job = delay(millis, this)

fun <T> delay(dispatcher: CoroutineDispatcher,
              millis: Long,
              action: () -> T): Job =
    CoroutineScope(dispatcher).launch(dispatcher, CoroutineStart.DEFAULT) {
        coroutinesDelay(millis)
        action()
    }


fun <T> delayCallOnMain(millis: Long, action: () -> T): Job = delayCallOnMain(Default, millis, action)

fun <T> (() -> T).delayCallOnMain(millis: Long): Job = delayCallOnMain(millis, this)

fun <T> delayCallOnMain(dispatcher: CoroutineDispatcher,
                        millis: Long,
                        action: () -> T): Job =
    CoroutineScope(dispatcher).launch(dispatcher, CoroutineStart.DEFAULT) {
        coroutinesDelay(millis)
        onMain { action() }
    }
// endregion delay

// region launch
fun <T> launch(suspended: suspend CoroutineScope.() -> T): Job = launch(Default, suspended)

fun <T> launch(dispatcher: CoroutineDispatcher, suspended: suspend CoroutineScope.() -> T): Job =
    CoroutineScope(dispatcher).launch(dispatcher, CoroutineStart.DEFAULT) {
        suspended.invoke(this)
    }

fun <T> launch(suspended: suspend CoroutineScope.() -> T,
               onError: ((error: Throwable) -> Any?)? = null): Job = launch(Default, null, suspended, onError)

fun <T> launch(dispatcher: CoroutineDispatcher = Default,
               initially: (() -> Unit)? = null,
               suspended: suspend CoroutineScope.() -> T,
               onError: ((error: Throwable) -> Any?)? = null,
               finally: (() -> Unit)? = null): Job {
    initially?.invoke()

    return CoroutineScope(dispatcher).launch(getCoroutineExceptionHandler(onError, finally)) {
        suspended.invoke(this)
        finally?.invoke()
    }
}
// endregion launch

// region launchHandleOnMain
fun <T> launchHandleOnMain(suspended: suspend CoroutineScope.() -> T,
                           onError: ((error: Throwable) -> Any?)? = null): Job =
    launchHandleOnMain(Default, null, suspended, onError)

/**
 * initially() block will run on the same thread where coroutine was launched from.
 */
fun <T> launchHandleOnMain(dispatcher: CoroutineDispatcher = Default,
                           initially: (() -> Any?)? = null,
                           suspended: suspend CoroutineScope.() -> T,
                           onError: ((error: Throwable) -> Any?)? = null,
                           finally: (() -> Any?)? = null): Job {
    initially?.invoke()

    return CoroutineScope(dispatcher).launch(getCoroutineExceptionOnMainHandler(onError, finally)) {
        suspended.invoke(this)
        onMain(finally)
    }
}
// endregion launchHandleOnMain

// region nullableLaunch
fun <T> nullableLaunch(suspended: suspend CoroutineScope.() -> T?): Job = nullableLaunch(Default, suspended)

fun <T> nullableLaunch(dispatcher: CoroutineDispatcher, suspended: suspend CoroutineScope.() -> T?): Job =
    CoroutineScope(dispatcher).launch(dispatcher, CoroutineStart.DEFAULT) {
        suspended.invoke(this)
    }

fun <T> nullableLaunch(suspended: suspend CoroutineScope.() -> T?,
                       onError: ((error: Throwable) -> Any?)? = null): Job =
    nullableLaunch(Default, null, suspended, onError)

/**
 * initially() block will run on the same thread where coroutine was launched from.
 */
fun <T> nullableLaunch(dispatcher: CoroutineDispatcher = Default,
                       initially: (() -> Any?)? = null,
                       suspended: suspend CoroutineScope.() -> T?,
                       onError: ((error: Throwable) -> Any?)? = null,
                       finally: (() -> Any?)? = null): Job {
    initially?.invoke()

    return CoroutineScope(dispatcher).launch(getCoroutineExceptionHandler(onError, finally)) {
        suspended.invoke(this)
        finally?.invoke()
    }
}
// endregion nullableLaunch

// region nullableLaunchHandleOnMain
fun <T> nullableLaunchHandleOnMain(suspended: suspend CoroutineScope.() -> T?): Job =
    nullableLaunchHandleOnMain(Default, suspended)

fun <T> nullableLaunchHandleOnMain(dispatcher: CoroutineDispatcher, suspended: suspend CoroutineScope.() -> T?): Job =
    CoroutineScope(dispatcher).launch(dispatcher, CoroutineStart.DEFAULT) {
        suspended.invoke(this)
    }

fun <T> nullableLaunchHandleOnMain(suspended: suspend CoroutineScope.() -> T?,
                                   onError: ((error: Throwable) -> Any?)? = null): Job =
    nullableLaunchHandleOnMain(Default, null, suspended, onError)

/**
 * initially() block will run on the same thread where coroutine was launched from.
 */
fun <T> nullableLaunchHandleOnMain(dispatcher: CoroutineDispatcher = Default,
                                   initially: (() -> Any?)? = null,
                                   suspended: suspend CoroutineScope.() -> T?,
                                   onError: ((error: Throwable) -> Any?)? = null,
                                   finally: (() -> Any?)? = null): Job {
    initially?.invoke()

    return CoroutineScope(dispatcher).launch(getCoroutineExceptionOnMainHandler(onError, finally)) {
        suspended.invoke(this)
        onMain(finally)
    }
}
// endregion nullableLaunchHandleOnMain

// region async
fun <T> async(suspended: suspend CoroutineScope.() -> T): Deferred<T> = async(Default, suspended)

fun <T> async(dispatcher: CoroutineDispatcher, suspended: suspend CoroutineScope.() -> T): Deferred<T> =
    CoroutineScope(dispatcher).async {
        suspended.invoke(this)
    }

fun <T> async(suspended: suspend CoroutineScope.() -> T,
              onError: ((error: Throwable) -> Any?)? = null): Job = async(Default, null, suspended, onError)

fun <T> async(dispatcher: CoroutineDispatcher = Default,
              initially: (() -> Unit)? = null,
              suspended: suspend CoroutineScope.() -> T,
              onError: ((error: Throwable) -> Any?)? = null,
              finally: (() -> Unit)? = null): Job {
    initially?.invoke()

    return CoroutineScope(dispatcher).async(getCoroutineExceptionHandler(onError, finally)) {
        suspended.invoke(this)
        finally?.invoke()
    }
}
// endregion async

// region asyncHandleOnMain
fun <T> asyncHandleOnMain(suspended: suspend CoroutineScope.() -> T,
                          onError: ((error: Throwable) -> Any?)? = null): Job =
    asyncHandleOnMain(Default, null, suspended, onError)

/**
 * initially() block will run on the same thread where coroutine was launched from.
 */
fun <T> asyncHandleOnMain(dispatcher: CoroutineDispatcher = Default,
                          initially: (() -> Any?)? = null,
                          suspended: suspend CoroutineScope.() -> T,
                          onError: ((error: Throwable) -> Any?)? = null,
                          finally: (() -> Any?)? = null): Job {
    initially?.invoke()

    return CoroutineScope(dispatcher).async(getCoroutineExceptionOnMainHandler(onError, finally)) {
        suspended.invoke(this)
        onMain(finally)
    }
}
// endregion asyncHandleOnMain

// region nullableAsync
fun <T> nullableAsync(suspended: suspend CoroutineScope.() -> T?): Deferred<T?> = nullableAsync(Default, suspended)

fun <T> nullableAsync(dispatcher: CoroutineDispatcher, suspended: suspend CoroutineScope.() -> T?): Deferred<T?> =
    CoroutineScope(dispatcher).async(dispatcher, CoroutineStart.DEFAULT) {
        suspended.invoke(this)
    }

fun <T> nullableAsync(suspended: suspend CoroutineScope.() -> T?,
                      onError: ((error: Throwable) -> Any?)? = null): Job = nullableAsync(Default, null, suspended, onError)

/**
 * initially() block will run on the same thread where coroutine was launched from.
 */
fun <T> nullableAsync(dispatcher: CoroutineDispatcher = Default,
                      initially: (() -> Any?)? = null,
                      suspended: suspend CoroutineScope.() -> T?,
                      onError: ((error: Throwable) -> Any?)? = null,
                      finally: (() -> Any?)? = null): Job {
    initially?.invoke()

    return CoroutineScope(dispatcher).async(getCoroutineExceptionHandler(onError, finally)) {
        suspended.invoke(this)
        finally?.invoke()
    }
}
// endregion nullableAsync

// region nullableAsyncHandleOnMain
fun <T> nullableAsyncHandleOnMain(suspended: suspend CoroutineScope.() -> T?): Job =
    nullableAsyncHandleOnMain(Default, suspended)

fun <T> nullableAsyncHandleOnMain(dispatcher: CoroutineDispatcher, suspended: suspend CoroutineScope.() -> T?): Job =
    CoroutineScope(dispatcher).async(dispatcher, CoroutineStart.DEFAULT) {
        suspended.invoke(this)
    }

fun <T> nullableAsyncHandleOnMain(suspended: suspend CoroutineScope.() -> T?,
                                  onError: ((error: Throwable) -> Any?)? = null): Job =
    nullableAsyncHandleOnMain(Default, null, suspended, onError)

/**
 * initially() block will run on the same thread where coroutine was launched from.
 */
fun <T> nullableAsyncHandleOnMain(dispatcher: CoroutineDispatcher = Default,
                                  initially: (() -> Any?)? = null,
                                  suspended: suspend CoroutineScope.() -> T?,
                                  onError: ((error: Throwable) -> Any?)? = null,
                                  finally: (() -> Any?)? = null): Job {
    initially?.invoke()

    return CoroutineScope(dispatcher).async(getCoroutineExceptionOnMainHandler(onError, finally)) {
        suspended.invoke(this)
        onMain(finally)
    }
}
// endregion nullableAsyncHandleOnMain

private fun getCoroutineExceptionHandler(onError: ((error: Throwable) -> Any?)? = null,
                                         finally: (() -> Any?)? = null): CoroutineExceptionHandler =
    CoroutineExceptionHandler { _, exception ->
        onError?.invoke(exception)
        finally?.invoke()
    }

private fun getCoroutineExceptionOnMainHandler(onError: ((error: Throwable) -> Any?)? = null,
                                               finally: (() -> Any?)? = null): CoroutineExceptionHandler =
    CoroutineExceptionHandler { _, exception ->
        onMainBlocking {
            onError?.invoke(exception)
            finally?.invoke()
        }
    }